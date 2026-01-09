package knight.clubbing.evaluation;

import knight.clubbing.core.BBoard;
import knight.clubbing.evaluation.CpuEvaluator;
import knight.clubbing.evaluation.EvalFeature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpuEvaluatorTest {

    @Test
    void evaluate() {
        EvalFeature f1 = new EvalFeature() {
            @Override
            public int compute(BBoard board) {
                return 100;
            }
            @Override
            public String name() {
                return "TestFeature1";
            }
        };
        EvalFeature f2 = new EvalFeature() {
            @Override
            public int compute(BBoard board) {
                return 20;
            }
            @Override
            public String name() {
                return "TestFeature2";
            }
        };

        CpuEvaluator evaluator = new CpuEvaluator(new EvalFeature[]{f1, f2});

        BBoard whiteBoard = new BBoard();
        whiteBoard.isWhiteToMove = true;
        assertEquals(120, evaluator.evaluate(whiteBoard), "white to move should see positive sum");

        BBoard blackBoard = new BBoard();
        blackBoard.isWhiteToMove = false;
        assertEquals(-120, evaluator.evaluate(blackBoard), "black to move should see negated sum");
    }

    @Test
    void evaluateBatch() {
        EvalFeature f = new EvalFeature() {
            @Override
            public int compute(BBoard board) {
                return 1;
            }
            @Override
            public String name() {
                return "TestFeature1";
            }
        };

        CpuEvaluator evaluator = new CpuEvaluator(new EvalFeature[]{f});

        BBoard w = new BBoard();
        w.isWhiteToMove = true;
        BBoard b = new BBoard();
        b.isWhiteToMove = false;

        int[] actual = evaluator.evaluateBatch(new BBoard[]{w, b});
        assertArrayEquals(new int[]{1, -1}, actual);
    }

}