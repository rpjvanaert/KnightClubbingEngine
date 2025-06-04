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

    public MinimaxTask(BBoard board, BMove move, int depth, boolean isMaximizing, ResultCollector collector) {
        this.board = board;
        this.move = move;
        this.depth = depth;
        this.isMaximizing = isMaximizing;
        this.collector = collector;
    }

    @Override
    public void run() {
        try {
            int score = minimax(board, depth, isMaximizing, Integer.MIN_VALUE, Integer.MAX_VALUE);
            collector.report(move, score);
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }
    }

    protected int minimax(BBoard board, int depth, boolean isMaximizing, int alpha, int beta) throws InterruptedException {

        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] moves = moveGenerator.generateMoves(false);

        if (moves.length == 0) {
            if (board.isInCheck())
                return isMaximizing ? -1_000_000 : 1_000_000;
            return 0;
        }

        if (depth == 0)
            return quiesce(board, isMaximizing, alpha, beta);

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        MoveOrdering.orderMoves(board, moves, OrderStrategy.GENERAL);

        for (BMove eachMove : moves) {

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

    private int quiesce(BBoard board, boolean isMaximizing, int alpha, int beta) throws InterruptedException {
        int standPat = Evaluation.evaluate(board);

        if (isMaximizing) {
            if (standPat >= beta) return beta;
            if (standPat > alpha) alpha = standPat;
        } else {
            if (standPat <= alpha) return alpha;
            if (standPat < beta) beta = standPat;
        }

        BMove[] captures = new MoveGenerator(board).generateMoves(true);
        MoveOrdering.orderMoves(board, captures, OrderStrategy.MVV_LVA);

        for (BMove eachMove : captures) {
            BBoard nextBoard = new BBoard(board);
            nextBoard.makeMove(eachMove, true);
            int score = quiesce(nextBoard, !isMaximizing, alpha, beta);

            if (isMaximizing) {
                if (score > alpha) alpha = score;
            } else {
                if (score < beta) beta = score;
            }

            if (beta <= alpha)
                break;
        }

        return isMaximizing ? alpha : beta;
    }
}
