package knight.clubbing.search.Iterative;

public class SearchResult {
    private String bestMove;
    private int evaluation;

    public SearchResult() {
        this.bestMove = null;
        this.evaluation = 0;
    }

    public SearchResult(String bestMove, int evaluation) {
        this.bestMove = bestMove;
        this.evaluation = evaluation;
    }

    public String getBestMove() {
        return bestMove;
    }

    public void setBestMove(String bestMove) {
        this.bestMove = bestMove;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }
}
