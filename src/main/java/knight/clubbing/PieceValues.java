package knight.clubbing;

public class PieceValues {

    public static final int[] EVAL_VALUES = {
        0,    // EMPTY
        100,  // PAWN
        310,  // KNIGHT
        330,  // BISHOP
        500,  // ROOK
        950,  // QUEEN
        0     // KING
    };

    public static final int[] MVVLVA_VALUES = {
        0,      // EMPTY
        100,    // PAWN
        320,    // KNIGHT
        330,    // BISHOP
        500,    // ROOK
        900,    // QUEEN
        0       // KING
    };
}
