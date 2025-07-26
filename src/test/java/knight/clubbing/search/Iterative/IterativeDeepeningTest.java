package knight.clubbing.search.Iterative;

import knight.clubbing.core.BBoard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IterativeDeepeningTest {

    @Test
    void testSearch_Opening1() {
        BBoard board = new BBoard();

        IterativeDeepening searcher = new IterativeDeepening(board);

        SearchResult result = searcher.search(1000);

        assertNotNull(result, "Search result should not be null");
        assertNotEquals(0, result.getEvaluation(), "Evaluation should not be zero");
        assertNotNull(result.getBestMove(), "Best move should not be null");

    }

    @Test
    void testSearch_basic1() {
        BBoard board = new BBoard("r4rk1/pppq1p2/3p1b1Q/4p3/1PPn4/P1NP2P1/4PP1P/R4RK1 w - - 1 17");

        IterativeDeepening searcher = new IterativeDeepening(board);

        SearchResult result = searcher.search(1000);

        assertNotNull(result, "Search result should not be null");
        assertNotEquals(0, result.getEvaluation(), "Evaluation should not be zero");
        assertNotNull(result.getBestMove(), "Best move should not be null");
    }

    @Test
    void testSearch_basic2() {
        BBoard board = new BBoard("2r3k1/p5pp/1p3b2/1P2p3/PBr3P1/6K1/R1n2P1P/3R4 b - - 10 30");

        IterativeDeepening searcher = new IterativeDeepening(board);

        SearchResult result = searcher.search(1000);
        System.out.println("Move: " + result.getBestMove() + ", Evaluation: " + result.getEvaluation());

        assertNotNull(result, "Search result should not be null");
        assertNotEquals(0, result.getEvaluation(), "Evaluation should not be zero");
        assertNotNull(result.getBestMove(), "Best move should not be null");
    }
}