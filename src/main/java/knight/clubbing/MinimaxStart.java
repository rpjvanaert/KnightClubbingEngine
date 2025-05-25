package knight.clubbing;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.moveGeneration.MoveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MinimaxStart {

    private final int maxDepth;
    private final ExecutorService executor;

    public MinimaxStart(int maxDepth, ExecutorService executor) {
        this.maxDepth = maxDepth;
        this.executor = executor;
    }

    public BMove findBestMove(BBoard board) throws InterruptedException {
        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] moves = moveGenerator.generateMoves(false);

        CancellationToken cancelToken = new CancellationToken();
        ResultCollector collector = new ResultCollector(board.isWhiteToMove);

        List<Future<?>> futures = new ArrayList<>();

        for (BMove move : moves) {
            if (cancelToken.isCancelled()) break;

            BBoard nextBoard = new BBoard(board);
            nextBoard.makeMove(move, true);
            MinimaxTask task = new MinimaxTask(nextBoard, move, maxDepth - 1, nextBoard.isWhiteToMove, collector, cancelToken);
            futures.add(executor.submit(task));
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
