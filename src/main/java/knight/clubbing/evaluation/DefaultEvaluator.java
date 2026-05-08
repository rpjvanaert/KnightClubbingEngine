package knight.clubbing.evaluation;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BBoardHelper;
import knight.clubbing.core.BPiece;
import knight.clubbing.core.PopLsbResult;
import knight.clubbing.movegen.PrecomputedMoveData;
import knight.clubbing.movegen.magic.Magic;

import static knight.clubbing.core.BBoardHelper.FILE_MASKS;

public class DefaultEvaluator implements Evaluator {

    private static final int[] MG_PIECE_VALUES = {
            0,      // Empty
            100,    // Pawn
            320,    // Knight
            330,    // Bishop
            500,    // Rook
            950,    // Queen
            0       // King
    };

    private static final int[] EG_PIECE_VALUES = {
            0,      // Empty
            110,    // Pawn
            300,    // Knight
            330,    // Bishop
            550,    // Rook
            1000,   // Queen
            0       // King
    };

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

    private static final int[][] MG_PST = new int[7][64];
    private static final int[][] EG_PST = new int[7][64];

    private static final long[] FILE_FILL_MASKS = new long[8];
    private static final long[] ADJACENT_FILE_MASKS = new long[8];
    private static final long[] WHITE_AHEAD_MASKS = new long[64];
    private static final long[] BLACK_AHEAD_MASKS = new long[64];
    private static final long[][] PASSED_PAWN_MASKS = new long[2][64];
    private static final long[][] PAWN_SUPPORT_MASKS = new long[2][64];

    static {
        MG_PST[0] = new int[64];
        EG_PST[0] = new int[64];

        MG_PST[BPiece.pawn] = new int[]{
                0,   0,   0,   0,   0,   0,   0,   0,
                5,  10,  10, -20, -20,  10,  10,   5,
                5,  -5, -10,   0,   0, -10,  -5,   5,
                0,   0,   0,  20,  20,   0,   0,   0,
                5,   5,  10,  25,  25,  10,   5,   5,
                10,  10,  20,  30,  30,  20,  10,  10,
                50,  50,  50,  50,  50,  50,  50,  50,
                0,   0,   0,   0,   0,   0,   0,   0
        };

        EG_PST[BPiece.pawn] = new int[]{
                0,   0,   0,   0,   0,   0,   0,   0,
                10,  10,  10,  10,  10,  10,  10,  10,
                20,  20,  20,  20,  20,  20,  20,  20,
                30,  30,  30,  30,  30,  30,  30,  30,
                40,  40,  40,  40,  40,  40,  40,  40,
                50,  50,  50,  50,  50,  50,  50,  50,
                70,  70,  70,  70,  70,  70,  70,  70,
                0,   0,   0,   0,   0,   0,   0,   0
        };

        MG_PST[BPiece.knight] = new int[]{
                -50, -40, -30, -30, -30, -30, -40, -50,
                -40, -20,   0,   5,   5,   0, -20, -40,
                -30,   5,  10,  15,  15,  10,   5, -30,
                -30,   0,  15,  20,  20,  15,   0, -30,
                -30,   5,  15,  20,  20,  15,   5, -30,
                -30,   0,  10,  15,  15,  10,   0, -30,
                -40, -20,   0,   0,   0,   0, -20, -40,
                -50, -40, -30, -30, -30, -30, -40, -50
        };

        EG_PST[BPiece.knight] = new int[]{
                -50, -40, -30, -30, -30, -30, -40, -50,
                -40, -20,   0,   0,   0,   0, -20, -40,
                -30,   0,   5,  10,  10,   5,   0, -30,
                -30,   0,  10,  15,  15,  10,   0, -30,
                -30,   0,  10,  15,  15,  10,   0, -30,
                -30,   0,   5,  10,  10,   5,   0, -30,
                -40, -20,   0,   0,   0,   0, -20, -40,
                -50, -40, -30, -30, -30, -30, -40, -50
        };

        MG_PST[BPiece.bishop] = new int[]{
                -20, -10, -10, -10, -10, -10, -10, -20,
                -10,   5,   0,   0,   0,   0,   5, -10,
                -10,  10,  10,  10,  10,  10,  10, -10,
                -10,   0,  10,  10,  10,  10,   0, -10,
                -10,   5,   5,  10,  10,   5,   5, -10,
                -10,   0,   5,  10,  10,   5,   0, -10,
                -10,   0,   0,   0,   0,   0,   0, -10,
                -20, -10, -10, -10, -10, -10, -10, -20
        };

        EG_PST[BPiece.bishop] = new int[]{
                -20, -10, -10, -10, -10, -10, -10, -20,
                -10,   0,   0,   0,   0,   0,   0, -10,
                -10,   0,   5,  10,  10,   5,   0, -10,
                -10,   5,   5,  10,  10,   5,   5, -10,
                -10,   0,  10,  10,  10,  10,   0, -10,
                -10,  10,  10,  10,  10,  10,  10, -10,
                -10,   5,   0,   0,   0,   0,   5, -10,
                -20, -10, -10, -10, -10, -10, -10, -20
        };

        MG_PST[BPiece.rook] = new int[]{
                0,   0,   0,   5,   5,   0,   0,   0,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                5,  10,  10,  10,  10,  10,  10,   5,
                0,   0,   0,   0,   0,   0,   0,   0
        };

        EG_PST[BPiece.rook] = new int[]{
                0,   0,   0,   0,   0,   0,   0,   0,
                5,  10,  10,  10,  10,  10,  10,   5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                0,   0,   0,   5,   5,   0,   0,   0
        };

        MG_PST[BPiece.queen] = new int[]{
                -20, -10, -10,  -5,  -5, -10, -10, -20,
                -10,   0,   5,   0,   0,   0,   0, -10,
                -10,   5,   5,   5,   5,   5,   0, -10,
                0,   0,   5,   5,   5,   5,   0,  -5,
                -5,   0,   5,   5,   5,   5,   0,  -5,
                -10,   0,   5,   5,   5,   5,   0, -10,
                -10,   0,   0,   0,   0,   0,   0, -10,
                -20, -10, -10,  -5,  -5, -10, -10, -20
        };

        EG_PST[BPiece.queen] = new int[]{
                -20, -10, -10,  -5,  -5, -10, -10, -20,
                -10,   0,   0,   0,   0,   0,   0, -10,
                -10,   0,   5,   5,   5,   5,   0, -10,
                -5,   0,   5,   5,   5,   5,   0,  -5,
                0,   0,   5,   5,   5,   5,   0,  -5,
                -10,   5,   5,   5,   5,   5,   0, -10,
                -10,   0,   5,   0,   0,   0,   0, -10,
                -20, -10, -10,  -5,  -5, -10, -10, -20
        };

        MG_PST[BPiece.king] = new int[]{
                20,  30,  10,   0,   0,  10,  30,  20,
                20,  20,   0,   0,   0,   0,  20,  20,
                -10, -20, -20, -20, -20, -20, -20, -10,
                -20, -30, -30, -40, -40, -30, -30, -20,
                -30, -40, -40, -50, -50, -40, -40, -30,
                -30, -40, -40, -50, -50, -40, -40, -30,
                -30, -40, -40, -50, -50, -40, -40, -30,
                -30, -40, -40, -50, -50, -40, -40, -30
        };

        EG_PST[BPiece.king] = new int[]{
                -50, -30, -30, -30, -30, -30, -30, -50,
                -30, -30,   0,   0,   0,   0, -30, -30,
                -30, -10,  20,  30,  30,  20, -10, -30,
                -30, -10,  30,  40,  40,  30, -10, -30,
                -30, -10,  30,  40,  40,  30, -10, -30,
                -30, -10,  20,  30,  30,  20, -10, -30,
                -30, -20, -10,   0,   0, -10, -20, -30,
                -50, -40, -30, -20, -20, -30, -40, -50
        };

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
    public int evaluate(BBoard board) {
        int mgScore = 0;
        int egScore = 0;
        int phase = gamePhase(board);

        int materialScore = material(board);
        mgScore += mgScore(materialScore);
        egScore += egScore(materialScore);

        int pstWhite = pst(board, BPiece.white);
        int pstBlack = pst(board, BPiece.black);
        mgScore += mgScore(pstWhite) - mgScore(pstBlack);
        egScore += egScore(pstWhite) - egScore(pstBlack);

        int mobilityWhite = mobility(board, BPiece.white);
        int mobilityBlack = mobility(board, BPiece.black);
        mgScore += mgScore(mobilityWhite) - mgScore(mobilityBlack);
        egScore += egScore(mobilityWhite) - egScore(mobilityBlack);

        int score = 0;

        int bishopPairWhite = bishopPair(board, BPiece.white);
        int bishopPairBlack = bishopPair(board, BPiece.black);
        mgScore += mgScore(bishopPairWhite) - mgScore(bishopPairBlack);
        egScore += egScore(bishopPairWhite) - egScore(bishopPairBlack);

        int pawnStructureWhite = pawnStructure(board, BPiece.white);
        int pawnStructureBlack = pawnStructure(board, BPiece.black);
        mgScore += mgScore(pawnStructureWhite) - mgScore(pawnStructureBlack);
        egScore += egScore(pawnStructureWhite) - pawnStructureBlack;

        int kingSafetyWhite = kingSafety(board, BPiece.white);
        int kingSafetyBlack = kingSafety(board, BPiece.black);
        mgScore += mgScore(kingSafetyWhite) - kingSafetyBlack;
        egScore += egScore(kingSafetyWhite) - egScore(kingSafetyBlack);

        int rookWhite = rook(board, BPiece.white);
        int rookBlack = rook(board, BPiece.black);
        mgScore += mgScore(rookWhite) - mgScore(rookBlack);
        egScore += egScore(rookWhite) - egScore(rookBlack);

        score += (mgScore * phase + egScore * (TOTAL_PHASE - phase)) / TOTAL_PHASE;
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

    private int material(BBoard board) {
        int mgWhite = 0, egWhite = 0;

        int wPawns = Long.bitCount(board.getBitboard(BPiece.whitePawn));
        int wKnights = Long.bitCount(board.getBitboard(BPiece.whiteKnight));
        int wBishops = Long.bitCount(board.getBitboard(BPiece.whiteBishop));
        int wRooks = Long.bitCount(board.getBitboard(BPiece.whiteRook));
        int wQueens = Long.bitCount(board.getBitboard(BPiece.whiteQueen));

        mgWhite += wPawns * MG_PIECE_VALUES[BPiece.pawn];
        mgWhite += wKnights * MG_PIECE_VALUES[BPiece.knight];
        mgWhite += wBishops * MG_PIECE_VALUES[BPiece.bishop];
        mgWhite += wRooks * MG_PIECE_VALUES[BPiece.rook];
        mgWhite += wQueens * MG_PIECE_VALUES[BPiece.queen];

        egWhite += wPawns * EG_PIECE_VALUES[BPiece.pawn];
        egWhite += wKnights * EG_PIECE_VALUES[BPiece.knight];
        egWhite += wBishops * EG_PIECE_VALUES[BPiece.bishop];
        egWhite += wRooks * EG_PIECE_VALUES[BPiece.rook];
        egWhite += wQueens * EG_PIECE_VALUES[BPiece.queen];

        int mgBlack = 0, egBlack = 0;

        int bPawns = Long.bitCount(board.getBitboard(BPiece.blackPawn));
        int bKnights = Long.bitCount(board.getBitboard(BPiece.blackKnight));
        int bBishops = Long.bitCount(board.getBitboard(BPiece.blackBishop));
        int bRooks = Long.bitCount(board.getBitboard(BPiece.blackRook));
        int bQueens = Long.bitCount(board.getBitboard(BPiece.blackQueen));

        mgBlack += bPawns * MG_PIECE_VALUES[BPiece.pawn];
        mgBlack += bKnights * MG_PIECE_VALUES[BPiece.knight];
        mgBlack += bBishops * MG_PIECE_VALUES[BPiece.bishop];
        mgBlack += bRooks * MG_PIECE_VALUES[BPiece.rook];
        mgBlack += bQueens * MG_PIECE_VALUES[BPiece.queen];

        egBlack += bPawns * EG_PIECE_VALUES[BPiece.pawn];
        egBlack += bKnights * EG_PIECE_VALUES[BPiece.knight];
        egBlack += bBishops * EG_PIECE_VALUES[BPiece.bishop];
        egBlack += bRooks * EG_PIECE_VALUES[BPiece.rook];
        egBlack += bQueens * EG_PIECE_VALUES[BPiece.queen];

        return makeScore(mgWhite - mgBlack, egWhite - egBlack);
    }

    private int pst(BBoard board, int bpieceColor) {
        int mgScore = 0;
        int egScore = 0;

        int pawnScore = evaluatePiecePst(board.getBitboard(BPiece.makePiece(BPiece.pawn, bpieceColor)), BPiece.pawn, bpieceColor);
        mgScore += mgScore(pawnScore);
        egScore += egScore(pawnScore);

        int knightScore = evaluatePiecePst(board.getBitboard(BPiece.makePiece(BPiece.knight, bpieceColor)), BPiece.knight, bpieceColor);
        mgScore += mgScore(knightScore);
        egScore += egScore(knightScore);

        int bishopScore = evaluatePiecePst(board.getBitboard(BPiece.makePiece(BPiece.bishop, bpieceColor)), BPiece.bishop, bpieceColor);
        mgScore += mgScore(bishopScore);
        egScore += egScore(bishopScore);

        int rookScore = evaluatePiecePst(board.getBitboard(BPiece.makePiece(BPiece.rook, bpieceColor)), BPiece.rook, bpieceColor);
        mgScore += mgScore(rookScore);
        egScore += egScore(rookScore);

        int queenScore = evaluatePiecePst(board.getBitboard(BPiece.makePiece(BPiece.queen, bpieceColor)), BPiece.queen, bpieceColor);
        mgScore += mgScore(queenScore);
        egScore += egScore(queenScore);

        int kingScore = evaluatePiecePst(board.getBitboard(BPiece.makePiece(BPiece.king, bpieceColor)), BPiece.king, bpieceColor);
        mgScore += mgScore(kingScore);
        egScore += egScore(kingScore);

        return makeScore(mgScore, egScore);
    }

    private int evaluatePiecePst(long bitboard, int piece, int bpieceColor) {
        int mgScore = 0;
        int egScore = 0;
        boolean isWhite = bpieceColor == BPiece.white;

        while (bitboard != 0) {
            PopLsbResult result = PopLsbResult.popLsb(bitboard);
            int square = result.index;

            // For black pieces, mirror the square vertically
            int pstSquare = isWhite ? square : BBoardHelper.mirrorSquare(square);

            mgScore += MG_PST[piece][pstSquare];
            egScore += EG_PST[piece][pstSquare];

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

    private static int bishopPair(BBoard board, int bpieceColor) {
        boolean hasPair = Long.bitCount(board.getBitboard(BPiece.makePiece(BPiece.bishop, bpieceColor))) >= 2;
        if (!hasPair) return makeScore(0, 0);

        return makeScore(30, 50);
    }

    private static int pawnStructure(BBoard board, int bpieceColor) {
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
                mgScore -= 15;
                egScore -= 20;
            }

            // Isolated pawns
            if ((fPawns & ADJACENT_FILE_MASKS[file]) == 0) {
                mgScore -= 20;
                egScore -= 25;
            }

            // Passed pawns
            if ((ePawns & PASSED_PAWN_MASKS[bboardColor][square]) == 0) {
                int bonus = isWhite ? rank : (7 - rank);
                mgScore += bonus * 10;
                egScore += bonus * 20;
            }

            // Pawn chains
            if ((fPawns & PAWN_SUPPORT_MASKS[bboardColor][square]) != 0) {
                mgScore += 16;
                egScore += 12;
            }

            fPawnsCopy = result.remaining;
        }

        return makeScore(mgScore, egScore);
    }

    private static int kingSafety(BBoard board, int bpieceColor) {
        int mgScore = 0;
        int egScore = 0;
        int bboardColor = bpieceColor == BPiece.white ? BBoard.whiteIndex : BBoard.blackIndex;

        int kingSquare = board.getKingSquare(bboardColor);
        int kingFile = BBoardHelper.fileIndex(kingSquare);

        long fPawns = board.getBitboard(BPiece.makePiece(BPiece.pawn, bpieceColor));

        // Penalize missing pawn shield
        for (int f = Math.max(0, kingFile - 1); f <= Math.min(7, kingFile + 1); f++) {
            if ((fPawns & FILE_MASKS[f]) == 0) {
                mgScore -= 15;
                egScore -= 3;
            }
        }

        return makeScore(mgScore, egScore);
    }

    private static int rook(BBoard board, int bpieceColor) {
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
                mgScore += 25;
                egScore += 15;
            } else if ((friendlyPawns & fileMask) == 0) {
                // Semi-open file
                mgScore += 12;
                egScore += 10;
            }

            rooks = result.remaining;
        }

        return makeScore(mgScore, egScore);
    }
}