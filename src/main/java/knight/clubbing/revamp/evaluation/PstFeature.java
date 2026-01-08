package knight.clubbing.revamp.evaluation;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BPiece;

public class PstFeature implements EvalFeature {

    private static final int[][] PST = initializePst();

    private static int[][] initializePst() {
        int[][] pst = new int[7][64];

        pst[BPiece.pawn] = new int[] {
                0,  0,  0,  0,  0,  0,  0,  0,
                50, 50, 50, 50, 50, 50, 50, 50,
                10, 10, 20, 30, 30, 20, 10, 10,
                5,  5, 10, 25, 25, 10,  5,  5,
                0,  0,  0, 20, 20,  0,  0,  0,
                5, -5,-10,  0,  0,-10, -5,  5,
                5, 10, 10,-20,-20, 10, 10,  5,
                0,  0,  0,  0,  0,  0,  0,  0
        };

        pst[BPiece.knight] = new int[] {
                -50,-40,-30,-30,-30,-30,-40,-50,
                -40,-20,  0,  0,  0,  0,-20,-40,
                -30,  0, 10, 15, 15, 10,  0,-30,
                -30,  5, 15, 20, 20, 15,  5,-30,
                -30,  0, 15, 20, 20, 15,  0,-30,
                -30,  5, 10, 15, 15, 10,  5,-30,
                -40,-20,  0,  5,  5,  0,-20,-40,
                -50,-40,-30,-30,-30,-30,-40,-50
        };

        pst[BPiece.bishop] = new int[] {
                -20,-10,-10,-10,-10,-10,-10,-20,
                -10,  0,  0,  0,  0,  0,  0,-10,
                -10,  0,  5, 10, 10,  5,  0,-10,
                -10,  5,  5, 10, 10,  5,  5,-10,
                -10,  0, 10, 10, 10, 10,  0,-10,
                -10, 10, 10, 10, 10, 10, 10,-10,
                -10, 15,  0,  0,  0,  0, 15,-10,
                -20,-10,-10,-10,-10,-10,-10,-20
        };

        pst[BPiece.rook] = new int[] {
                0,  0,  0,  0,  0,  0,  0,  0,
                5, 10, 10, 10, 10, 10, 10,  5,
                -5, 0, 0, 0, 0, 0, 0, -5,
                -5, 0, 0, 0, 0, 0, 0, -5,
                -5, 0, 0, 0, 0, 0, 0, -5,
                -5, 0, 0, 0, 0, 0, 0, -5,
                -5, 0, 0, 0, 0, 0, 0, -5,
                0,  0,  0,  5,  5,  0,  0,  0
        };

        pst[BPiece.queen] = new int[] {
                -20,-10,-10, -5, -5,-10,-10,-20,
                -10,  0,  0,  0,  0,  0,  0,-10,
                -10,  0,  5,  5,  5,  5,  0,-10,
                 -5,  0,  5,  5,  5,  5,  0, -5,
                  0,  0,  5,  5,  5,  5,  0, -5,
                -10,  5,  5,  5,  5,  5,  0,-10,
                -10,  0,  5,  0,  0,  0,  0,-10,
                -20,-10,-10, -5, -5,-10,-10,-20
        };

        pst[BPiece.king] = new int[] {
                -30,-40,-40,-50,-50,-40,-40,-30,
                -30,-40,-40,-50,-50,-40,-40,-30,
                -30,-40,-40,-50,-50,-40,-40,-30,
                -30,-40,-40,-50,-50,-40,-40,-30,
                -20,-30,-30,-40,-40,-30,-30,-20,
                -10,-20,-20,-20,-20,-20,-20,-10,
                 20, 20,  0,  0,  0,  0, 20, 20,
                 20, 30, 10,  0,  0, 10, 30, 20
        };

        return pst;
    }

    @Override
    public int compute(BBoard board) {
        int scoreWhite = 0;
        scoreWhite += getPstScore(board, BPiece.whitePawn);
        scoreWhite += getPstScore(board, BPiece.whiteKnight);
        scoreWhite += getPstScore(board, BPiece.whiteBishop);
        scoreWhite += getPstScore(board, BPiece.whiteRook);
        scoreWhite += getPstScore(board, BPiece.whiteQueen);
        scoreWhite += getPstScore(board, BPiece.whiteKing);

        int scoreBlack = 0;
        scoreBlack += getPstScore(board, BPiece.blackPawn);
        scoreBlack += getPstScore(board, BPiece.blackKnight);
        scoreBlack += getPstScore(board, BPiece.blackBishop);
        scoreBlack += getPstScore(board, BPiece.blackRook);
        scoreBlack += getPstScore(board, BPiece.blackQueen);
        scoreBlack += getPstScore(board, BPiece.blackKing);

        return scoreWhite - scoreBlack;
    }

    protected static int getPstScore(BBoard board, int piece) {
        int score = 0;
        long pieceBitboard = board.getBitboard(piece);
        boolean isWhite = BPiece.isWhite(piece);
        while (pieceBitboard != 0) {
            int square = Long.numberOfTrailingZeros(pieceBitboard);
            square = isWhite ? mirror(square) : square;
            score += PST[BPiece.getPieceType(piece)][square];
            pieceBitboard &= pieceBitboard - 1;
        }
        return score;
    }

    protected static int mirror(int square) {
        return square ^ 56;
    }

    @Override
    public String name() {
        return "Piece-Square Tables";
    }
}
