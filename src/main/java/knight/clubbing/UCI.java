package knight.clubbing;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static knight.clubbing.search.EngineConst.DEFAULT_DEPTH;
import static knight.clubbing.search.EngineConst.MAX_THREADED_MOVES;

public class UCI {

    public static final String IN = "in: ";
    public static final String OUT = "out: ";
    public static final String COMMAND_LOG = "command.log";
    public static final String BOARD_LOG = "board.log";
    private final Logger logger = Logger.getLogger(getClass().getName());

    private final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADED_MOVES);

    private BBoard board;
    private Thread searchThread;
    //private NegaMaxStart searchStart;

    protected BBoard getBoard() {
        return board;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line == null || line.isEmpty()) continue;
            handleCommand(line.trim());
        }
    }

    protected void handleCommand(String line) {
        logText(IN + line, COMMAND_LOG);

        switch (line) {
            case "uci": {
                sendCommand("id name KnightClubbing");
                sendCommand("id author Ralf van Aert");
                sendCommand("uciok");
                break;
            }
            case "isready": {
                sendCommand("readyok");
                break;
            }
            case "stop": {
                //if (searchStart != null) searchStart.stop();
                if (searchThread != null) searchThread.interrupt();
                break;
            }
            case "quit" : {
                //if (searchStart != null) searchStart.stop();
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

    private void sendCommand(String line) {
        System.out.println(line);
        logText(OUT + line, COMMAND_LOG);
    }

    private void logText(String text, String location) {
        try (PrintWriter log = new PrintWriter(new FileWriter(location, true))) {
            log.println(text);
        } catch (IOException _) {
        }
    }

    protected void handlePosition(String line) {
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
                try {
                    BMove move = BMove.fromUci(parts[i], board);
                    board.makeMove(move, false);
                } catch (Exception e) {
                    logger.warning("Invalid move in position command: " + parts[i]);
                    logger.warning(e.getMessage());
                }

            }
        }

        /*
        logText("--------", BOARD_LOG);
        logText(line, BOARD_LOG);
        logText(board.exportFen(), BOARD_LOG);
        logText(board.getDisplay(), BOARD_LOG);
        logText("--------", BOARD_LOG);
         */
    }

    protected void handleGo(String line) {
        int depth = DEFAULT_DEPTH;

        if (line.contains("depth")) {
            String[] parts = line.split(" ");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("depth") && i + 1 < parts.length) {
                    depth = Integer.parseInt(parts[i + 1]);
                    break;
                }
            }
        }
        //searchStart = new NegaMaxStart(depth, executor);\
        //Search search = new Search(new SearchConfig(depth));

        searchThread = new Thread(() -> {
            BMove move = null;
            try {
                //move = searchStart.findBestMove(board);
                //move = search.search(board).move;
            } catch (Throwable t) {
                t.printStackTrace();
                try (PrintWriter log = new PrintWriter(new FileWriter("engine_crash.log", true))) {
                    t.printStackTrace(log);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("FEN: ").append(board.exportFen()).append("\n");
                    stringBuilder.append("Time: ").append(Instant.now()).append("\n");
                    stringBuilder.append("------\n");
                    String string = stringBuilder.toString();
                    log.println(string);
                } catch (IOException _) {}
            } finally {
                if (move != null) {
                    sendCommand("bestmove " + move.getUci());
                } else {
                    sendCommand("bestmove 0000");
                }
            }
        });


        searchThread.start();
    }
}
