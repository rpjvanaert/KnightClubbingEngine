package knight.clubbing.evaluation;

import knight.clubbing.core.BBoard;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Disabled // Disable all tests in this class due to weights changing. Validity tested by SPRT
class PstFeatureTest {

    @Test
    void testMirror() {
        assertEquals(7, PstFeature.mirror(63));
        assertEquals(63, PstFeature.mirror(7));
        assertEquals(0, PstFeature.mirror(56));
        assertEquals(56, PstFeature.mirror(0));
    }

    private static void testCompute(String fen, int expectedScore) {
        BBoard b = new BBoard(fen);
        PstFeature pstFeature = new PstFeature();

        int score = pstFeature.compute(b);

        assertEquals(expectedScore, score);
    }

    /**
     * *** Pawn ***
     */

    @Test
    void testPawn() {
        testCompute("8/1P1p4/3P4/8/8/4p3/4P1p1/8 w - - 0 1", 0);
    }

    @Test
    void testPawnWhite() {
        testCompute("8/1P6/3P4/8/3PP3/2P4P/3PP3/8 w - - 0 1", 75);
    }

    @Test
    void testPawnBlack() {
        testCompute("8/3pp3/2p4p/3pp3/8/4p3/1p6/8 w - - 0 1", -75);
    }

    /**
     * *** Knight ***
     */

    @Test
    void testKnight() {
        testCompute("7n/6N1/2Nn4/3Nn3/3nN3/2nN4/6n1/7N w - - 0 1", 0);
    }

    @Test
    void testKnightWhite() {
        testCompute("6N1/8/5N2/1N1N4/5N2/8/8/8 w - - 0 1", 10);
    }

    @Test
    void testKnightBlack() {
        testCompute("8/8/8/5n2/1n1n4/5n2/8/6n1 w - - 0 1", -10);
    }

    /**
     * *** Bishop ***
     */

    @Test
    void testBishop() {
        testCompute("7b/6b1/8/1B2B3/1b2b3/8/6B1/7B w - - 0 1", 0);
    }

    @Test
    void testBishopWhite() {
        testCompute("8/8/8/1B2B3/8/8/6B1/7B w - - 0 1", 10);
    }

    @Test
    void testBishopBlack() {
        testCompute("7b/6b1/8/8/1b2b3/8/8/8 w - - 0 1", -10);
    }

    /**
     * *** Rook ***
     */

    @Test
    void testRook() {
        testCompute("3rR3/R2Rr3/8/7R/7r/8/r2rR3/3Rr3 w - - 0 1", 0);
    }

    @Test
    void testRookWhite() {
        testCompute("4R3/R2R4/8/7R/8/8/4R3/3R4 w - - 0 1", 15);
    }

    @Test
    void testRookBlack() {
        testCompute("3r4/4r3/8/8/7r/8/r2r4/4r3 w - - 0 1", -15);
    }

    /**
     * *** Queen ***
     */

    @Test
    void testQueen() {
        testCompute("7Q/1q6/3Q4/5q2/5Q2/3q4/1Q6/7q w - - 0 1", 0);
    }

    @Test
    void testQueenWhite() {
        testCompute("7Q/8/3Q4/8/5Q2/8/1Q6/8 w - - 0 1", -10);
    }

    @Test
    void testQueenBlack() {
        testCompute("8/1q6/8/5q2/8/3q4/8/7q w - - 0 1", 10);
    }

    /**
     * *** King ***
     */

    @Test
    void testKing() {
        testCompute("6k1/1k2k3/8/3K3k/3k3K/8/1K2K3/6K1 w - - 0 1", 0);
    }

    @Test
    void testKingWhite() {
        testCompute("8/8/8/3K4/7K/8/1K2K3/6K1 w - - 0 1", -20);
    }

    @Test
    void testKingBlack() {
        testCompute("6k1/1k2k3/8/7k/3k4/8/8/8 w - - 0 1", 20);
    }

    /**
     * *** Scenarios ***
     */

    @Test
    void testScenario1() {
        testCompute("r1bqkbnr/pppp1ppp/2n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 0 1", 15);
    }
}