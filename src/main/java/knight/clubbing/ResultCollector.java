package knight.clubbing;

import knight.clubbing.core.BMove;

import java.util.concurrent.atomic.AtomicInteger;

public class ResultCollector {
    private final boolean isMaximizing;
    private volatile BMove bestMove = null;
    private final AtomicInteger bestScore;

    public ResultCollector(boolean isMaximizing) {
        this.isMaximizing = isMaximizing;
        this.bestScore = new AtomicInteger(isMaximizing? Integer.MIN_VALUE : Integer.MAX_VALUE);
    }

    public synchronized void report(BMove move, int score) {
        if ((isMaximizing && score > bestScore.get()) || (!isMaximizing && score < bestScore.get())) {
            bestScore.set(score);
            bestMove = move;
        }
    }

    public BMove getBestMove() {
        return bestMove;
    }

    public int getBestScore() {
        return bestScore.get();
    }
}
