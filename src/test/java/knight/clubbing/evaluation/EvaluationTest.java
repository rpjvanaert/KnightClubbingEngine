package knight.clubbing.evaluation;

import knight.clubbing.core.BBoard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationTest {

    @Test
    void testEvaluate() {
        BBoard board = new BBoard();
        assertEquals(0, Evaluation.evaluate(board));
    }


}