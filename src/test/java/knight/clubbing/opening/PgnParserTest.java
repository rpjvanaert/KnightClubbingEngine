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
    void determineMoveFromSan_basic1() {
        BBoard board = new BBoard("r2q1rk1/ppp1bppp/2n2n2/3p2B1/3P2b1/2N1PN2/PP2BPPP/R2Q1RK1 b - - 3 9");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "h6";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("h7"), BBoardHelper.stringCoordToIndex("h6"));

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_basic2() {
        BBoard board = new BBoard("r4rk1/p1pqbppp/2pp1n2/4p1B1/4P1b1/2NP1N2/PPPQ1PPP/R4RK1 w - - 5 10");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "Ne1";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("f3"), BBoardHelper.stringCoordToIndex("e1"));

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_take1() {
        BBoard board = new BBoard("r2q1rk1/ppp1bppp/2n2n2/3p2B1/3P2b1/2N1PN2/PP2BPPP/R2Q1RK1 b - - 3 9");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "Bxf3";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("g4"), BBoardHelper.stringCoordToIndex("f3"));

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_take2() {
        BBoard board = new BBoard("r4rk1/p1pqbppp/2pp1n2/4p1B1/4P1b1/2NP1N2/PPPQ1PPP/R4RK1 w - - 5 10");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "Nxe5";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("f3"), BBoardHelper.stringCoordToIndex("e5"));

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_check1() {
        BBoard board = new BBoard("rnbqkbnr/ppp1pppp/8/8/2pP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "Qa4+";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("d1"), BBoardHelper.stringCoordToIndex("a4"));

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_check2() {
        BBoard board = new BBoard("rnbqkbnr/pp2pppp/8/2Pp4/8/5N2/PPP1PPPP/RNBQKB1R b KQkq - 1 4");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "Qa5+";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("d8"), BBoardHelper.stringCoordToIndex("a5"));

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_castleShort() {
        BBoard board = new BBoard("rnbqk2r/ppppbppp/5n2/4p3/4P3/5N2/PPPPBPPP/RNBQK2R w KQkq - 4 4");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "O-O";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("e1"), BBoardHelper.stringCoordToIndex("g1"), BMove.castleFlag);

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_castleLong() {
        BBoard board = new BBoard("r3kbnr/ppp1qppp/2npb3/4p3/4P3/2NP1N2/PPP1BPPP/R1BQ1RK1 b kq - 0 6");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "O-O-O";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("e8"), BBoardHelper.stringCoordToIndex("c8"), BMove.castleFlag);

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_mate1() {
        BBoard board = new BBoard("6k1/R7/1R6/8/8/3K4/8/8 w - - 0 1");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "Rb8#";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("b6"), BBoardHelper.stringCoordToIndex("b8"));

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_enPassant() {
        BBoard board = new BBoard("rnbqkbnr/ppp1ppp1/7p/2Pp4/8/8/PP1PPPPP/RNBQKBNR w KQkq d6 0 3");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "cxd6";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("c5"), BBoardHelper.stringCoordToIndex("d6"), BMove.enPassantCaptureFlag);

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_promotionQueen() {
        BBoard board = new BBoard("8/1P4k1/8/8/8/8/8/6K1 w - - 0 1");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "b8=Q";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("b7"), BBoardHelper.stringCoordToIndex("b8"), BMove.promoteToQueenFlag);

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_promotionRook() {
        BBoard board = new BBoard("8/1P4k1/8/8/8/8/8/6K1 w - - 0 1");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "b8=R";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("b7"), BBoardHelper.stringCoordToIndex("b8"), BMove.promoteToRookFlag);

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_promotionBishop() {
        BBoard board = new BBoard("8/1P4k1/8/8/8/8/8/6K1 w - - 0 1");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "b8=B";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("b7"), BBoardHelper.stringCoordToIndex("b8"), BMove.promoteToBishopFlag);

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_promotionKnight() {
        BBoard board = new BBoard("8/1P4k1/8/8/8/8/8/6K1 w - - 0 1");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "b8=N";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("b7"), BBoardHelper.stringCoordToIndex("b8"), BMove.promoteToKnightFlag);

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void determineMoveFromSan_promotionQueen_check() {
        BBoard board = new BBoard("6k1/1P6/8/8/8/8/8/6K1 w - - 0 1");
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        String san = "b8=Q+";
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex("b7"), BBoardHelper.stringCoordToIndex("b8"), BMove.promoteToQueenFlag);

        BMove result = PgnParser.determineMoveFromSan(san, possibleMoves, board);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    //@Test
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