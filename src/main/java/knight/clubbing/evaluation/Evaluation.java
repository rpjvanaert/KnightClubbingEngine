package knight.clubbing.evaluation;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BCoord;
import knight.clubbing.core.BPiece;
import knight.clubbing.core.PopLsbResult;

public class Evaluation {

    protected static final int[][] pst = new int[BPiece.maxPieceIndex + 1][64];

    public static int evaluate(BBoard board) {
        MaterialEvaluation whiteMaterialEvaluation = MaterialEvaluation.getMaterialEvaluation(board, true);
        MaterialEvaluation blackMaterialEvaluation = MaterialEvaluation.getMaterialEvaluation(board, false);

        int pstEvalWhite = pstEvaluation(board, true, whiteMaterialEvaluation.getEndgameT());
        int pstEvalBlack = pstEvaluation(board, false, blackMaterialEvaluation.getEndgameT());


        EvaluationData whiteEvaluationData = new EvaluationData(whiteMaterialEvaluation.getMaterialScore(), pstEvalWhite);
        EvaluationData blackEvaluationData = new EvaluationData(blackMaterialEvaluation.getMaterialScore(), pstEvalBlack);

        return whiteEvaluationData.sum() - blackEvaluationData.sum();
    }

    private static int pstEvaluation(BBoard board, boolean isWhite, float endGameT) {
        int value = 0;
        int colorIndex = isWhite ? BBoard.whiteIndex : BBoard.blackIndex;

        value += pstEvaluation(BPiece.knight, board, isWhite);
        value += pstEvaluation(BPiece.bishop, board, isWhite);
        value += pstEvaluation(BPiece.rook, board, isWhite);
        value += pstEvaluation(BPiece.queen, board, isWhite);
        
        int pawn = pstEvaluation(BPiece.pawn, board, isWhite);
        value += (int)(pawn * (1 - endGameT));
        int pawnEndgame = pstEvaluation(pawnsEnd, board.getBitboard(BPiece.makePiece(BPiece.pawn, isWhite)), isWhite);
        value += (int)(pawnEndgame * endGameT);

        int king = pstEvaluation(BPiece.king, board, isWhite);
        value += (int)(king * (1 - endGameT));
        int kingEndgame = kingEnd[isWhite ? board.getKingSquare(colorIndex) : mirrorSquareIndex(board.getKingSquare(colorIndex))];
        value += (int)(kingEndgame * endGameT);

        return value;
    }

    private static int pstEvaluation(int pieceType, BBoard board, boolean isWhite) {
        int value = 0;
        int piece = BPiece.makePiece(pieceType, isWhite);
        long bitboard = board.getBitboard(piece);

        while (bitboard != 0) {
            PopLsbResult popLsbResult = PopLsbResult.popLsb(bitboard);
            int square = popLsbResult.index;
            bitboard = popLsbResult.remaining;

            value += readPst(piece, square);
        }
        return value;
    }

    private static int pstEvaluation(int[] table, long bitboard, boolean isWhite) {
        int value = 0;

        while (bitboard != 0) {
            PopLsbResult popLsbResult = PopLsbResult.popLsb(bitboard);
            int square = popLsbResult.index;
            bitboard = popLsbResult.remaining;

            if (isWhite)
                value += table[square];
            else
                value += table[mirrorSquareIndex(square)];
        }

        return value;
    }

    public static int readPst(int piece, int square) {
        return pst[piece][square];
    }

    private static final int[] pawns = {
            0,   0,   0,   0,   0,   0,   0,   0,
            50,  50,  50,  50,  50,  50,  50,  50,
            10,  10,  20,  30,  30,  20,  10,  10,
            5,   5,  10,  25,  25,  10,   5,   5,
            0,   0,   0,  20,  20,   0,   0,   0,
            5,  -5, -10,   0,   0, -10,  -5,   5,
            5,  10,  10, -20, -20,  10,  10,   5,
            0,   0,   0,   0,   0,   0,   0,   0
    };

    protected static final int[] pawnsEnd = {
            0,   0,   0,   0,   0,   0,   0,   0,
            80,  80,  80,  80,  80,  80,  80,  80,
            50,  50,  50,  50,  50,  50,  50,  50,
            30,  30,  30,  30,  30,  30,  30,  30,
            20,  20,  20,  20,  20,  20,  20,  20,
            10,  10,  10,  10,  10,  10,  10,  10,
            10,  10,  10,  10,  10,  10,  10,  10,
            0,   0,   0,   0,   0,   0,   0,   0
    };

    private static final int[] rooks =  {
            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10, 10, 10, 10, 10,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            0,  0,  0,  5,  5,  0,  0,  0
    };
    private static final int[] knights = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50,
    };
    private static final int[] bishops =  {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20,
    };
    private static final int[] queens =  {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -5,   0,  5,  5,  5,  5,  0, -5,
            0,    0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };
    private static final int[] king =
            {
                    -80, -70, -70, -70, -70, -70, -70, -80,
                    -60, -60, -60, -60, -60, -60, -60, -60,
                    -40, -50, -50, -60, -60, -50, -50, -40,
                    -30, -40, -40, -50, -50, -40, -40, -30,
                    -20, -30, -30, -40, -40, -30, -30, -20,
                    -10, -20, -20, -20, -20, -20, -20, -10,
                    20,  20,  -5,  -5,  -5,  -5,  20,  20,
                    20,  30,  10,   0,   0,  10,  30,  20
            };

    protected static final int[] kingEnd =
            {
                    -20, -10, -10, -10, -10, -10, -10, -20,
                    -5,   0,   5,   5,   5,   5,   0,  -5,
                    -10, -5,   20,  30,  30,  20,  -5, -10,
                    -15, -10,  35,  45,  45,  35, -10, -15,
                    -20, -15,  30,  40,  40,  30, -15, -20,
                    -25, -20,  20,  25,  25,  20, -20, -25,
                    -30, -25,   0,   0,   0,   0, -25, -30,
                    -50, -30, -30, -30, -30, -30, -30, -50
            };

    static {
        pst[BPiece.whitePawn] = pawns;
        pst[BPiece.whiteKnight] = knights;
        pst[BPiece.whiteBishop] = bishops;
        pst[BPiece.whiteRook] = rooks;
        pst[BPiece.whiteQueen] = queens;
        pst[BPiece.whiteKing] = king;

        pst[BPiece.blackPawn] = mirror(pawns);
        pst[BPiece.blackKnight] = mirror(knights);
        pst[BPiece.blackBishop] = mirror(bishops);
        pst[BPiece.blackRook] = mirror(rooks);
        pst[BPiece.blackQueen] = mirror(queens);
        pst[BPiece.blackKing] = mirror(king);
    }

    protected static int[] mirror(int[] pst) {
        int[] mirror = new int[pst.length];
        for (int i = 0; i < pst.length; i++) {
            mirror[mirrorSquareIndex(i)] = pst[i];
        }
        return mirror;
    }

    protected static int mirrorSquareIndex(int squareIndex) {
        BCoord coord = new BCoord(squareIndex);
        BCoord mirrorCoord = new BCoord(coord.getFileIndex(), 7 - coord.getRankIndex());
        return mirrorCoord.getSquareIndex();
    }

    private Evaluation() {}
}
