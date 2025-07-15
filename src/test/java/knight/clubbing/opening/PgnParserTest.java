package knight.clubbing.opening;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BBoardHelper;
import knight.clubbing.core.BMove;
import knight.clubbing.movegen.MoveGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class PgnParserTest {

    @ParameterizedTest(name = "needDisambiguation for {1}{2} should be {3}")
    @CsvSource({
            "rnbqkb1r/ppp1pppp/5n2/3p4/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 0 1, f3, d2, true",
            "rnbqkb1r/ppp1pppp/5n2/3p4/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 0 1, b1, d2, true",
            "r2qkb1r/1pp1npp1/p4n1p/P2Pp3/2B3b1/1QPP1N2/1P1N1PPP/R1B1K2R b KQkq - 0 10, f6, d5, true",
            "rnbqkbnr/ppp1pppp/8/3p4/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1, b1, d2, false",
            "rnbqkbnr/ppp1pppp/8/3p4/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1, c2, c4, false"
    })
    void needDisambiguation(String fen, String from, String to, boolean expected) {
        BBoard board = new BBoard(fen);
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove move = new BMove(BBoardHelper.stringCoordToIndex(from), BBoardHelper.stringCoordToIndex(to));
        BMove[] possibleMoves = moveGenerator.generateMoves(false);

        boolean result = PgnParser.needsDisambiguation(board, move, possibleMoves);

        assertEquals(expected, result);
    }

    /*
        ---     Flags for BMove:    ---
        noFlag = 0
        enPassantCaptureFlag = 1
        castleFlag = 2
        pawnTwoUpFlag = 3
        promoteToQueenFlag = 4
        promoteToKnightFlag = 5
        promoteToRookFlag = 6
        promoteToBishopFlag = 7
     */
    @ParameterizedTest(name = "getDisambiguation for {1}{2}(flag {3} should be {4})")
    @CsvSource({
            "rnbqkb1r/ppp1pppp/5n2/3p4/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 0 1, f3, d2, 0, f",
            "rnbqkb1r/ppp1pppp/5n2/3p4/3P4/5N2/PPP1PPPP/RNBQKB1R w KQkq - 0 1, b1, d2, 0, b",
            "r2qkb1r/1pp1npp1/p4n1p/P2Pp3/2B3b1/1QPP1N2/1P1N1PPP/R1B1K2R b KQkq - 0 10, f6, d5, 0, f",
            "k7/8/8/2N3N1/8/6N1/8/K7 w - - 0 1, g5, e4, 0, g5",
            "r2qkbnr/pp3ppp/2n1p3/3pP3/2pP4/2P1BN2/PP3PPP/RN1QK2R b KQkq - 1 9, g8, e7, 0, g"
    })
    void getDisambiguation(String fen, String from, String to, int flag, String expected) {
        BBoard board = new BBoard(fen);
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove move = new BMove(BBoardHelper.stringCoordToIndex(from), BBoardHelper.stringCoordToIndex(to), flag);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);

        String result = PgnParser.getDisambiguation(board, move, possibleMoves);

        assertEquals(expected, result);
    }

    /*
        ---     Flags for BMove:    ---
        noFlag = 0
        enPassantCaptureFlag = 1
        castleFlag = 2
        pawnTwoUpFlag = 3
        promoteToQueenFlag = 4
        promoteToKnightFlag = 5
        promoteToRookFlag = 6
        promoteToBishopFlag = 7
     */
    @ParameterizedTest(name = "determineMoveFromSan for {1} - BMove:{2}{3} (flag {4})")
    @CsvSource({
            "r2q1rk1/ppp1bppp/2n2n2/3p2B1/3P2b1/2N1PN2/PP2BPPP/R2Q1RK1 b - - 3 9, h6, h7, h6, 0",
            "r4rk1/p1pqbppp/2pp1n2/4p1B1/4P1b1/2NP1N2/PPPQ1PPP/R4RK1 w - - 5 10, Ne1, f3, e1, 0",
            "r2q1rk1/ppp1bppp/2n2n2/3p2B1/3P2b1/2N1PN2/PP2BPPP/R2Q1RK1 b - - 3 9, Bxf3, g4, f3, 0",
            "r4rk1/p1pqbppp/2pp1n2/4p1B1/4P1b1/2NP1N2/PPPQ1PPP/R4RK1 w - - 5 10, Nxe5, f3, e5, 0",
            "rnbqkbnr/ppp1pppp/8/8/2pP4/8/PP2PPPP/RNBQKBNR w KQkq - 0 3, Qa4+, d1, a4, 0",
            "rnbqkbnr/pp2pppp/8/2Pp4/8/5N2/PPP1PPPP/RNBQKB1R b KQkq - 1 4, Qa5+, d8, a5, 0",
            "rnbqk2r/ppppbppp/5n2/4p3/4P3/5N2/PPPPBPPP/RNBQK2R w KQkq - 4 4, O-O, e1, g1, 2",
            "r3kbnr/ppp1qppp/2npb3/4p3/4P3/2NP1N2/PPP1BPPP/R1BQ1RK1 b kq - 0 6, O-O-O, e8, c8, 2",
            "6k1/R7/1R6/8/8/3K4/8/8 w - - 0 1, Rb8#, b6, b8, 0",
            "rnbqkbnr/ppp1ppp1/7p/2Pp4/8/8/PP1PPPPP/RNBQKBNR w KQkq d6 0 3, cxd6, c5, d6, 1",
            "8/1P4k1/8/8/8/8/8/6K1 w - - 0 1, b8=Q, b7, b8, 4",
            "8/1P4k1/8/8/8/8/8/6K1 w - - 0 1, b8=R, b7, b8, 6",
            "8/1P4k1/8/8/8/8/8/6K1 w - - 0 1, b8=B, b7, b8, 7",
            "8/1P4k1/8/8/8/8/8/6K1 w - - 0 1, b8=N, b7, b8, 5",
            "6k1/1P6/8/8/8/8/8/6K1 w - - 0 1, b8=Q+, b7, b8, 4"
    })
    void determineMoveFromSan(String fen, String san, String fromExpected, String toExpected, int flagExpected) {
        BBoard board = new BBoard(fen);
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] possibleMoves = moveGenerator.generateMoves(false);
        BMove expected = new BMove(BBoardHelper.stringCoordToIndex(fromExpected), BBoardHelper.stringCoordToIndex(toExpected), flagExpected);

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