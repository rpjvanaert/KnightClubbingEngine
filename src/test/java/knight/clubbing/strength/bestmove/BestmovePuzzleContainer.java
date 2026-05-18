package knight.clubbing.strength.bestmove;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BestmovePuzzleContainer {
    @JsonProperty("puzzles")
    private List<BestmovePuzzle> puzzles;

    public List<BestmovePuzzle> getPuzzles() {
        return puzzles;
    }

    public void setPuzzles(List<BestmovePuzzle> puzzles) {
        this.puzzles = puzzles;
    }
}
