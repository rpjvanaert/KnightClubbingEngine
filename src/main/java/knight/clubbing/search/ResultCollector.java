package knight.clubbing.search;

import knight.clubbing.core.BMove;

import java.util.concurrent.atomic.AtomicInteger;

import static knight.clubbing.search.EngineConst.MATE_SCORE;

public class ResultCollector {
    private BMove bestMove = null;
    private final AtomicInteger bestScore;
    private boolean cancelled = false;

    public ResultCollector() {
        this.bestScore = new AtomicInteger(Integer.MIN_VALUE);
    }

    public synchronized void report(BMove move, int score) {
        if (score > bestScore.get()) {
            bestScore.set(score);
            bestMove = move;

            if (score >= MATE_SCORE)
                cancelled = true;
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public BMove getBestMove() {
        return bestMove;
    }

    public int getBestScore() {
        return bestScore.get();
    }
}
