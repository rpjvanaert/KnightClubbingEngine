package knight.clubbing.revamp.evaluation;

import knight.clubbing.core.BBoard;

public interface EvalFeature {
    int compute(BBoard board);
    String name();
}
