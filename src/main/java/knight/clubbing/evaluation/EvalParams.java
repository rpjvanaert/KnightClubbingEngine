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

    // Empty idx: 16, 17

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
        values[IDX_MG_PAWN] = 13;
        values[IDX_EG_PAWN] = 16;
        values[IDX_MG_KNIGHT] = 1;
        values[IDX_EG_KNIGHT] = 114;
        values[IDX_MG_BISHOP] = -6;
        values[IDX_EG_BISHOP] = 111;
        values[IDX_MG_ROOK] = 24;
        values[IDX_EG_ROOK] = 160;
        values[IDX_MG_QUEEN] = 442;
        values[IDX_EG_QUEEN] = 601;
        values[IDX_MG_BISHOP_PAIR] = -1;
        values[IDX_EG_BISHOP_PAIR] = -13;
        values[IDX_MG_DOUBLED_PAWN] = -2;
        values[IDX_EG_DOUBLED_PAWN] = 2;
        values[IDX_MG_ISOLATED_PAWN] = 5;
        values[IDX_EG_ISOLATED_PAWN] = -7;
        values[IDX_MG_PAWN_CHAIN] = 1;
        values[IDX_EG_PAWN_CHAIN] = 0;
        values[IDX_MG_KING_SHIELD] = 4;
        values[IDX_EG_KING_SHIELD] = -11;
        values[IDX_MG_ROOK_OPEN] = -24;
        values[IDX_EG_ROOK_OPEN] = -3;
        values[IDX_MG_ROOK_SEMIOPEN] = -27;
        values[IDX_EG_ROOK_SEMIOPEN] = -1;

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

        mgPassedPawnRank = new int[]{0, -21, -17, -11, -3, -3, 6, 0};

        egPassedPawnRank = new int[]{0, 17, 17, 8, 14, 9, 49, 0};

    }

    public int[] values;
    public int[][] mgPst = new int[7][64];
    public int[][] egPst = new int[7][64];

    public int[] mgPassedPawnRank = new int[8];
    public int[] egPassedPawnRank = new int[8];

    public EvalParams(int[] values,  int[][] mgPst, int[][] egPst) {
        this.values = values;
        this.mgPst = mgPst;
        this.egPst = egPst;
    }
}
