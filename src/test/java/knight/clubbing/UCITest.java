package knight.clubbing;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UCITest {

    @Tag("uci")
    @Test
    void testIssue_wrongSideToMove() {
        UCI uci = new UCI();
        String positionCommand = "position startpos moves b1a3 b7b5 a3b5 c7c6 b5c3 d7d5 g1f3 d5d4 c3e4 c6c5 e4c5 e7e5 c5d3 b8c6 f3e5 c6e5 d3e5 d8e7 e5f3 e7d6 a2a3 g8f6 h2h3 d6d7 f3e5 d7c6 e5c6 f8e7 c6d4 e8g8 d4b5 f8e8";

        uci.handlePosition(positionCommand);

        assertEquals("r1b1r1k1/p3bppp/5n2/1N6/8/P6P/1PPPPPP1/R1BQKB1R w KQ - 3 17", uci.getBoard().exportFen());
    }

    @Tag("uci")
    @Test
    void testIssue_unkown() {
        UCI uci = new UCI();
        String positionCommand = "position startpos moves d2d4 b8c6 d4d5 c6b4 b1c3 g8f6 a2a3 b4c2 d1c2 f6g4 g1f3 c7c6 d5c6 d7c6 h2h3 g4f6 g2g4 d8a5 c1d2 a5c5 a1c1 f6d7 f1g2 h7h5 e1g1 h5g4 h3g4 c5c4 b2b3 c4g4 c3e4 d7b6 d2e3 b6d5 c2d3 c8f5 f3d4 f5e4 d3e4 g4e4 g2e4 e7e5 d4f3";

        uci.handlePosition(positionCommand);

        assertEquals("r3kb1r/pp3pp1/2p5/3np3/4B3/PP2BN2/4PP2/2R2RK1 b kq - 1 22", uci.getBoard().exportFen());
    }

    @Tag("uci")
    @Test
    void testPositionCommand_issue1() {
        UCI uci = new UCI();
        String positionCommand = "position startpos moves e2e4 e7e5 g1f3 b8c6 d2d4 e5d4 f3d4 c6d4 d1d4 g8f6 e4e5 f6h5";

        uci.handlePosition(positionCommand);

        assertEquals("r1bqkb1r/pppp1ppp/8/4P2n/3Q4/8/PPP2PPP/RNB1KB1R w KQkq - 1 7", uci.getBoard().exportFen());
    }

    @Tag("uci")
    @Test
    void testSequence_issue1() throws InterruptedException {
        UCI uci = new UCI();
        String positionCommand = "position startpos moves e2e4 e7e5 g1f3 b8c6 d2d4 e5d4 f3d4 c6d4 d1d4 g8f6 e4e5 f6h5";

        uci.handlePosition(positionCommand);
        uci.handleGo("go");
        //Thread.sleep(100000);
    }

    @Tag("uci")
    @Test
    void testArenaCommand_issue1() throws InterruptedException {
        UCI uci = new UCI();
        String command1 = "uci";
        String command2 = "isready";
        String command3 = "ucinewgame";
        String command4 = "position startpos";
        String command5 = "position startpos moves c2c4";
        String command6 = "isready";
        String command7 = "go wtime 300000 btime 300000 winc 0 binc 0";


        uci.handleCommand(command1);
        uci.handleCommand(command2);
        uci.handleCommand(command3);
        uci.handleCommand(command4);
        uci.handleCommand(command5);
        uci.handleCommand(command6);
        uci.handleCommand(command7);

        Thread.sleep(100000);
    }
}