package knight.clubbing.revamp.evaluation;

import knight.clubbing.core.BBoard;

public class CpuEvaluator implements Evaluator {

    private final EvalFeature[] features;

    public CpuEvaluator() {
        this(
                new MaterialFeature(),
                new PstFeature()
        );
    }

    public CpuEvaluator(EvalFeature... features) {
        this.features = features;
    }

    @Override
    public int evaluate(BBoard board) {
        int score = 0;

        for (EvalFeature feature : features) {
            score += feature.compute(board);
        }

        return board.isWhiteToMove ? score : -score;
    }

    @Override
    public int[] evaluateBatch(BBoard[] boards) {
        int[] scores = new int[boards.length];
        for (int i = 0; i < boards.length; i++) scores[i] = evaluate(boards[i]);
        return scores;
    }
}
