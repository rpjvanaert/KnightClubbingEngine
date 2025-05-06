package knight.clubbing;

import knight.clubbing.data.details.Color;
import knight.clubbing.data.move.Move;
import knight.clubbing.logic.ChessGame;

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

    public Move findBestMove(ChessGame game) throws InterruptedException {
        List<Move> legalMoves = game.determineAllLegalMoves();
        CancellationToken cancelToken = new CancellationToken();
        ResultCollector collector = new ResultCollector(game.getBoard().getActive().equals(Color.WHITE));

        List<Future<?>> futures = new ArrayList<>();

        for (Move move : legalMoves) {
            if (cancelToken.isCancelled()) break;

            ChessGame newGame = new ChessGame(game.getBoard().exportFEN());
            newGame.executeMove(move);
            MinimaxTask task = new MinimaxTask(newGame, move, maxDepth - 1, move.color().other().equals(Color.WHITE), collector, cancelToken);
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
