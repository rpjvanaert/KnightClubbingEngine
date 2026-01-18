package knight.clubbing.evaluation;

import knight.clubbing.core.BBoard;

public interface Evaluator {
    int evaluate(BBoard board);
    int[] evaluateBatch(BBoard[] boards);
}
