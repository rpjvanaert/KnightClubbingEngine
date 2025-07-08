package knight.clubbing.opening;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class OpeningBookMaker {

    private static long total = 0;
    private static AtomicLong count = new AtomicLong(0);

    public static void main(String[] args) {
        OpeningService openingService = new OpeningService(OpeningService.jdbcUrl);
        //openingService.deleteAll();
        System.out.println(openingService.getAll());

        try {
            Stockfish stockfish = new Stockfish();
            stockfish.start();
            insertOpeningBookEntries(openingService, stockfish, new BBoard());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            Iterable<String> pgns = streamPerPgn();
            total = StreamSupport.stream(pgns.spliterator(), false).count();
            streamPerPgn().forEach(pgn -> executorService.submit(() -> processPgn(pgn, openingService)));
        } finally {
            executorService.shutdown();
        }
        //streamPerPgn().iterator().forEachRemaining(pgn -> processPgn(pgn, openingService));
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
        //if (!PgnParser.isWorthy(pgn))
        //    return;

        //System.out.println(pgn);

        try {
            PgnInfo pgnInfo = PgnParser.parse(pgn, 15);

            createOpeningBookEntries(pgnInfo, openingService);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //System.out.println("Done one");
        long currentCount = count.incrementAndGet();
        System.out.println("Processed: " + currentCount + " of " + total);
        //System.out.println(pgnInfo);

        //BBoard board = new BBoard();


    }

    private static void createOpeningBookEntries(PgnInfo pgnInfo, OpeningService openingService) throws IOException {
        BBoard board = new BBoard();
        Stockfish stockfish = new Stockfish();
        stockfish.start();
        for (BMove move : pgnInfo.moves()) {

            board.makeMove(move, false);

            if (openingService.exists(board.state.getZobristKey()))
                continue;

            insertOpeningBookEntries(openingService, stockfish, board);
        }

    }

    private static void insertOpeningBookEntries(OpeningService openingService, Stockfish stockfish, BBoard board) throws IOException {
        List<OpeningBookEntry> moves = stockfish.topMoves("position fen " + board.exportFen(), board.state.getZobristKey(), 15);
        moves.forEach(openingService::insert);
    }
}
