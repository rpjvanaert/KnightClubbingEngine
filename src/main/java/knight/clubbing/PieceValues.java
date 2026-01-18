package knight.clubbing;

public class PieceValues {

    private static final int[] EVAL_VALUES = {
        0,    // EMPTY
        100,  // PAWN
        310,  // KNIGHT
        330,  // BISHOP
        500,  // ROOK
        950,  // QUEEN
        0     // KING
    };

    private static final int[] MVVLVA_VALUES = {
        0,      // EMPTY
        100,    // PAWN
        320,    // KNIGHT
        330,    // BISHOP
        500,    // ROOK
        900,    // QUEEN
        0       // KING
    };

    // Indexed by piece type (BPiece.queen f.e.)
    private final int[] values;

    public PieceValues(int[] values) {
        if (values == null || values.length != 7) {
            throw new IllegalArgumentException("value arrays must be length 7");
        }
        this.values = values.clone();
    }

    public int value(int pieceType) {
        return values[pieceType];
    }

    public static PieceValues material() {
        return new PieceValues(EVAL_VALUES);
    }

    public static PieceValues mvvLva() {
        return new PieceValues(MVVLVA_VALUES);
    }
}
