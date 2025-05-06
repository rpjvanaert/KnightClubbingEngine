package knight.clubbing;

import knight.clubbing.data.move.Move;
import knight.clubbing.data.move.MoveDraft;

import java.util.concurrent.atomic.AtomicInteger;

public class ResultCollector {
    private final boolean isMaximizing;
    private volatile Move bestMove = null;
    private final AtomicInteger bestScore;

    public ResultCollector(boolean isMaximizing) {
        this.isMaximizing = isMaximizing;
        this.bestScore = new AtomicInteger(isMaximizing? Integer.MIN_VALUE : Integer.MAX_VALUE);
    }

    public synchronized void report(Move move, int score) {
        if ((isMaximizing && score > bestScore.get()) || (!isMaximizing && score < bestScore.get())) {
            bestScore.set(score);
            bestMove = move;
        }
    }

    public Move getBestMove() {
        return bestMove;
    }

    public int getBestScore() {
        return bestScore.get();
    }
}
