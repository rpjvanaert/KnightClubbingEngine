package knight.clubbing.opening;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BBoardHelper;
import knight.clubbing.core.BMove;
import knight.clubbing.movegen.MoveGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PgnParserTest {

    @Test
    void needsDisambiguation_true_nfd2() {
        BBoard board = new BBoard("rnbqkb1r/ppp1pppp/5n2/3p4/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 0 1");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove move = new BMove(BBoardHelper.stringCoordToIndex("f3"), BBoardHelper.stringCoordToIndex("d2"));
        BMove[] possibleMoves = moveGenerator.generateMoves(false);

        boolean result = PgnParser.needsDisambiguation(board, move, possibleMoves);

        assertTrue(result);
    }

    @Test
    void needsDisambiguation_true_nbd2() {
        BBoard board = new BBoard("rnbqkb1r/ppp1pppp/5n2/3p4/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 0 1");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove move = new BMove(BBoardHelper.stringCoordToIndex("b1"), BBoardHelper.stringCoordToIndex("d2"));
        BMove[] possibleMoves = moveGenerator.generateMoves(false);

        boolean result = PgnParser.needsDisambiguation(board, move, possibleMoves);

        assertTrue(result);
    }

    @Test
    void needsDisambiguation_true_nfxd5() {
        BBoard board = new BBoard("r2qkb1r/1pp1npp1/p4n1p/P2Pp3/2B3b1/1QPP1N2/1P1N1PPP/R1B1K2R b KQkq - 0 10");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove move = new BMove(BBoardHelper.stringCoordToIndex("f6"), BBoardHelper.stringCoordToIndex("d5"));
        BMove[] possibleMoves = moveGenerator.generateMoves(false);

        boolean result = PgnParser.needsDisambiguation(board, move, possibleMoves);

        assertTrue(result);
    }

    @Test
    void needsDisambiguation_false_knight() {
        BBoard board = new BBoard("rnbqkbnr/ppp1pppp/8/3p4/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove move = new BMove(BBoardHelper.stringCoordToIndex("b1"), BBoardHelper.stringCoordToIndex("d2"));
        BMove[] possibleMoves = moveGenerator.generateMoves(false);

        boolean result = PgnParser.needsDisambiguation(board, move, possibleMoves);

        assertFalse(result);
    }

    @Test
    void needsDisambiguation_false_pawn() {
        BBoard board = new BBoard("rnbqkbnr/ppp1pppp/8/3p4/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove move = new BMove(BBoardHelper.stringCoordToIndex("c2"), BBoardHelper.stringCoordToIndex("c4"));
        BMove[] possibleMoves = moveGenerator.generateMoves(false);

        boolean result = PgnParser.needsDisambiguation(board, move, possibleMoves);

        assertFalse(result);
    }

    @Test
    void test() {
        String pgn = 
                "[Event \"Rated Classical game\"]\n" +
                "[Site \"https://lichess.org/T1wXZOfY\"]\n" +
                "[White \"foytik\"]\n" +
                "[Black \"no_limit\"]\n" +
                "[Result \"1-0\"]\n" +
                "[UTCDate \"2016.12.01\"]\n" +
                "[UTCTime \"02:08:02\"]\n" +
                "[WhiteElo \"2200\"]\n" +
                "[BlackElo \"2303\"]\n" +
                "[WhiteRatingDiff \"+14\"]\n" +
                "[BlackRatingDiff \"-14\"]\n" +
                "[ECO \"B25\"]\n" +
                "[Opening \"Sicilian Defense: Closed Variation, Traditional\"]\n" +
                "[TimeControl \"480+5\"]\n" +
                "[Termination \"Normal\"]\n" +
                "\n" +
                "1. e4 c5 2. Nc3 Nc6 3. d3 Nf6 4. g4 h6 5. h4 d6 6. g5 hxg5 7. Bxg5 Nd4 8. Bg2 g6 9. Nce2 Bg4 10. c3 Nxe2 11. Nxe2 Bh6 12. f3 Bxg5 13. hxg5 Rxh1+ 14. Bxh1 Nd7 15. fxg4 Ne5 16. d4 Nxg4 17. Nf4 Ne3 18. Qe2 cxd4 19. cxd4 Qa5+ 20. Kf2 Qxg5 21. Qxe3 Rc8 22. Rc1 Qh4+ 23. Kg1 Qg5+ 24. Bg2 Rb8 25. e5 Kd7 26. e6+ 1-0";

        PgnInfo pgnInfo = PgnParser.parse(pgn, 15);

        assertNotNull(pgnInfo);
    }
}