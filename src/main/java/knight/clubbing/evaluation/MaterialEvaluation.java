package knight.clubbing.evaluation;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BPiece;

public class MaterialEvaluation {

    protected static final int PAWN_VALUE = 100;
    protected static final int KNIGHT_VALUE = 320;
    protected static final int BISHOP_VALUE = 330;
    protected static final int ROOK_VALUE = 500;
    protected static final int QUEEN_VALUE = 900;

    private static final int knightEgw = 10;
    private static final int bishopEgw = 10;
    private static final int rookEgw = 20;
    private static final int queenEgw = 45;
    private static final int baseEgw = queenEgw + 2 * rookEgw + 2 * knightEgw + 2 * bishopEgw;

    private int materialScore;
    private int numPawns;
    private int numKnights;
    private int numBishops;
    private int numRooks;
    private int numQueens;

    private int numMinor;
    private int numMajor;

    private float endgameT;

    public MaterialEvaluation(int materialScore, int numPawns, int numKnights, int numBishops, int numRooks, int numQueens, int numMinor, int numMajor, float endgameT) {
        this.materialScore = materialScore;
        this.numPawns = numPawns;
        this.numKnights = numKnights;
        this.numBishops = numBishops;
        this.numRooks = numRooks;
        this.numQueens = numQueens;
        this.numMinor = numMinor;
        this.numMajor = numMajor;
        this.endgameT = endgameT;
    }

    public static MaterialEvaluation getMaterialEvaluation(BBoard board, boolean isWhite) {
        int countPawns = Long.bitCount(board.getBitboard(BPiece.makePiece(BPiece.pawn, isWhite)));
        int countKnights = Long.bitCount(board.getBitboard(BPiece.makePiece(BPiece.knight, isWhite)));
        int countBishops = Long.bitCount(board.getBitboard(BPiece.makePiece(BPiece.bishop, isWhite)));
        int countRooks = Long.bitCount(board.getBitboard(BPiece.makePiece(BPiece.rook, isWhite)));
        int countQueens = Long.bitCount(board.getBitboard(BPiece.makePiece(BPiece.queen, isWhite)));

        int countMinors = countKnights + countBishops;
        int countMajors = countRooks + countQueens;

        int materialScore =
                countPawns * PAWN_VALUE +
                countKnights * KNIGHT_VALUE +
                countBishops * BISHOP_VALUE +
                countRooks * ROOK_VALUE +
                countQueens * QUEEN_VALUE;


        int egw = countQueens * queenEgw + countRooks * rookEgw + countKnights * knightEgw + countBishops * bishopEgw;
        int endgameT = 1 - Math.min(1, (int)(egw / (float)baseEgw));

        return new MaterialEvaluation(materialScore, countPawns, countKnights, countBishops, countRooks, countQueens, countMinors, countMajors, endgameT);
    }

    public int getMaterialScore() {
        return materialScore;
    }

    public int getNumPawns() {
        return numPawns;
    }

    public int getNumKnights() {
        return numKnights;
    }

    public int getNumBishops() {
        return numBishops;
    }

    public int getNumRooks() {
        return numRooks;
    }

    public int getNumQueens() {
        return numQueens;
    }

    public int getNumMinor() {
        return numMinor;
    }

    public int getNumMajor() {
        return numMajor;
    }

    public float getEndgameT() {
        return endgameT;
    }
}
