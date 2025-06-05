package knight.clubbing;

import knight.clubbing.core.BMove;

import java.util.concurrent.atomic.AtomicInteger;

public class ResultCollector {
    private BMove bestMove = null;
    private final AtomicInteger bestScore;

    public ResultCollector() {
        this.bestScore = new AtomicInteger(Integer.MIN_VALUE);
    }

    public synchronized void report(BMove move, int score) {
        if (score > bestScore.get()) {
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
