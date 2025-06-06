package knight.clubbing;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.search.NegaMaxStart;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static knight.clubbing.search.EngineConst.MAX_THREADED_MOVES;

public class UCI {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private BBoard board;
    private Thread searchThread;
    private NegaMaxStart searchStart;

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line == null || line.isEmpty()) continue;
            handleCommand(line.trim());
        }
    }

    private void handleCommand(String line) {
        switch (line) {
            case "uci": {
                logger.info("id name KnightClubbing");
                logger.info("id author Ralf van Aert");
                logger.info("uciok");
                break;
            }
            case "isready": {
                logger.info("readyok");
                break;
            }
            case "stop": {
                if (searchStart != null) searchStart.stop();
                if (searchThread != null) searchThread.interrupt();
                break;
            }
            case "quit" : {
                if (searchStart != null) searchStart.stop();
                if (searchThread != null) searchThread.interrupt();
                System.exit(0);
                break;
            }
            default: {
                if (line.startsWith("position")) {
                    handlePosition(line);
                } else if (line.startsWith("go")) {
                    handleGo(line);
                }
                break;
            }
        }
    }

    private void handlePosition(String line) {
        String[] parts = line.split(" ");
        int index = 1;

        if (parts[index].equals("startpos")) {
            board = new BBoard();
            index = 2;
        } else if (parts[index].equals("fen")) {
            StringBuilder fen = new StringBuilder();
            for (int i = 2; i < 8; i++) fen.append(parts[i]).append(" ");
            board = new BBoard(fen.toString().trim());
            index = 8;
        }

        if (index < parts.length && parts[index].equals("moves")) {
            for (int i = index + 1; i < parts.length; i++) {
                BMove move = BMove.fromUci(parts[i], board);
                board.makeMove(move, true);
            }
        }
    }

    private void handleGo(String line) {
        int depth = 8;

        if (line.contains("depth")) {
            String[] parts = line.split(" ");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("depth") && i + 1 < parts.length) {
                    depth = Integer.parseInt(parts[i + 1]);
                    break;
                }
            }
        }
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADED_MOVES);
        searchStart = new NegaMaxStart(depth, executor);

        searchThread = new Thread(() -> {
            try {
                BMove move = searchStart.findBestMove(board);
                logger.info("bestmove " + move.getUci());
            } catch (InterruptedException _) {
                logger.info("Interrupted...");
                Thread.currentThread().interrupt();
            } finally {
                executor.shutdownNow();
            }
        });

        searchThread.start();
    }
}
