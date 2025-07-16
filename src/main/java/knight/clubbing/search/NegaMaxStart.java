package knight.clubbing.search;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.movegen.MoveGenerator;
import knight.clubbing.moveOrdering.MoveOrdering;
import knight.clubbing.moveOrdering.OrderStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static knight.clubbing.search.EngineConst.MAX_THREADED_MOVES;
import static knight.clubbing.search.EngineConst.NEGAMAX_INF;

public class NegaMaxStart {

    private final int maxDepth;
    private final ExecutorService executor;
    
    private volatile boolean stopped = false;

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

        runParallelSearches(board, threadedMoveCount, collector, futures, moves);
        runSynchronousSearches(board, threadedMoveCount, moves, collector);

        waitForFutures(futures, collector);

        return collector.getBestMove();
    }

    private void waitForFutures(List<Future<?>> futures, ResultCollector collector) {
        for (Future<?> future : futures) {
            if (checkToCancel(collector)) {
                cancelAllFutures(futures);
                collector.cancel();
                break;
            }

            try {
                future.get();

                if (collector.isMateFound()) {
                    cancelAllFutures(futures);
                    break;
                }
            } catch (InterruptedException | ExecutionException e) {
                cancelAllFutures(futures);
                collector.cancel();
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void cancelAllFutures(List<Future<?>> futures) {
        for (Future<?> future : futures) future.cancel(true);
    }

    private void runSynchronousSearches(BBoard board, int threadedMoveCount, BMove[] moves, ResultCollector collector) throws InterruptedException {
        for (int i = threadedMoveCount; i < moves.length; i++) {
            if (this.checkToCancel(collector)) break;

            startSearch(board, collector, moves[i]);
        }
    }

    private void runParallelSearches(BBoard board, int threadedMoveCount, ResultCollector collector, List<Future<?>> futures, BMove[] moves) {
        for (int i = 0; i < threadedMoveCount; i++) {
            if (this.checkToCancel(collector)) break;

            startThreadedSearch(board, collector, futures, moves[i]);
        }
    }

    private void startSearch(BBoard board, ResultCollector collector, BMove moves) throws InterruptedException {
        BBoard nextBoard = new BBoard(board);
        nextBoard.makeMove(moves, true);
        int score = - new NegaMaxTask(nextBoard, moves, maxDepth - 1, collector).negamax(nextBoard, maxDepth - 1, -NEGAMAX_INF, NEGAMAX_INF);
        collector.report(moves, score);
    }

    private void startThreadedSearch(BBoard board, ResultCollector collector, List<Future<?>> futures, BMove moves) {
        BBoard nextBoard = new BBoard(board);
        nextBoard.makeMove(moves, true);
        NegaMaxTask task = new NegaMaxTask(nextBoard, moves, maxDepth - 1, collector);
        futures.add(executor.submit(task));
    }

    private boolean checkToCancel(ResultCollector collector) {
        return stopped ||  collector.isCancelled() || Thread.currentThread().isInterrupted();
    }

    public void stop() {
        stopped = true;
    }
}
