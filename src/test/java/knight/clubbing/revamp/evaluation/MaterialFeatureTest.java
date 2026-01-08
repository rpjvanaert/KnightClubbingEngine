package knight.clubbing.revamp.evaluation;

import knight.clubbing.core.BBoard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialFeatureTest {

    private static void testCompute(String fen, int expectedScore) {
        BBoard b = new BBoard(fen);
        MaterialFeature materialFeature = new MaterialFeature();

        int score = materialFeature.compute(b);

        assertEquals(expectedScore, score);
    }

    @Test
    void testStartPos() {
        testCompute("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 0);
    }

    @Test
    void testWhite() {
        testCompute("8/8/8/8/8/8/PPPPPPPP/RNBQKBNR w KQha - 0 1", 4030);
    }

    @Test
    void testBlack() {
        testCompute("rnbqkbnr/pppppppp/8/8/8/8/8/8 w HAkq - 0 1", -4030);
    }

    @Test
    void testScenario1() {
        testCompute("rnbqkb1r/ppp2ppp/3p4/8/3Pn3/5N2/PPP2PPP/RNBQKB1R b KQkq - 0 1", 0);
    }

    @Test
    void testScenario2() {
        testCompute("1k1r4/1pp5/p1br1p2/6p1/2P5/1P2N3/4QPPP/6K1 b - - 0 1", -70);
    }
}