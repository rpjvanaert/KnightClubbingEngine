package knight.clubbing.search;

public class TranspositionEntry {
    private final int depth;
    private final int score;
    private final int flag;

    public TranspositionEntry(int depth, int score, int flag) {
        this.depth = depth;
        this.score = score;
        this.flag = flag;
    }

    public int getDepth() {
        return depth;
    }

    public int getScore() {
        return score;
    }

    public int getFlag() {
        return flag;
    }

    public static int determineFlag(int beta, int bestScore, int originalAlpha) {
        if (bestScore <= originalAlpha) return 1;
        if (bestScore >= beta) return 2;
        return 0;
    }
}
