package knight.clubbing;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.evaluation.Evaluation;
import knight.clubbing.moveGeneration.MoveGenerator;
import knight.clubbing.moveOrdering.MoveOrdering;
import knight.clubbing.moveOrdering.OrderStrategy;

public class MinimaxTask implements Runnable {

    private final BBoard board;
    private final BMove move;
    private final int depth;
    private final boolean isMaximizing;
    private final ResultCollector collector;
    private final CancellationToken cancelToken;

    public MinimaxTask(BBoard board, BMove move, int depth, boolean isMaximizing, ResultCollector collector, CancellationToken cancelToken) {
        this.board = board;
        this.move = move;
        this.depth = depth;
        this.isMaximizing = isMaximizing;
        this.collector = collector;
        this.cancelToken = cancelToken;
    }

    @Override
    public void run() {
        try {
            if (cancelToken.isCancelled()) return;
            int score = minimax(board, depth, isMaximizing, Integer.MIN_VALUE, Integer.MAX_VALUE);
            collector.report(move, score);

            if (score == Integer.MAX_VALUE || score == Integer.MIN_VALUE) {
                cancelToken.cancel();
            }
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }
    }

    protected int minimax(BBoard board, int depth, boolean isMaximizing, int alpha, int beta) throws InterruptedException {
        if (cancelToken.isCancelled()) return isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] moves = moveGenerator.generateMoves(false);

        if (moves.length == 0) {
            if (board.isInCheck())
                return isMaximizing ? -1_000_000 : 1_000_000;
            return 0;
        }

        if (depth == 0) {
            return Evaluation.evaluate(board);
        }

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        MoveOrdering.orderMoves(board, moves, OrderStrategy.GENERAL);

        for (BMove eachMove : moves) {
            if (cancelToken.isCancelled()) break;

            BBoard nextBoard = new BBoard(board);
            nextBoard.makeMove(eachMove, true);
            int score = minimax(nextBoard, depth - 1, !isMaximizing, alpha, beta);

            if (isMaximizing) {
                bestScore = Math.max(score, bestScore);
                alpha = Math.max(alpha, bestScore);
            } else {
                bestScore = Math.min(score, bestScore);
                beta = Math.min(beta, bestScore);
            }

            if (beta <= alpha) {
                break;
            }
        }

        return bestScore;
    }
}
