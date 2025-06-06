package knight.clubbing.search;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.evaluation.Evaluation;
import knight.clubbing.moveGeneration.MoveGenerator;
import knight.clubbing.moveOrdering.MoveOrdering;
import knight.clubbing.moveOrdering.OrderStrategy;

import static knight.clubbing.search.EngineConst.MATE_SCORE;
import static knight.clubbing.search.EngineConst.NEGAMAX_INF;

public class NegaMaxTask implements Runnable {

    private final BBoard board;
    private final BMove move;
    private final int depth;
    private final ResultCollector collector;

    public NegaMaxTask(BBoard board, BMove move, int depth, ResultCollector collector) {
        this.board = board;
        this.move = move;
        this.depth = depth;
        this.collector = collector;
    }

    @Override
    public void run() {
        try {
            int score = -negamax(board, depth, -NEGAMAX_INF, NEGAMAX_INF);
            collector.report(move, score);
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }
    }

    protected int negamax(BBoard board, int depth, int alpha, int beta) throws InterruptedException {

        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] moves = moveGenerator.generateMoves(false);

        if (moves.length == 0) {
            if (board.isInCheck())
                return -MATE_SCORE - depth;
            return 0;
        }

        if (depth == 0) {
            if (board.isInCheck()) {
                return negamax(board, 1, alpha, beta);
            } else {
                return quiesce(board, alpha, beta);
            }
        }


        int bestScore = Integer.MIN_VALUE;

        MoveOrdering.orderMoves(board, moves, OrderStrategy.GENERAL);

        for (BMove eachMove : moves) {
            this.checkThread();
            BBoard nextBoard = new BBoard(board);
            nextBoard.makeMove(eachMove, true);
            int score = -negamax(nextBoard, depth - 1, -beta, -alpha);

            if (score > bestScore) bestScore = score;
            if (score > alpha) alpha = score;
            if (alpha >= beta) break;
        }

        return bestScore;
    }

    private int quiesce(BBoard board, int alpha, int beta) throws InterruptedException {
        int standPat = Evaluation.evaluate(board);

        if (standPat >= beta) return beta;
        if (standPat > alpha) alpha = standPat;

        BMove[] captures = new MoveGenerator(board).generateMoves(true);
        MoveOrdering.orderMoves(board, captures, OrderStrategy.MVV_LVA);

        for (BMove eachMove : captures) {
            this.checkThread();
            BBoard nextBoard = new BBoard(board);
            nextBoard.makeMove(eachMove, true);
            int score = -quiesce(nextBoard, -beta, -alpha);
            if (score >= beta) return beta;
            if (score > alpha) alpha = score;
        }

        return alpha;
    }

    private void checkThread() throws InterruptedException {
        if (Thread.interrupted() || collector.isCancelled()) throw new InterruptedException();
    }
}
