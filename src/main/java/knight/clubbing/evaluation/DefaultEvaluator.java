package knight.clubbing.evaluation;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BBoardHelper;
import knight.clubbing.core.BPiece;
import knight.clubbing.core.PopLsbResult;
import knight.clubbing.movegen.PrecomputedMoveData;
import knight.clubbing.movegen.magic.Magic;

import static knight.clubbing.core.BBoardHelper.FILE_MASKS;
import static knight.clubbing.evaluation.EvalParams.*;

public class DefaultEvaluator implements Evaluator {

    private static final int PAWN_PHASE = 0;
    private static final int KNIGHT_PHASE = 1;
    private static final int BISHOP_PHASE = 1;
    private static final int ROOK_PHASE = 2;
    private static final int QUEEN_PHASE = 4;
    private static final int TOTAL_PHASE =
            PAWN_PHASE * 16
            + KNIGHT_PHASE * 4
            + BISHOP_PHASE * 4
            + ROOK_PHASE * 4
            + QUEEN_PHASE * 2; // = 24

    private static final PrecomputedMoveData moveData = PrecomputedMoveData.getInstance();

    private static final long[] FILE_FILL_MASKS = new long[8];
    private static final long[] ADJACENT_FILE_MASKS = new long[8];
    private static final long[] WHITE_AHEAD_MASKS = new long[64];
    private static final long[] BLACK_AHEAD_MASKS = new long[64];
    private static final long[][] PASSED_PAWN_MASKS = new long[2][64];
    private static final long[][] PAWN_SUPPORT_MASKS = new long[2][64];

    static {
        for (int file = 0; file < 8; file++) {
            long fileMask = 0L;
            for (int rank = 0; rank < 8; rank++) {
                fileMask |= (1L << (rank * 8 + file));
            }
            FILE_FILL_MASKS[file] = fileMask;

            long adjacent = 0L;
            if (file > 0) adjacent |= FILE_FILL_MASKS[file - 1];
            if (file < 7) adjacent |= FILE_FILL_MASKS[file + 1];
            ADJACENT_FILE_MASKS[file] = adjacent;
        }

        for (int square = 0; square < 64; square++) {
            int file = BBoardHelper.fileIndex(square);
            int rank = BBoardHelper.rankIndex(square);

            // White ahead mask: all squares on same file, ranks above
            long whiteAhead = 0L;
            for (int r = rank + 1; r < 8; r++) {
                whiteAhead |= (1L << (r * 8 + file));
            }
            WHITE_AHEAD_MASKS[square] = whiteAhead;

            // Black ahead mask: all squares on same file, ranks below
            long blackAhead = 0L;
            for (int r = rank - 1; r >= 0; r--) {
                blackAhead |= (1L << (r * 8 + file));
            }
            BLACK_AHEAD_MASKS[square] = blackAhead;

            // Build passed pawn masks (current file + adjacent files, all ahead)
            long fileMask = FILE_MASKS[file];
            long adjacentFiles = 0L;
            if (file > 0) adjacentFiles |= FILE_MASKS[file - 1];
            if (file < 7) adjacentFiles |= FILE_MASKS[file + 1];
            long frontSpan = fileMask | adjacentFiles;

            // White: front span for all ranks above this square
            PASSED_PAWN_MASKS[0][square] = frontSpan & -(1L << ((rank + 1) * 8));

            // Black: front span for all ranks below this square
            PASSED_PAWN_MASKS[1][square] = frontSpan & ((1L << (rank * 8)) - 1);

            // Pawn support white
            if (rank > 0) {
                if (file > 0) PAWN_SUPPORT_MASKS[0][square] |= (1L << ((rank - 1) * 8 + file - 1));
                if (file < 7) PAWN_SUPPORT_MASKS[0][square] |= (1L << ((rank - 1) * 8 + file + 1));
            }

            // Pawn support black
            if (rank < 7) {
                if (file > 0) PAWN_SUPPORT_MASKS[1][square] |= (1L << ((rank + 1) * 8 + file - 1));
                if (file < 7) PAWN_SUPPORT_MASKS[1][square] |= (1L << ((rank + 1) * 8 + file + 1));
            }
        }
    }


    @Override
    public int evaluate(BBoard board, EvalParams params) {
        int mgScore = 0;
        int egScore = 0;
        int phase = gamePhase(board);

        int materialScore = material(board, params);
        mgScore += mgScore(materialScore);
        egScore += egScore(materialScore);

        int pstWhite = pst(board, BPiece.white, params);
        int pstBlack = pst(board, BPiece.black, params);
        mgScore += mgScore(pstWhite) - mgScore(pstBlack);
        egScore += egScore(pstWhite) - egScore(pstBlack);

        int mobilityWhite = mobility(board, BPiece.white);
        int mobilityBlack = mobility(board, BPiece.black);
        mgScore += mgScore(mobilityWhite) - mgScore(mobilityBlack);
        egScore += egScore(mobilityWhite) - egScore(mobilityBlack);

        int bishopPairWhite = bishopPair(board, BPiece.white, params);
        int bishopPairBlack = bishopPair(board, BPiece.black, params);
        mgScore += mgScore(bishopPairWhite) - mgScore(bishopPairBlack);
        egScore += egScore(bishopPairWhite) - egScore(bishopPairBlack);

        int pawnStructureWhite = pawnStructure(board, BPiece.white, params);
        int pawnStructureBlack = pawnStructure(board, BPiece.black, params);
        mgScore += mgScore(pawnStructureWhite) - mgScore(pawnStructureBlack);
        egScore += egScore(pawnStructureWhite) - egScore(pawnStructureBlack);

        int kingSafetyWhite = kingSafety(board, BPiece.white, params);
        int kingSafetyBlack = kingSafety(board, BPiece.black, params);
        mgScore += mgScore(kingSafetyWhite) - mgScore(kingSafetyBlack);
        egScore += egScore(kingSafetyWhite) - egScore(kingSafetyBlack);

        int rookWhite = rook(board, BPiece.white, params);
        int rookBlack = rook(board, BPiece.black, params);
        mgScore += mgScore(rookWhite) - mgScore(rookBlack);
        egScore += egScore(rookWhite) - egScore(rookBlack);

        int score = (mgScore * phase + egScore * (TOTAL_PHASE - phase)) / TOTAL_PHASE;
        return board.isWhiteToMove() ? score : -score;
    }

    private int gamePhase(BBoard board) {
        int phase = TOTAL_PHASE;

        phase -= Long.bitCount(board.getBitboard(BPiece.whiteKnight)) * KNIGHT_PHASE;
        phase -= Long.bitCount(board.getBitboard(BPiece.blackKnight)) * KNIGHT_PHASE;
        phase -= Long.bitCount(board.getBitboard(BPiece.whiteBishop)) * BISHOP_PHASE;
        phase -= Long.bitCount(board.getBitboard(BPiece.blackBishop)) * BISHOP_PHASE;
        phase -= Long.bitCount(board.getBitboard(BPiece.whiteRook)) * ROOK_PHASE;
        phase -= Long.bitCount(board.getBitboard(BPiece.blackRook)) * ROOK_PHASE;
        phase -= Long.bitCount(board.getBitboard(BPiece.whiteQueen)) * QUEEN_PHASE;
        phase -= Long.bitCount(board.getBitboard(BPiece.blackQueen)) * QUEEN_PHASE;

        return phase; // Higher = more midgame
    }

    private static int makeScore(int mg, int eg) {
        return (mg << 16) + eg;
    }

    private static int mgScore(int score) {
        return (score + 0x8000) >> 16;
    }

    private static int egScore(int score) {
        return (short) (score & 0xFFFF);
    }

    private int material(BBoard board, EvalParams params) {
        int mgWhite = 0, egWhite = 0;

        int wPawns = Long.bitCount(board.getBitboard(BPiece.whitePawn));
        int wKnights = Long.bitCount(board.getBitboard(BPiece.whiteKnight));
        int wBishops = Long.bitCount(board.getBitboard(BPiece.whiteBishop));
        int wRooks = Long.bitCount(board.getBitboard(BPiece.whiteRook));
        int wQueens = Long.bitCount(board.getBitboard(BPiece.whiteQueen));

        mgWhite += wPawns * params.values[IDX_MG_PAWN];
        mgWhite += wKnights * params.values[IDX_MG_KNIGHT];
        mgWhite += wBishops * params.values[IDX_MG_BISHOP];
        mgWhite += wRooks * params.values[IDX_MG_ROOK];
        mgWhite += wQueens * params.values[IDX_MG_QUEEN];

        egWhite += wPawns * params.values[IDX_EG_PAWN];
        egWhite += wKnights * params.values[IDX_EG_KNIGHT];
        egWhite += wBishops * params.values[IDX_EG_BISHOP];
        egWhite += wRooks * params.values[IDX_EG_ROOK];
        egWhite += wQueens * params.values[IDX_EG_QUEEN];

        int mgBlack = 0, egBlack = 0;

        int bPawns = Long.bitCount(board.getBitboard(BPiece.blackPawn));
        int bKnights = Long.bitCount(board.getBitboard(BPiece.blackKnight));
        int bBishops = Long.bitCount(board.getBitboard(BPiece.blackBishop));
        int bRooks = Long.bitCount(board.getBitboard(BPiece.blackRook));
        int bQueens = Long.bitCount(board.getBitboard(BPiece.blackQueen));

        mgBlack += bPawns * params.values[IDX_MG_PAWN];
        mgBlack += bKnights * params.values[IDX_MG_KNIGHT];
        mgBlack += bBishops * params.values[IDX_MG_BISHOP];
        mgBlack += bRooks * params.values[IDX_MG_ROOK];
        mgBlack += bQueens * params.values[IDX_MG_QUEEN];

        egBlack += bPawns * params.values[IDX_EG_PAWN];
        egBlack += bKnights * params.values[IDX_EG_KNIGHT];
        egBlack += bBishops * params.values[IDX_EG_BISHOP];
        egBlack += bRooks * params.values[IDX_EG_ROOK];
        egBlack += bQueens * params.values[IDX_EG_QUEEN];

        return makeScore(mgWhite - mgBlack, egWhite - egBlack);
    }

    private int pst(BBoard board, int bpieceColor, EvalParams params) {
        int mgScore = 0;
        int egScore = 0;

        int pawnScore = evaluatePiecePst(board.getBitboard(BPiece.makePiece(BPiece.pawn, bpieceColor)), BPiece.pawn, bpieceColor, params);
        mgScore += mgScore(pawnScore);
        egScore += egScore(pawnScore);

        int knightScore = evaluatePiecePst(board.getBitboard(BPiece.makePiece(BPiece.knight, bpieceColor)), BPiece.knight, bpieceColor, params);
        mgScore += mgScore(knightScore);
        egScore += egScore(knightScore);

        int bishopScore = evaluatePiecePst(board.getBitboard(BPiece.makePiece(BPiece.bishop, bpieceColor)), BPiece.bishop, bpieceColor, params);
        mgScore += mgScore(bishopScore);
        egScore += egScore(bishopScore);

        int rookScore = evaluatePiecePst(board.getBitboard(BPiece.makePiece(BPiece.rook, bpieceColor)), BPiece.rook, bpieceColor, params);
        mgScore += mgScore(rookScore);
        egScore += egScore(rookScore);

        int queenScore = evaluatePiecePst(board.getBitboard(BPiece.makePiece(BPiece.queen, bpieceColor)), BPiece.queen, bpieceColor, params);
        mgScore += mgScore(queenScore);
        egScore += egScore(queenScore);

        int kingScore = evaluatePiecePst(board.getBitboard(BPiece.makePiece(BPiece.king, bpieceColor)), BPiece.king, bpieceColor, params);
        mgScore += mgScore(kingScore);
        egScore += egScore(kingScore);

        return makeScore(mgScore, egScore);
    }

    private int evaluatePiecePst(long bitboard, int piece, int bpieceColor, EvalParams params) {
        int mgScore = 0;
        int egScore = 0;
        boolean isWhite = bpieceColor == BPiece.white;

        while (bitboard != 0) {
            PopLsbResult result = PopLsbResult.popLsb(bitboard);
            int square = result.index;

            // For black pieces, mirror the square vertically
            int pstSquare = isWhite ? square : BBoardHelper.mirrorSquare(square);

            mgScore += params.mgPst[piece][pstSquare];
            egScore += params.egPst[piece][pstSquare];

            bitboard = result.remaining;
        }

        return makeScore(mgScore, egScore);
    }

    private int mobility(BBoard board, int bpieceColor) {
        int bboardColor = bpieceColor == BPiece.white ? BBoard.whiteIndex : BBoard.blackIndex;
        int mgScore = 0;
        int egScore = 0;

        long friendlyBoard = board.getColorBitboard(bboardColor);
        long occupancy = board.getAllPiecesBoard();

        int knightMob = mobilityKnight(board, BPiece.makePiece(BPiece.knight, bpieceColor), friendlyBoard);
        mgScore += mgScore(knightMob);
        egScore += egScore(knightMob);

        int bishopMob = mobilityBishop(board, BPiece.makePiece(BPiece.bishop, bpieceColor), friendlyBoard, occupancy);
        mgScore += mgScore(bishopMob);
        egScore += egScore(bishopMob);

        int rookMob = mobilityRook(board, BPiece.makePiece(BPiece.rook, bpieceColor), friendlyBoard, occupancy);
        mgScore += mgScore(rookMob);
        egScore += egScore(rookMob);

        int queenMob = mobilityQueen(board, BPiece.makePiece(BPiece.queen, bpieceColor), friendlyBoard, occupancy);
        mgScore += mgScore(queenMob);
        egScore += egScore(queenMob);

        return makeScore(mgScore, egScore);
    }

    private int mobilityKnight(BBoard board, int piece, long friendlyBoard) {
        int count = 0;
        long knights = board.getBitboard(piece);

        while (knights != 0) {
            PopLsbResult result = PopLsbResult.popLsb(knights);
            count += Long.bitCount(moveData.getKnightAttackBitboards()[result.index] & ~friendlyBoard);
            knights = result.remaining;
        }

        return makeScore(count * 4, count * 2);
    }

    private int mobilityBishop(BBoard board, int piece, long friendlyBoard, long occupancy) {
        int count = 0;
        long bishops = board.getBitboard(piece);

        while (bishops != 0) {
            PopLsbResult result = PopLsbResult.popLsb(bishops);
            long attacks = Magic.getBishopAttacks(result.index, occupancy);
            count += Long.bitCount(attacks & ~friendlyBoard);
            bishops = result.remaining;
        }

        return makeScore(count * 3, count * 3);
    }

    private int mobilityRook(BBoard board, int piece, long friendlyBoard, long occupancy) {
        int count = 0;
        long rooks = board.getBitboard(piece);

        while (rooks != 0) {
            PopLsbResult result = PopLsbResult.popLsb(rooks);
            long attacks = Magic.getRookAttacks(result.index, occupancy);
            count += Long.bitCount(attacks & ~friendlyBoard);
            rooks = result.remaining;
        }

        return makeScore(count * 2, count * 3);
    }

    private int mobilityQueen(BBoard board, int piece, long friendlyBoard, long occupancy) {
        int count = 0;
        long queens = board.getBitboard(piece);

        while (queens != 0) {
            PopLsbResult result = PopLsbResult.popLsb(queens);
            long attacks = Magic.getBishopAttacks(result.index, occupancy) |
                    Magic.getRookAttacks(result.index, occupancy);
            count += Long.bitCount(attacks & ~friendlyBoard);
            queens = result.remaining;
        }

        return makeScore(count, count / 2);
    }

    private static int bishopPair(BBoard board, int bpieceColor, EvalParams params) {
        boolean hasPair = Long.bitCount(board.getBitboard(BPiece.makePiece(BPiece.bishop, bpieceColor))) >= 2;
        if (!hasPair) return makeScore(0, 0);

        return makeScore(params.values[IDX_MG_BISHOP_PAIR], params.values[IDX_EG_BISHOP_PAIR]);
    }

    private static int pawnStructure(BBoard board, int bpieceColor, EvalParams params) {
        int mgScore = 0;
        int egScore = 0;
        boolean isWhite = BPiece.white == bpieceColor;
        int bboardColor = isWhite ? BBoard.whiteIndex : BBoard.blackIndex;
        long fPawns = board.getBitboard(BPiece.makePiece(BPiece.pawn, bpieceColor));
        long ePawns = board.getBitboard(BPiece.makePiece(BPiece.pawn, isWhite ? BPiece.black : BPiece.white));

        long fPawnsCopy = fPawns;
        while (fPawnsCopy != 0) {
            PopLsbResult result = PopLsbResult.popLsb(fPawnsCopy);
            int square = result.index;

            int file = BBoardHelper.fileIndex(square);
            int rank = BBoardHelper.rankIndex(square);

            // Doubled pawns
            if (Long.bitCount(fPawns & FILE_FILL_MASKS[file]) > 1) {
                mgScore += params.values[IDX_MG_DOUBLED_PAWN];
                egScore += params.values[IDX_EG_DOUBLED_PAWN];
            }

            // Isolated pawns
            if ((fPawns & ADJACENT_FILE_MASKS[file]) == 0) {
                mgScore += params.values[IDX_MG_ISOLATED_PAWN];
                egScore += params.values[IDX_EG_ISOLATED_PAWN];
            }

            // Passed pawns
            if ((ePawns & PASSED_PAWN_MASKS[bboardColor][square]) == 0) {
                int rankPerspectively = isWhite ? rank : (7 - rank);
                mgScore += params.mgPassedPawnRank[rankPerspectively];
                egScore += params.egPassedPawnRank[rankPerspectively];
            }

            // Pawn chains
            if ((fPawns & PAWN_SUPPORT_MASKS[bboardColor][square]) != 0) {
                mgScore += params.values[IDX_MG_PAWN_CHAIN];
                egScore += params.values[IDX_EG_PAWN_CHAIN];
            }

            fPawnsCopy = result.remaining;
        }

        return makeScore(mgScore, egScore);
    }

    private static int kingSafety(BBoard board, int bpieceColor, EvalParams params) {
        int mgScore = 0;
        int egScore = 0;
        int bboardColor = bpieceColor == BPiece.white ? BBoard.whiteIndex : BBoard.blackIndex;

        int kingSquare = board.getKingSquare(bboardColor);
        int kingFile = BBoardHelper.fileIndex(kingSquare);

        long fPawns = board.getBitboard(BPiece.makePiece(BPiece.pawn, bpieceColor));

        // Penalize missing pawn shield
        for (int f = Math.max(0, kingFile - 1); f <= Math.min(7, kingFile + 1); f++) {
            if ((fPawns & FILE_MASKS[f]) == 0) {
                mgScore += params.values[IDX_MG_KING_SHIELD];
                egScore += params.values[IDX_EG_KING_SHIELD];
            }
        }

        return makeScore(mgScore, egScore);
    }

    private static int rook(BBoard board, int bpieceColor, EvalParams params) {
        int mgScore = 0;
        int egScore = 0;

        long rooks = board.getBitboard(BPiece.makePiece(BPiece.rook, bpieceColor));
        long allPawns =
                board.getBitboard(BPiece.makePiece(BPiece.pawn, BPiece.white)) |
                        board.getBitboard(BPiece.makePiece(BPiece.pawn, BPiece.black));
        long friendlyPawns = board.getBitboard(BPiece.makePiece(BPiece.pawn, bpieceColor));

        while (rooks != 0) {
            PopLsbResult result = PopLsbResult.popLsb(rooks);
            int square = result.index;
            int file = BBoardHelper.fileIndex(square);

            long fileMask = FILE_MASKS[file];
            if ((allPawns & fileMask) == 0) {
                // Open file
                mgScore += params.values[IDX_MG_ROOK_OPEN];
                egScore += params.values[IDX_EG_ROOK_OPEN];
            } else if ((friendlyPawns & fileMask) == 0) {
                // Semi-open file
                mgScore += params.values[IDX_MG_ROOK_SEMIOPEN];
                egScore += params.values[IDX_EG_ROOK_SEMIOPEN];
            }

            rooks = result.remaining;
        }

        return makeScore(mgScore, egScore);
    }
}