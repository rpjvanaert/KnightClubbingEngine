package knight.clubbing.search.singleThreaded;

import knight.clubbing.core.BMove;

public class SearchResult {
    public final BMove move;
    public final int score;

    public SearchResult(BMove move, int score) {
        this.move = move;
        this.score = score;
    }

    @Override
    public String toString() {
        return "SearchResult{" + "move=" + move + ", score=" + score + '}';
    }
}
