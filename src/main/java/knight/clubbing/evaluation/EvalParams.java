package knight.clubbing.evaluation;

import knight.clubbing.core.BPiece;

public class EvalParams {

    // Material
    public static final int IDX_MG_PAWN   = 0;
    public static final int IDX_EG_PAWN   = 1;
    public static final int IDX_MG_KNIGHT = 2;
    public static final int IDX_EG_KNIGHT = 3;
    public static final int IDX_MG_BISHOP = 4;
    public static final int IDX_EG_BISHOP = 5;
    public static final int IDX_MG_ROOK   = 6;
    public static final int IDX_EG_ROOK   = 7;
    public static final int IDX_MG_QUEEN  = 8;
    public static final int IDX_EG_QUEEN  = 9;

    // Bishop pair
    public static final int IDX_MG_BISHOP_PAIR = 10;
    public static final int IDX_EG_BISHOP_PAIR = 11;
    // Doubled pawn
    public static final int IDX_MG_DOUBLED_PAWN = 12;
    public static final int IDX_EG_DOUBLED_PAWN = 13;
    // Isolated pawn
    public static final int IDX_MG_ISOLATED_PAWN = 14;
    public static final int IDX_EG_ISOLATED_PAWN = 15;
    // Passed pawn
    public static final int IDX_MG_PASSED_PAWN = 16;
    public static final int IDX_EG_PASSED_PAWN = 17;
    // Pawn chain
    public static final int IDX_MG_PAWN_CHAIN = 18;
    public static final int IDX_EG_PAWN_CHAIN = 19;
    // King safety (per missing shield pawn)
    public static final int IDX_MG_KING_SHIELD = 20;
    public static final int IDX_EG_KING_SHIELD = 21;
    // Rook open file
    public static final int IDX_MG_ROOK_OPEN = 22;
    public static final int IDX_EG_ROOK_OPEN = 23;
    //Rook semi-open file
    public static final int IDX_MG_ROOK_SEMIOPEN = 24;
    public static final int IDX_EG_ROOK_SEMIOPEN = 25;

    public static final int SIZE = 26;

    public EvalParams() {
        this.values = new int[SIZE];
        mgPst[0] = new int[64];
        egPst[0] = new int[64];

        // defaults
        values[IDX_MG_PAWN] = 100;
        values[IDX_EG_PAWN] = 110;
        values[IDX_MG_KNIGHT] = 320;
        values[IDX_EG_KNIGHT] = 300;
        values[IDX_MG_BISHOP] = 330;
        values[IDX_EG_BISHOP] = 330;
        values[IDX_MG_ROOK] = 500;
        values[IDX_EG_ROOK] = 550;
        values[IDX_MG_QUEEN] = 950;
        values[IDX_EG_QUEEN] = 1000;

        values[IDX_MG_BISHOP_PAIR] = 30;
        values[IDX_EG_BISHOP_PAIR] = 50;
        values[IDX_MG_DOUBLED_PAWN] = -15;
        values[IDX_EG_DOUBLED_PAWN] = -20;
        values[IDX_MG_ISOLATED_PAWN] = -20;
        values[IDX_EG_ISOLATED_PAWN] = -25;
        values[IDX_MG_PASSED_PAWN] = 10;
        values[IDX_EG_PASSED_PAWN] = 20;
        values[IDX_MG_PAWN_CHAIN] = 16;
        values[IDX_EG_PAWN_CHAIN] = 12;
        values[IDX_MG_KING_SHIELD] = -15;
        values[IDX_EG_KING_SHIELD] = -3;
        values[IDX_MG_ROOK_OPEN] = 25;
        values[IDX_EG_ROOK_OPEN] = 15;
        values[IDX_MG_ROOK_SEMIOPEN] = 12;
        values[IDX_EG_ROOK_SEMIOPEN] = 10;

        mgPst[BPiece.pawn] = new int[]{
                0,   0,   0,   0,   0,   0,   0,   0,
                5,  10,  10, -20, -20,  10,  10,   5,
                5,  -5, -10,   0,   0, -10,  -5,   5,
                0,   0,   0,  20,  20,   0,   0,   0,
                5,   5,  10,  25,  25,  10,   5,   5,
                10,  10,  20,  30,  30,  20,  10,  10,
                50,  50,  50,  50,  50,  50,  50,  50,
                0,   0,   0,   0,   0,   0,   0,   0
        };

        egPst[BPiece.pawn] = new int[]{
                0,   0,   0,   0,   0,   0,   0,   0,
                0,   0,   0,   0,   0,   0,   0,   0,
                0,   0,   0,   0,   0,   0,   0,   0,
                0,   0,   0,   0,   0,   0,   0,   0,
                0,   0,   0,   0,   0,   0,   0,   0,
                0,   0,   0,   0,   0,   0,   0,   0,
                0,   0,   0,   0,   0,   0,   0,   0,
                0,   0,   0,   0,   0,   0,   0,   0
        };

        mgPst[BPiece.knight] = new int[]{
                -50, -40, -30, -30, -30, -30, -40, -50,
                -40, -20,   0,   5,   5,   0, -20, -40,
                -30,   5,  10,  15,  15,  10,   5, -30,
                -30,   0,  15,  20,  20,  15,   0, -30,
                -30,   5,  15,  20,  20,  15,   5, -30,
                -30,   0,  10,  15,  15,  10,   0, -30,
                -40, -20,   0,   0,   0,   0, -20, -40,
                -50, -40, -30, -30, -30, -30, -40, -50
        };

        egPst[BPiece.knight] = new int[]{
                -50, -40, -30, -30, -30, -30, -40, -50,
                -40, -20,   0,   0,   0,   0, -20, -40,
                -30,   0,   5,  10,  10,   5,   0, -30,
                -30,   0,  10,  15,  15,  10,   0, -30,
                -30,   0,  10,  15,  15,  10,   0, -30,
                -30,   0,   5,  10,  10,   5,   0, -30,
                -40, -20,   0,   0,   0,   0, -20, -40,
                -50, -40, -30, -30, -30, -30, -40, -50
        };

        mgPst[BPiece.bishop] = new int[]{
                -20, -10, -10, -10, -10, -10, -10, -20,
                -10,   5,   0,   0,   0,   0,   5, -10,
                -10,  10,  10,  10,  10,  10,  10, -10,
                -10,   0,  10,  10,  10,  10,   0, -10,
                -10,   5,   5,  10,  10,   5,   5, -10,
                -10,   0,   5,  10,  10,   5,   0, -10,
                -10,   0,   0,   0,   0,   0,   0, -10,
                -20, -10, -10, -10, -10, -10, -10, -20
        };

        egPst[BPiece.bishop] = new int[]{
                -20, -10, -10, -10, -10, -10, -10, -20,
                -10,   0,   0,   0,   0,   0,   0, -10,
                -10,   0,   5,  10,  10,   5,   0, -10,
                -10,   5,   5,  10,  10,   5,   5, -10,
                -10,   0,  10,  10,  10,  10,   0, -10,
                -10,  10,  10,  10,  10,  10,  10, -10,
                -10,   5,   0,   0,   0,   0,   5, -10,
                -20, -10, -10, -10, -10, -10, -10, -20
        };

        mgPst[BPiece.rook] = new int[]{
                0,   0,   0,   5,   5,   0,   0,   0,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                5,  10,  10,  10,  10,  10,  10,   5,
                0,   0,   0,   0,   0,   0,   0,   0
        };

        egPst[BPiece.rook] = new int[]{
                0,   0,   0,   0,   0,   0,   0,   0,
                5,  10,  10,  10,  10,  10,  10,   5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                -5,   0,   0,   0,   0,   0,   0,  -5,
                0,   0,   0,   5,   5,   0,   0,   0
        };

        mgPst[BPiece.queen] = new int[]{
                -20, -10, -10,  -5,  -5, -10, -10, -20,
                -10,   0,   5,   0,   0,   0,   0, -10,
                -10,   5,   5,   5,   5,   5,   0, -10,
                0,   0,   5,   5,   5,   5,   0,  -5,
                -5,   0,   5,   5,   5,   5,   0,  -5,
                -10,   0,   5,   5,   5,   5,   0, -10,
                -10,   0,   0,   0,   0,   0,   0, -10,
                -20, -10, -10,  -5,  -5, -10, -10, -20
        };

        egPst[BPiece.queen] = new int[]{
                -20, -10, -10,  -5,  -5, -10, -10, -20,
                -10,   0,   0,   0,   0,   0,   0, -10,
                -10,   0,   5,   5,   5,   5,   0, -10,
                -5,   0,   5,   5,   5,   5,   0,  -5,
                0,   0,   5,   5,   5,   5,   0,  -5,
                -10,   5,   5,   5,   5,   5,   0, -10,
                -10,   0,   5,   0,   0,   0,   0, -10,
                -20, -10, -10,  -5,  -5, -10, -10, -20
        };

        mgPst[BPiece.king] = new int[]{
                20,  30,  10,   0,   0,  10,  30,  20,
                20,  20,   0,   0,   0,   0,  20,  20,
                -10, -20, -20, -20, -20, -20, -20, -10,
                -20, -30, -30, -40, -40, -30, -30, -20,
                -30, -40, -40, -50, -50, -40, -40, -30,
                -30, -40, -40, -50, -50, -40, -40, -30,
                -30, -40, -40, -50, -50, -40, -40, -30,
                -30, -40, -40, -50, -50, -40, -40, -30
        };

        egPst[BPiece.king] = new int[]{
                -50, -30, -30, -30, -30, -30, -30, -50,
                -30, -30,   0,   0,   0,   0, -30, -30,
                -30, -10,  20,  30,  30,  20, -10, -30,
                -30, -10,  30,  40,  40,  30, -10, -30,
                -30, -10,  30,  40,  40,  30, -10, -30,
                -30, -10,  20,  30,  30,  20, -10, -30,
                -30, -20, -10,   0,   0, -10, -20, -30,
                -50, -40, -30, -20, -20, -30, -40, -50
        };
    }

    public int[] values;
    public int[][] mgPst = new int[7][64];
    public int[][] egPst = new int[7][64];

    public EvalParams(int[] values,  int[][] mgPst, int[][] egPst) {
        this.values = values;
        this.mgPst = mgPst;
        this.egPst = egPst;
    }
}
