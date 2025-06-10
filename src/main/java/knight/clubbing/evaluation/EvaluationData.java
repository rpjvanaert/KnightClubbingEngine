package knight.clubbing.evaluation;

public record EvaluationData(
        int material,
        //int mopUp,
        int pieceSquare,
        int pawnScore//,
        //int pawnShield
) {
    public int sum() {
        return material + /*mopUp +*/ pieceSquare + pawnScore /*+ pawnShield*/;
    }
}
