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

    @Test
    void testEvaluateSides() {
        BBoard board;
        board = new BBoard("rnb1kbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assertTrue(Evaluation.evaluate(board) > 0);

        board = new BBoard("rnb1kbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
        assertTrue(Evaluation.evaluate(board) < 0);
    }

    @Test
    void testEvaluateFlip() {
        //Flip color to move
        BBoard boardOriginal = new BBoard("r7/1bk5/8/4N3/5P2/4P1P1/6K1/7R w - - 0 1");
        BBoard boardFlipped = new BBoard("r7/1bk5/8/4N3/5P2/4P1P1/6K1/7R b - - 0 1");

        assertEquals(Evaluation.evaluate(boardOriginal), -Evaluation.evaluate(boardFlipped));

        //Flip whole board incl. color to move
        boardOriginal = new BBoard("r7/1bk5/8/4N3/5P2/4P1P1/6K1/7R w - - 0 1");
        boardFlipped = new BBoard("r7/1k6/1p1p4/2p5/3n4/8/5KB1/7R b - - 0 1");

        assertEquals(Evaluation.evaluate(boardOriginal), Evaluation.evaluate(boardFlipped));
    }

}