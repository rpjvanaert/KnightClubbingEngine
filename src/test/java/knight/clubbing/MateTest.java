package knight.clubbing;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class MateTest extends AbstractScenarioTest {

    @Test
    @Tag("strength")
    void mateIn1_1() throws InterruptedException {
        expectMoveInPosition("4r2k/1p3rbp/2p1N1p1/p3n3/P2NB1nq/1P6/4R1P1/B1Q2RK1 b - - 4 32", "h4", "h2");
    }

    @Test
    @Tag("strength")
    void mateIn2_1() throws InterruptedException {
        expectMoveInPosition("r1bq2r1/b4pk1/p1pp1p2/1p2pP2/1P2P1PB/3P4/1PPQ2P1/R3K2R w - - 0 1", "d2", "h6");
    }

    @Test
    @Tag("strength")
    void mateIn2_2() throws InterruptedException {
        expectMoveInPosition("kbK5/pp6/1P6/8/8/8/8/R7 w - - 0 1", "a1", "a6");
    }

    @Test
    @Tag("strength")
    void mateIn3_1() throws InterruptedException {
        expectMoveInPosition("r5rk/5p1p/5R2/4B3/8/8/7P/7K w - - 0 1", "f6", "a6");
    }
}
