package knight.clubbing.opening;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

public class OpeningBookMaker {

    public static void main(String[] args) {
        OpeningService openingService = new OpeningService(OpeningService.jdbcUrl);
        System.out.println(openingService.getAll());

        streamPerPgn().spliterator().forEachRemaining(pgn -> processPgn(pgn, openingService));
    }

    private static Iterable<String> streamPerPgn() {
        try {
            InputStream input = new FileInputStream("C:\\Users\\ralf\\Documents\\Repositories\\GitHub\\KnightClubbing\\KnightClubbingEngine\\src\\main\\resources\\lichess_db_standard_rated_2016-12.pgn");
            return new PgnStreamer(input);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Stream.<String>empty()::iterator;
    }

    private static void processPgn(String pgn, OpeningService openingService) {
        if (!PgnParser.isWorthy(pgn))
            return;

        //System.out.println(pgn);

        try {
            PgnInfo pgnInfo = PgnParser.parse(pgn, 15);

            createOpeningBookEntries(pgnInfo, openingService);

        } catch (Exception e) {
            e.printStackTrace();
        }




        //System.out.println(pgnInfo);

        //BBoard board = new BBoard();


    }

    private static void createOpeningBookEntries(PgnInfo pgnInfo, OpeningService openingService) {
        BBoard board = new BBoard();
        int depth = 0;

        for (BMove move : pgnInfo.moves()) {

            board.makeMove(move, false);

            int score = 0;
            OpeningBookEntry entry = new OpeningBookEntry(board.state.getZobristKey(), move.getUci(), score, ++depth);
        }

    }
}
