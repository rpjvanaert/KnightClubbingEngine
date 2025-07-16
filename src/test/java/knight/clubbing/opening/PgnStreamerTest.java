package knight.clubbing.opening;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PgnStreamerTest {

    @Test
    void basic() throws IOException {
        String input = """
                [Event "Rated Classical game"]
                [Site "https://lichess.org/dgBq4TcZ"]
                [White "edgarro"]
                [Black "CLE9595"]
                [Result "0-1"]
                [UTCDate "2016.11.30"]
                [UTCTime "23:00:00"]
                [WhiteElo "3000"]
                [BlackElo "1539"]
                [WhiteRatingDiff "-11"]
                [BlackRatingDiff "+10"]
                [ECO "C00"]
                [Opening "French Defense #2"]
                [TimeControl "300+8"]
                [Termination "Normal"]
                
                1. e4 e6 2. Qf3 d5 3. exd5 exd5 4. Bb5+ Nc6 5. Bxc6+ bxc6 6. Ne2 Nf6 7. h3 Bd6 8. d4 O-O 9. Bd2 Rb8 10. b3 Re8 11. Nc3 Ba3 12. Rb1 Bb4 13. Na4 Bxd2+ 14. Kxd2 Ne4+ 15. Kd3 Ba6+ 16. c4 dxc4+ 17. bxc4 Rxb1 18. Rxb1 Nd6 19. Qxc6 Bxc4+ 20. Kc3 Rxe2 21. Nc5 h6 22. a4 Rxf2 23. Rb4 Bf1 24. g3 Bg2 25. Qa6 Rf3+ 26. Kd2 Qg5+ 27. Kc2 Rf2+ 28. Kc3 Qd2+ 29. Kb3 Rf3+ 0-1
                
                [Event "Rated Classical tournament https://lichess.org/tournament/egN9tCMI"]
                [Site "https://lichess.org/5NLLFQRo"]
                [White "ultrafurious"]
                [Black "drcliffhanger"]
                [Result "1-0"]
                [UTCDate "2016.11.30"]
                [UTCTime "23:00:01"]
                [WhiteElo "3000"]
                [BlackElo "1958"]
                [WhiteRatingDiff "+13"]
                [BlackRatingDiff "-11"]
                [ECO "A43"]
                [Opening "Benoni Defense: Old Benoni"]
                [TimeControl "60+0"]
                [Termination "Time forfeit"]
                
                1. d4 c5 2. d5 d6 3. Nc3 Nf6 4. e4 g6 5. Bb5+ Bd7 6. a4 Bg7 7. Nf3 O-O 8. h3 a6 9. Be2 e6 10. O-O exd5 11. exd5 Re8 12. Nd2 Bf5 13. Nc4 Ne4 14. Nxe4 Bxe4 15. Bf4 Bf8 16. c3 Qc7 17. a5 Nd7 18. Bf3 Bxf3 19. Qxf3 b5 20. axb6 Nxb6 21. Na5 Bg7 22. Nc6 Nc4 23. b3 Ne5 24. Bxe5 Bxe5 25. Rfe1 Kg7 26. Nxe5 Rxe5 27. Rxe5 dxe5 28. d6 Qd8 29. c4 Ra7 30. Rd1 Rd7 31. Qd5 f6 32. Qxc5 1-0
                """;

        PgnStreamer pgnStreamer = new PgnStreamer(new ByteArrayInputStream(input.getBytes()));

        List<String> pgns = new ArrayList<>();
        for (String pgn : pgnStreamer) {
            pgns.add(pgn);
        }
        pgnStreamer.close();

        assertEquals(2, pgns.size());
    }

    @Test
    void notWorthy() throws IOException {
        String input = """
                [Event "Rated Blitz game"]
                [Site "https://lichess.org/dgBq4TcZ"]
                [White "edgarro"]
                [Black "CLE9595"]
                [Result "0-1"]
                [UTCDate "2016.11.30"]
                [UTCTime "23:00:00"]
                [WhiteElo "3000"]
                [BlackElo "1539"]
                [WhiteRatingDiff "-11"]
                [BlackRatingDiff "+10"]
                [ECO "C00"]
                [Opening "French Defense #2"]
                [TimeControl "300+8"]
                [Termination "Normal"]
                
                1. e4 e6 2. Qf3 d5 3. exd5 exd5 4. Bb5+ Nc6 5. Bxc6+ bxc6 6. Ne2 Nf6 7. h3 Bd6 8. d4 O-O 9. Bd2 Rb8 10. b3 Re8 11. Nc3 Ba3 12. Rb1 Bb4 13. Na4 Bxd2+ 14. Kxd2 Ne4+ 15. Kd3 Ba6+ 16. c4 dxc4+ 17. bxc4 Rxb1 18. Rxb1 Nd6 19. Qxc6 Bxc4+ 20. Kc3 Rxe2 21. Nc5 h6 22. a4 Rxf2 23. Rb4 Bf1 24. g3 Bg2 25. Qa6 Rf3+ 26. Kd2 Qg5+ 27. Kc2 Rf2+ 28. Kc3 Qd2+ 29. Kb3 Rf3+ 0-1
                
                [Event "Rated Classical tournament https://lichess.org/tournament/egN9tCMI"]
                [Site "https://lichess.org/5NLLFQRo"]
                [White "ultrafurious"]
                [Black "drcliffhanger"]
                [Result "1-0"]
                [UTCDate "2016.11.30"]
                [UTCTime "23:00:01"]
                [WhiteElo "1947"]
                [BlackElo "1958"]
                [WhiteRatingDiff "+13"]
                [BlackRatingDiff "-11"]
                [ECO "A43"]
                [Opening "Benoni Defense: Old Benoni"]
                [TimeControl "60+0"]
                [Termination "Time forfeit"]
                
                1. d4 c5 2. d5 d6 3. Nc3 Nf6 4. e4 g6 5. Bb5+ Bd7 6. a4 Bg7 7. Nf3 O-O 8. h3 a6 9. Be2 e6 10. O-O exd5 11. exd5 Re8 12. Nd2 Bf5 13. Nc4 Ne4 14. Nxe4 Bxe4 15. Bf4 Bf8 16. c3 Qc7 17. a5 Nd7 18. Bf3 Bxf3 19. Qxf3 b5 20. axb6 Nxb6 21. Na5 Bg7 22. Nc6 Nc4 23. b3 Ne5 24. Bxe5 Bxe5 25. Rfe1 Kg7 26. Nxe5 Rxe5 27. Rxe5 dxe5 28. d6 Qd8 29. c4 Ra7 30. Rd1 Rd7 31. Qd5 f6 32. Qxc5 1-0
                
                [Event "Rated Classical tournament https://lichess.org/tournament/lbbO1bEa"]
                [Site "https://lichess.org/lGF1wCmJ"]
                [White "Guitarplaya"]
                [Black "lefoudelavalleenoire"]
                [Result "0-1"]
                [UTCDate "2016.11.30"]
                [UTCTime "23:00:01"]
                [WhiteElo "3000"]
                [BlackElo "3000"]
                [WhiteRatingDiff "-12"]
                [BlackRatingDiff "+12"]
                [ECO "?"]
                [Opening "?"]
                [TimeControl "180+0"]
                [Termination "Abandoned"]
                
                 0-1
                """;

        PgnStreamer pgnStreamer = new PgnStreamer(new ByteArrayInputStream(input.getBytes()));

        List<String> pgns = new ArrayList<>();
        for (String pgn : pgnStreamer) {
            pgns.add(pgn);
        }
        pgnStreamer.close();

        assertEquals(0, pgns.size());

    }
}