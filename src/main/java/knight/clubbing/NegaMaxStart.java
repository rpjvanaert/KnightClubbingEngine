package knight.clubbing;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.moveGeneration.MoveGenerator;
import knight.clubbing.moveOrdering.MoveOrdering;
import knight.clubbing.moveOrdering.OrderStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static knight.clubbing.EngineConst.MAX_THREADED_MOVES;
import static knight.clubbing.EngineConst.NEGAMAX_INF;

public class NegaMaxStart {

    private final int maxDepth;
    private final ExecutorService executor;

    public NegaMaxStart(int maxDepth, ExecutorService executor) {
        this.maxDepth = maxDepth;
        this.executor = executor;
    }

    public BMove findBestMove(BBoard board) throws InterruptedException {
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] moves = moveGenerator.generateMoves(false);

        ResultCollector collector = new ResultCollector();
        List<Future<?>> futures = new ArrayList<>();

        MoveOrdering.orderMoves(board, moves, OrderStrategy.GENERAL);

        int threadedMoveCount = Math.min(moves.length, MAX_THREADED_MOVES);

        for (int i = 0; i < threadedMoveCount; i++) {
            BBoard nextBoard = new BBoard(board);
            nextBoard.makeMove(moves[i], true);
            NegaMaxTask task = new NegaMaxTask(nextBoard, moves[i], maxDepth - 1, collector);
            futures.add(executor.submit(task));
        }

        for (int i = threadedMoveCount; i < moves.length; i++) {
            BBoard nextBoard = new BBoard(board);
            nextBoard.makeMove(moves[i], true);
            int score = - new NegaMaxTask(nextBoard, moves[i], maxDepth - 1, collector).negamax(nextBoard, maxDepth - 1, -NEGAMAX_INF, NEGAMAX_INF);
            collector.report(moves[i], score);
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return collector.getBestMove();
    }
}
