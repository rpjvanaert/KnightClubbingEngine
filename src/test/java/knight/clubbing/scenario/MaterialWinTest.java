package knight.clubbing.scenario;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class MaterialWinTest extends AbstractScenarioTest {

    /*
    -----   Basic captures   -----
     */

    @Test
    @Tag("strength")
    void knightWin() throws InterruptedException {
        expectMoveInPosition("1r3rk1/2pb1pp1/pbnp1q1p/1p1P4/4P3/1B3N1P/PP3PP1/R1BQ1RK1 w - - 0 1", "d5", "c6");
    }

    /*
    -----   Forks   -----
     */

    @Test
    @Tag("strength")
    void fork1() throws InterruptedException {
        expectMoveInPosition("4rrk1/pp2Qpbp/1q4p1/4Nb2/4p3/1B6/PPP2PPP/2KR3R w - - 0 1", "b3", "f7");
    }

    @Test
    @Tag("strength")
    void fork2() throws InterruptedException {
        expectMoveInPosition("7r/5p2/p2Ppkp1/1pr1N2p/7P/P7/1P4P1/1K1R4 w - - 0 1", "e5", "d7");
    }

    @Test
    @Tag("strength")
    void fork2_beforeTrade() throws InterruptedException {
        expectMoveInPosition("7r/5pk1/p2Ppqp1/1pr1N2p/5Q1P/P7/1P4P1/1K1R4 w - - 0 1", "f4", "f6");
    }

    @Test
    @Tag("strength")
    void fork3() throws InterruptedException {
        expectMoveInPosition("b2k2nr/3p2Rp/p7/1pq2p1Q/8/3BP3/2P2P1P/1R2K3 b - - 0 1", "c5", "c3");
    }

    @Test
    @Tag("strength")
    void fork4() throws InterruptedException {
        expectMoveInPosition("2q2k2/5p2/3p4/2nN4/1Q5P/1PK5/r7/6R1 w - - 0 1", "g1", "g8");
    }

    /*
    -----   Pins   -----
     */

    @Test
    @Tag("strength")
    void pin1() throws InterruptedException {
        expectMoveInPosition("1r1k2nr/2p2ppp/p1Qp1q2/1pb4b/4P3/1B2BP1P/PP3P1K/RN4R1 w - - 0 1", "e3", "g5");
    }

    /*
    -----   Remove defender   -----
     */

    @Test
    @Tag("strength")
    void removeDefender1() throws InterruptedException {
        expectMoveInPosition("r4rk1/pp2bppp/n1pp1n2/q5B1/2PQP1b1/2N2N1P/PP2BPP1/3RR1K1 b - - 0 1", "g4", "f3");
    }

    @Test
    @Tag("strength")
    void removeDefender2() throws InterruptedException {
        expectMoveInPosition("r2q1rk1/pp1n1ppp/2p2n2/4p1B1/2B1P3/2P2Q2/P1P2PPP/2KR3R w - - 0 1", "g5", "f6");
    }

    /*
    -----   Trap piece   -----
     */

    @Test
    @Tag("strength")
    void trappedPiece1() throws InterruptedException {
        expectMoveInPosition("8/Q3bk2/4p1p1/3pPp1p/3P4/5KPr/8/8 w - - 0 1", "f3", "g2");
    }

    /*
    -----   Hanging piece   -----
     */

    @Test
    @Tag("strength")
    void hangingPiece1() throws InterruptedException {
        expectMoveInPosition("2R2rk1/1p1n1ppb/p6p/q2pP3/3N4/1P4P1/PQ3PBP/4R1K1 b - - 0 1", "a5", "e1");
    }

    @Test
    @Tag("strength")
    void hangingPiece2() throws InterruptedException {
        expectMoveInPosition("r3r1k1/2pN1p1p/1pP2bp1/pP1p1p2/P2P4/q3P2P/5PP1/R4RK1 w - - 0 1", "d7", "f6");
    }

    @Test
    @Tag("strength")
    void hangingPiece3() throws InterruptedException {
        expectMoveInPosition("r4rk1/1p3p1p/p2p1Pp1/2p1q3/7Q/1P1P1R1P/P1P3P1/R5K1 b - - 0 1", "e5", "a1");
    }
}
