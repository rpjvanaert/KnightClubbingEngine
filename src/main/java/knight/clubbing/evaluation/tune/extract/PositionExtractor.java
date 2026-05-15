package knight.clubbing.evaluation.tune.extract;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class PositionExtractor {

    public static final int MAX_GAMES = 150_000;

    public static final String INPUT_FILE = "src/main/resources/lichess_elite_2025-11.pgn";
    public static final String OUTPOUT_FILE = "src/main/resources/positions.txt";
    public static final int[] IDX_SAMPLE_MOVES = {12, 18, 25, 35};


    public static void extract(String pgnFilePath, String outputFilePath, int maxGames) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(pgnFilePath));

        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(outputFilePath))) {
            StringBuilder currentPgn = new StringBuilder();
            int gamesProcessed = 0;

            for (String line : lines) {
                currentPgn.append(line).append("\n");

                if (line.startsWith("1-0") || line.startsWith("0-1") || line.startsWith("1/2-1/2")) {
                    if (gamesProcessed >= maxGames) break;

                    String pgn = currentPgn.toString();
                    try {
                        List<String> entries = extractFromGame(pgn);
                        for (String entry : entries) {
                            writer.write(entry);
                            writer.newLine();
                        }
                        if (!entries.isEmpty()) gamesProcessed++;
                    } catch (Exception e) {
                        // skip broken games
                    }

                    currentPgn.setLength(0);
                }
            }
        }

        System.out.println("Done. Extracted to " + outputFilePath);
    }

    private static List<String> extractFromGame(String pgn) {
        PgnResult result = PgnResult.parse(PgnParser.getPgnData(pgn, "Result"));
        if (result == null) return List.of();

        double resultValue = switch (result) {
            case WHITE_WIN -> 1.0;
            case BLACK_WIN -> 0.0;
            case DRAW -> 0.5;
        };

        List<String> entries = new ArrayList<>();

        for (int sampleMove : IDX_SAMPLE_MOVES) {
            PgnInfo info = PgnParser.parse(pgn, sampleMove);
            List<BMove> moves = info.moves();
            if (moves.size() < sampleMove * 2) continue;

            BBoard board = new BBoard();
            for (BMove move : moves) {
                board.makeMove(move, false);
            }

            if (board.isInCheck()) continue;

            entries.add(board.exportFen() + " " + resultValue);
        }

        return entries;
    }

    public static void main(String[] args) throws IOException {
        extract(
                INPUT_FILE,
                OUTPOUT_FILE,
                MAX_GAMES
        );
    }
}