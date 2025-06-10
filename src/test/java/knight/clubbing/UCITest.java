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

}