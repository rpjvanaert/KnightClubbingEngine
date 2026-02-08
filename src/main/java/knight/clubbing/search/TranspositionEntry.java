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
}
