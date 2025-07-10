package knight.clubbing.opening;

import knight.clubbing.search.EngineConst;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Stockfish implements AutoCloseable {
    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;

    public boolean start() {
        Path stockfishFolder = Paths.get("stockfish");

        try (Stream<Path> folderStream = Files.list(stockfishFolder)){
            Optional<Path> executablePath = folderStream
                    .filter(Files::isExecutable)
                    .findFirst();

            if (executablePath.isEmpty()) {
                throw new IllegalStateException("Stockfish executable not found");
            }

            process = new ProcessBuilder(executablePath.get().toString()).start();
            reader = new BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
            writer = new BufferedWriter(new java.io.OutputStreamWriter(process.getOutputStream()));

            uci();
            isReady();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected void uci() throws IOException {
        sendCommand("uci", line -> line.startsWith("uciok"));
    }

    protected void isReady() throws IOException {
        sendCommand("isready", line -> true);
    }

    protected void stop() throws IOException {
        sendCommand("stop", _ -> true);
    }

    public void quit() throws IOException {
        sendCommand("quit", _ -> true);
        process.destroy();
    }

    public String bestMove(String fen, int depth) throws IOException {
        return bestMoveCommand("position fen " + fen, depth);
    }

    public String bestMove(int depth) throws IOException {
        return bestMoveCommand("position startpos", depth);
    }

    private String bestMoveCommand(String positionCommand, int depth) throws IOException {
        sendCommand(positionCommand, _ -> true);
        String response = sendCommand("go depth " + depth, line -> line.startsWith("bestmove"));
        return getBestMove(response);
    }

    private static String getBestMove(String response) {
        for (String line : response.split("\n")) {
            if (line.startsWith("bestmove")) {
                String[] parts = line.split(" ");
                return parts.length > 1 ? parts[1] : null;
            }
        }
        return null;
    }

    public List<OpeningBookEntry> topMoves(String positionCommand, long zobrist, int depth) throws IOException {
        sendCommand(positionCommand, _ -> true);
        String response = sendCommand("go depth " + depth, line -> line.startsWith("bestmove"));

        String[] responseLines = response.split("\n");
        Optional<String> infoLine = Arrays.stream(responseLines).filter(line -> line.startsWith("info depth " + depth)).findFirst();
        Optional<String> bestMoveLine = Arrays.stream(responseLines).filter(line -> line.startsWith("bestmove")).findFirst();

        if (infoLine.isEmpty() || bestMoveLine.isEmpty())
            throw new IOException("Failed to retrieve top moves from Stockfish");

        String[] infoParts = infoLine.get().split(" ");

        int indexScore = getIndexArray(infoParts, "score");
        if (indexScore == -1)
            throw new IOException("Failed to parse Stockfish response");

        String bestMove = getBestMove(response);

        String typeScore = infoParts[indexScore + 1];
        String scoreValue = infoParts[indexScore + 2];

        List<OpeningBookEntry> topMoves = new ArrayList<>();

        int score = Integer.parseInt(scoreValue);
        if (typeScore.equals("mate")) {
            score = EngineConst.MATE_SCORE - score;
        }

        topMoves.add(new OpeningBookEntry(zobrist, bestMove, score, depth));

        return topMoves;
    }

    public String getEval(String positionCommand, int depth) throws IOException {
        sendCommand(positionCommand, _ -> true);
        String response = sendCommand("go depth " + depth, line -> line.startsWith("bestmove"));

        String[] responseLines = response.split("\n");
        Optional<String> infoLine = Arrays.stream(responseLines).filter(line -> line.startsWith("info depth " + depth)).findFirst();

        if (infoLine.isEmpty())
            throw new IOException("Failed to retrieve evaluation from Stockfish");

        String[] infoParts = infoLine.get().split(" ");

        int indexScore = getIndexArray(infoParts, "score");
        if (indexScore == -1)
            throw new IOException("Failed to parse Stockfish response for score");

        String typeScore = infoParts[indexScore + 1];
        String scoreValue = infoParts[indexScore + 2];

        return typeScore + " " + scoreValue;
    }

    protected String sendCommand(String command, Predicate<String> isEnd) throws IOException {
        writer.write(command + "\n");
        writer.flush();

        if (notExpectedResponse(command))
            return "";

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line).append("\n");
            if (isEnd.test(line)) {
                break;
            }
        }
        return response.toString().trim();
    }

    private boolean notExpectedResponse(String command) {
        if (command.startsWith("position"))
            return true;

        return false;
    }

    protected static int getIndexArray(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void close() throws Exception {
        try {
            if (process.isAlive())
                quit();
        } finally {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        }

    }
}