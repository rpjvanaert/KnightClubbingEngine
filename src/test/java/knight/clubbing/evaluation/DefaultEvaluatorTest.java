package knight.clubbing.evaluation;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.movegen.MoveGenerator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class DefaultEvaluatorTest {

    @Test @Tag("strength")
    void benchmarkEvaluation() {
        BBoard board = new BBoard();
        DefaultEvaluator evaluator = new DefaultEvaluator();

        long start = System.nanoTime();
        for (int i = 0; i < 10_000_000; i++) {
            evaluator.evaluate(board, new EvalParams());
        }
        long elapsed = System.nanoTime() - start;
        System.out.println("10M evaluations (single position): " + elapsed / 1_000_000 + "ms");
    }

    @Test @Tag("strength")
    void benchmarkEvaluationVaryingPositions() {
        List<BBoard> testPositions = generateTestPositions(1000);
        DefaultEvaluator evaluator = new DefaultEvaluator();

        System.out.println("Generated " + testPositions.size() + " test positions");

        // Warm-up
        for (int i = 0; i < 1000; i++) {
            evaluator.evaluate(testPositions.get(i % testPositions.size()), new EvalParams());
        }

        // Actual benchmark - only evaluation is timed
        long start = System.nanoTime();
        int iterations = 10_000_000;
        for (int i = 0; i < iterations; i++) {
            evaluator.evaluate(testPositions.get(i % testPositions.size()), new EvalParams());
        }
        long elapsed = System.nanoTime() - start;

        System.out.println(iterations + " evaluations (varying positions): " + elapsed / 1_000_000 + "ms");
        System.out.println("Avg per evaluation: " + (elapsed / iterations) + "ns");
    }

    /**
     * Generate diverse positions by playing random games
     */
    private List<BBoard> generateTestPositions(int count) {
        List<BBoard> positions = new ArrayList<>();
        Random random = new Random(42);

        while (positions.size() < count) {
            BBoard board = new BBoard();
            int moves = 5 + random.nextInt(40);

            for (int i = 0; i < moves; i++) {
                MoveGenerator moveGen = new MoveGenerator(board);
                BMove[] legalMoves = moveGen.generateMoves(false);

                if (legalMoves.length == 0) break;

                BMove move = legalMoves[random.nextInt(legalMoves.length)];
                board.makeMove(move, false);

                if (i % 5 == 0 && positions.size() < count) {
                    positions.add(new BBoard(board.exportFen()));
                }
            }
        }

        return positions;
    }

    private List<BBoard> generateGamePhasePositions() {
        List<BBoard> positions = new ArrayList<>();

        // Opening position
        positions.add(new BBoard());

        // Common openings
        positions.add(new BBoard("rnbqkb1r/pppppppp/5n2/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 1 2"));

        // Middlegame positions
        positions.add(new BBoard("r1bqkb1r/pppp1ppp/2n2n2/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4"));

        // Complex middlegame
        positions.add(new BBoard("r2q1rk1/ppp2ppp/2np1n2/2b1p1B1/2B1P1b1/2NP1N2/PPP2PPP/R2Q1RK1 w - - 6 9"));

        // Endgame (fewer pieces)
        positions.add(new BBoard("8/5pk1/6p1/8/8/6P1/5PK1/8 w - - 0 40"));

        // Pawn endgame
        positions.add(new BBoard("8/pp3pk1/2p3p1/8/2P3P1/PP5K/8/8 w - - 0 45"));

        return positions;
    }

    @Test @Tag("strength")
    void benchmarkEvaluationGamePhases() {
        List<BBoard> testPositions = generateGamePhasePositions();
        DefaultEvaluator evaluator = new DefaultEvaluator();

        System.out.println("Testing " + testPositions.size() + " game phase positions");

        long start = System.nanoTime();
        int iterations = 5_000_000;
        for (int i = 0; i < iterations; i++) {
            evaluator.evaluate(testPositions.get(i % testPositions.size()), new EvalParams());
        }
        long elapsed = System.nanoTime() - start;

        System.out.println(iterations + " evaluations (game phases): " + elapsed / 1_000_000 + "ms");
        System.out.println("Avg per evaluation: " + (elapsed / iterations) + "ns");
    }
}