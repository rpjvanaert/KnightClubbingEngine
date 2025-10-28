package knight.clubbing.search.Iterative;

import knight.clubbing.core.BBoard;
import knight.clubbing.search.IterativeDeepening;
import knight.clubbing.search.SearchConfig;
import knight.clubbing.search.SearchResult;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class IterativeDeepeningTest {

    public static final long TEN_SECONDS = Duration.ofSeconds(10L).toMillis();

    @Test
    void testSearch_Opening1() {
        BBoard board = new BBoard();

        IterativeDeepening searcher = new IterativeDeepening(board);

        SearchResult result = searcher.search(new SearchConfig(0, TEN_SECONDS, 1));

        assertNotNull(result, "Search result should not be null");
        assertNotEquals(0, result.getEvaluation(), "Evaluation should not be zero");
        assertNotNull(result.getBestMove(), "Best move should not be null");

    }

    @Test
    void testSearch_basic1() {
        BBoard board = new BBoard("r4rk1/pppq1p2/3p1b1Q/4p3/1PPn4/P1NP2P1/4PP1P/R4RK1 w - - 1 17");

        IterativeDeepening searcher = new IterativeDeepening(board);

        SearchResult result = searcher.search(new SearchConfig(0, IterativeDeepeningTest.TEN_SECONDS, 1));

        assertNotNull(result, "Search result should not be null");
        assertNotEquals(0, result.getEvaluation(), "Evaluation for " + result.getBestMove() + " should not be zero");
        assertNotNull(result.getBestMove(), "Best move should not be null");
    }

    @Test
    void testSearch_basic2() {
        BBoard board = new BBoard("2r3k1/p5pp/1p3b2/1P2p3/PBr3P1/6K1/R1n2P1P/3R4 b - - 10 30");

        IterativeDeepening searcher = new IterativeDeepening(board);

        SearchResult result = searcher.search(new SearchConfig(0, TEN_SECONDS, 1));

        System.out.println(result);
        assertNotNull(result, "Search result should not be null");
        assertNotEquals(0, result.getEvaluation(), "Evaluation for " + result.getBestMove() + " should not be zero");
        assertNotNull(result.getBestMove(), "Best move should not be null");
    }

    @Test
    void testSearch_basic2_multiThreaded() {
        BBoard board = new BBoard("2r3k1/p5pp/1p3b2/1P2p3/PBr3P1/6K1/R1n2P1P/3R4 b - - 10 30");

        IterativeDeepening searcher = new IterativeDeepening(board);

        SearchResult result = searcher.search(new SearchConfig(0, TEN_SECONDS, 10));

        System.out.println(result);
        assertNotNull(result, "Search result should not be null");
        assertNotEquals(0, result.getEvaluation(), "Evaluation for " + result.getBestMove() + " should not be zero");
        assertNotNull(result.getBestMove(), "Best move should not be null");
    }

    @Test
    void testSearch_basic3_multiThreaded() {
        BBoard board = new BBoard("r3kbnr/pp2pppp/2n3b1/2P5/8/2P2NN1/P3PPPP/R1BK1B1R b kq - 0 9");

        IterativeDeepening searcher = new IterativeDeepening(board);

        SearchResult result = searcher.search(new SearchConfig(0, TEN_SECONDS, 10));

        System.out.println(result);
        assertNotNull(result, "Search result should not be null");
        assertNotEquals(0, result.getEvaluation(), "Evaluation should not be zero");
        assertNotNull(result.getBestMove(), "Best move should not be null");
    }

    @Test
    void testSearch_basic4_multiThreaded() {
        BBoard board = new BBoard("r2q1bnr/p3kppp/2B5/8/3pN3/8/PPPP1PPP/R1Bb1RK1 w - - 1 11");

        IterativeDeepening searcher = new IterativeDeepening(board);

        SearchResult result = searcher.search(new SearchConfig(0, TEN_SECONDS, 10));

        System.out.println(result);
        assertNotNull(result, "Search result should not be null");
        assertNotEquals(0, result.getEvaluation(), "Evaluation should not be zero");
        assertNotNull(result.getBestMove(), "Best move should not be null");
    }


}