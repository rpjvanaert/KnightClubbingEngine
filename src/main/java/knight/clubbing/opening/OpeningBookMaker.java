package knight.clubbing.opening;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class OpeningBookMaker {

    private static final Logger logger = LoggerFactory.getLogger(OpeningBookMaker.class);

    public static final int STOCKFISH_DEPTH = 15;
    private static long total = 0;
    private static AtomicLong count = new AtomicLong(0);

    public static void main(String[] args) {
        OpeningService openingService = new OpeningService(OpeningService.jdbcUrl);
        logger.info("Starting OpeningBookMaker...");
        logger.info("Current opening book size: {}", openingService.count());

        try {
            Stockfish stockfish = new Stockfish();
            stockfish.start();
            insertOpeningBookEntries(openingService, stockfish, new BBoard());
        } catch (IOException e) {
            logger.error("Error with Stockfish", e);
        }

        try (ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())){
            Iterable<String> pgns = streamPerPgn();
            total = StreamSupport.stream(pgns.spliterator(), false).count();
            streamPerPgn().forEach(pgn -> executorService.submit(() -> processPgn(pgn, openingService)));
        }
    }

    private static Iterable<String> streamPerPgn() {
        try {
            InputStream input = new FileInputStream("/home/ralf/Documents/repositories/github/KnightClubbing/KnightClubbingEngine/src/main/resources/lichess_db_standard_rated_2016-12.pgn");
            return new PgnStreamer(input);

        } catch (IOException e) {
            logger.error("Error reading file", e);
        }


        return Stream.<String>empty()::iterator;
    }

    private static void processPgn(String pgn, OpeningService openingService) {

        try {
            PgnInfo pgnInfo = PgnParser.parse(pgn, 15);

            createOpeningBookEntries(pgnInfo, openingService);

        } catch (Exception e) {
            logger.error("Error parsing pgn", e);
        }
        long currentCount = count.incrementAndGet();
        logger.info("Processing {} of {}", currentCount, total);


    }

    private static void createOpeningBookEntries(PgnInfo pgnInfo, OpeningService openingService) throws IOException {
        BBoard board = new BBoard();
        try (Stockfish stockfish = new Stockfish()){
            stockfish.start();
            Long prevZobristKey = null;

            for (BMove move : pgnInfo.moves()) {

                board.makeMove(move, false);

                if (openingService.exists(board.state.getZobristKey()))
                    continue;

                OpeningBookEntry entry = stockfish.topMove("position fen " + board.exportFen(), board.state.getZobristKey(), STOCKFISH_DEPTH);
                openingService.insert(entry);

                if (prevZobristKey != null)
                    openingService.insert(new OpeningBookEntry(prevZobristKey, move.getUci(), entry.getScore(), entry.getDepth()));
                prevZobristKey = entry.getZobristKey();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void insertOpeningBookEntries(OpeningService openingService, Stockfish stockfish, BBoard board) throws IOException {
        openingService.insert(stockfish.topMove("position fen " + board.exportFen(), board.state.getZobristKey(), 15));
    }
}
