package knight.clubbing.evaluation.tune.train;

import knight.clubbing.core.BBoard;
import knight.clubbing.evaluation.DefaultEvaluator;
import knight.clubbing.evaluation.EvalParams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Tuner {

    private static final double K = 1.13; // sigmoid scaling constant

    record LoadedEntry(BBoard board, double result) {}

    public static void main(String[] args) throws IOException {
        System.out.println("Loading positions...");
        List<PositionEntry> raw = loadDataset("src/main/resources/positions.txt");

        // Pre-parse all FENs once
        List<LoadedEntry> dataset = raw.stream()
                .map(e -> new LoadedEntry(new BBoard(e.fen()), e.result()))
                .toList();
        System.out.println("Loaded " + dataset.size() + " positions.");

        EvalParams params = new EvalParams();
        DefaultEvaluator evaluator = new DefaultEvaluator();

        double baseline = mse(dataset, evaluator, params);
        System.out.printf("Initial MSE: %.6f%n", baseline);

        boolean improved = true;
        int iteration = 0;

        while (improved) {
            improved = false;
            iteration++;
            double prev = baseline;

            for (int i = 0; i < EvalParams.SIZE; i++) {
                params.values[i]++;
                double up = mse(dataset, evaluator, params);

                params.values[i] -= 2;
                double down = mse(dataset, evaluator, params);

                params.values[i]++; // restore

                if (up < baseline && up <= down) {
                    params.values[i]++;
                    baseline = up;
                    improved = true;
                } else if (down < baseline) {
                    params.values[i]--;
                    baseline = down;
                    improved = true;
                }
            }

            tunePst(dataset, evaluator, params, new double[]{baseline});

            if (baseline < prev) improved = true;
            System.out.printf("Iteration %d MSE: %.6f%n", iteration, baseline);
        }

        System.out.println("Tuning complete.");
        exportAsJava(params);
    }

    private static void tunePst(List<LoadedEntry> dataset, DefaultEvaluator evaluator,  EvalParams params, double[] baseline) {
        for (int piece = 1; piece <= 6; piece++) {
            for (int sq = 0; sq < 64; sq++) {
                // Tune mgPst
                params.mgPst[piece][sq]++;
                double up = mse(dataset, evaluator, params);
                params.mgPst[piece][sq] -= 2;
                double down = mse(dataset, evaluator, params);
                params.mgPst[piece][sq]++;

                if (up < baseline[0] && up <= down) {
                    params.mgPst[piece][sq]++;
                    baseline[0] = up;
                } else if (down < baseline[0]) {
                    params.mgPst[piece][sq]--;
                    baseline[0] = down;
                }

                // Same for egPst
                params.egPst[piece][sq]++;
                up = mse(dataset, evaluator, params);
                params.egPst[piece][sq] -= 2;
                down = mse(dataset, evaluator, params);
                params.egPst[piece][sq]++;

                if (up < baseline[0] && up <= down) {
                    params.egPst[piece][sq]++;
                    baseline[0] = up;
                } else if (down < baseline[0]) {
                    params.egPst[piece][sq]--;
                    baseline[0] = down;
                }
            }
        }
    }

    private static double mse(List<LoadedEntry> dataset, DefaultEvaluator evaluator, EvalParams params) {
        return dataset.parallelStream()
                .mapToDouble(entry -> {
                    // Each thread needs its own board copy since evaluate may mutate state
                    double eval = evaluator.evaluate(entry.board(), params);
                    double diff = entry.result() - sigmoid(eval);
                    return diff * diff;
                })
                .average()
                .orElse(0);
    }

    private static double sigmoid(double eval) {
        return 1.0 / (1.0 + Math.pow(10.0, -K * eval / 400.0));
    }


    private static List<PositionEntry> loadDataset(String path) throws IOException {
        return Files.lines(Path.of(path))
                .filter(line -> !line.isBlank())
                .map(line -> {
                    // format: "<fen parts> <result>"
                    // FEN has 6 space-separated parts, result is the last token
                    int lastSpace = line.lastIndexOf(' ');
                    String fen = line.substring(0, lastSpace);
                    double result = Double.parseDouble(line.substring(lastSpace + 1));
                    return new PositionEntry(fen, result);
                })
                .toList();
    }

    private static void printParams(EvalParams params) {
        String[] names = {
                "MG_PAWN", "EG_PAWN", "MG_KNIGHT", "EG_KNIGHT",
                "MG_BISHOP", "EG_BISHOP", "MG_ROOK", "EG_ROOK",
                "MG_QUEEN", "EG_QUEEN",
                "BISHOP_PAIR_MG", "BISHOP_PAIR_EG",
                "DOUBLED_PAWN_MG", "DOUBLED_PAWN_EG",
                "ISOLATED_PAWN_MG", "ISOLATED_PAWN_EG",
                "PASSED_PAWN_MG", "PASSED_PAWN_EG",
                "PAWN_CHAIN_MG", "PAWN_CHAIN_EG",
                "KING_SHIELD_MG", "KING_SHIELD_EG",
                "ROOK_OPEN_MG", "ROOK_OPEN_EG",
                "ROOK_SEMIOPEN_MG", "ROOK_SEMIOPEN_EG"
        };
        for (int i = 0; i < params.values.length; i++) {
            System.out.printf("  %-20s = %d%n", names[i], params.values[i]);
        }
    }

    private static void exportAsJava(EvalParams params) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("// === TUNED EVAL PARAMS ===\n\n");

        // Scalar values
        String[] names = {
                "IDX_MG_PAWN", "IDX_EG_PAWN", "IDX_MG_KNIGHT", "IDX_EG_KNIGHT",
                "IDX_MG_BISHOP", "IDX_EG_BISHOP", "IDX_MG_ROOK", "IDX_EG_ROOK",
                "IDX_MG_QUEEN", "IDX_EG_QUEEN",
                "IDX_MG_BISHOP_PAIR", "IDX_EG_BISHOP_PAIR",
                "IDX_MG_DOUBLED_PAWN", "IDX_EG_DOUBLED_PAWN",
                "IDX_MG_ISOLATED_PAWN", "IDX_EG_ISOLATED_PAWN",
                "IDX_MG_PASSED_PAWN", "IDX_EG_PASSED_PAWN",
                "IDX_MG_PAWN_CHAIN", "IDX_EG_PAWN_CHAIN",
                "IDX_MG_KING_SHIELD", "IDX_EG_KING_SHIELD",
                "IDX_MG_ROOK_OPEN", "IDX_EG_ROOK_OPEN",
                "IDX_MG_ROOK_SEMIOPEN", "IDX_EG_ROOK_SEMIOPEN"
        };
        for (int i = 0; i < params.values.length; i++) {
            sb.append(String.format("values[%s] = %d;%n", names[i], params.values[i]));
        }

        // PST arrays
        String[] pieceNames = {"", "pawn", "knight", "bishop", "rook", "queen", "king"};
        for (int piece = 1; piece <= 6; piece++) {
            sb.append(String.format("%nmgPst[BPiece.%s] = new int[]{%n", pieceNames[piece]));
            appendPst(sb, params.mgPst[piece]);
            sb.append("};\n");

            sb.append(String.format("%negPst[BPiece.%s] = new int[]{%n", pieceNames[piece]));
            appendPst(sb, params.egPst[piece]);
            sb.append("};\n");
        }

        String output = sb.toString();
        System.out.println(output);
        Files.writeString(Path.of("src/main/resources/tuned_params.java.txt"), output);
        System.out.println("Exported to tuned_params.java.txt");
    }

    private static void appendPst(StringBuilder sb, int[] pst) {
        for (int rank = 7; rank >= 0; rank--) {
            sb.append("    ");
            for (int file = 0; file < 8; file++) {
                sb.append(String.format("%4d,", pst[rank * 8 + file]));
            }
            sb.append("\n");
        }
    }
}