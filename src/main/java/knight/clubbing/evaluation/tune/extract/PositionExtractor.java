package knight.clubbing.evaluation.tune.extract;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.movegen.MoveGenerator;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class PositionExtractor {

    public static final int MAX_GAMES = 500_000;

    public static final List<String> INPUT_FILES = List.of(
            "src/main/resources/lichess_elite_2025-09.pgn",
            "src/main/resources/lichess_elite_2025-10.pgn",
            "src/main/resources/lichess_elite_2025-11.pgn"
    );
    public static final String OUTPOUT_FILE = "src/main/resources/positions.txt";
    public static final int[] IDX_SAMPLE_MOVES = {16, 25, 35, 50};

    private static long failed = 0;


    public static void extract(List<String> pgnFilePaths, String outputFilePath, int maxGames) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(outputFilePath))) {
            StringBuilder currentPgn = new StringBuilder();
            int gamesProcessed = 0;

            outer:
            for (String pgnFilePath : pgnFilePaths) {
                List<String> lines = Files.readAllLines(Path.of(pgnFilePath));

                for (String line : lines) {
                    currentPgn.append(line).append("\n");

                    if (line.isBlank() && currentPgn.toString().contains("[Event")) {
                        if (gamesProcessed >= maxGames) break outer;

                        String pgn = currentPgn.toString();
                        try {
                            List<String> entries = extractFromGame(pgn);
                            for (String entry : entries) {
                                writer.write(entry);
                                writer.newLine();
                            }
                            if (!entries.isEmpty()) gamesProcessed++;
                        } catch (Exception e) {
                            failed++;
                        }

                        currentPgn.setLength(0);
                    }
                }
                currentPgn.setLength(0); // clear between files
            }
        }

        System.out.println("Done. Extracted to " + outputFilePath);
        System.out.println("Failed: " + failed);
    }

    private static List<String> extractFromGame(String pgn) {
        PgnResult result = PgnResult.parse(PgnParser.getPgnData(pgn, "Result"));
        if (result == null) return List.of();

        double resultValue = switch (result) {
            case WHITE_WIN -> 1.0;
            case BLACK_WIN -> 0.0;
            case DRAW -> 0.5;
        };

        // Get all moves once
        List<BMove> allMoves = PgnParser.parse(pgn, 999).moves();
        int totalMoves = allMoves.size();
        if (totalMoves < 20) return List.of(); // skip very short games

        List<String> entries = new ArrayList<>();

        for (int sampleMove : IDX_SAMPLE_MOVES) {
            int halfMove = sampleMove * 2;
            if (halfMove >= totalMoves) continue;

            // Slide forward up to 10 half-moves to find a quiet position
            for (int offset = 0; offset < 10; offset++) {
                int idx = halfMove + offset;
                if (idx >= totalMoves) break;

                BBoard board = new BBoard();
                for (int i = 0; i <= idx; i++) {
                    board.makeMove(allMoves.get(i), false);
                }

                if (board.isInCheck()) continue;
                BMove[] captures = new MoveGenerator(board).generateMoves(true);
                if (captures.length > 1) continue;

                entries.add(board.exportFen() + " " + resultValue);
                break;
            }
        }

        return entries;
    }

    public static void main(String[] args) throws IOException {
        extract(
                INPUT_FILES,
                OUTPOUT_FILE,
                MAX_GAMES
        );
    }
}