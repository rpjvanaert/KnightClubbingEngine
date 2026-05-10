package knight.clubbing.strength.bestmove;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class BestmovePuzzle {
    @JsonProperty("fen")
    private String fen;

    @JsonProperty("bestMoves")
    private List<String> bestMoves;

    @JsonProperty("description")
    private String description;

    public String getFen() {
        return fen;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public List<String> getBestMoves() {
        return bestMoves;
    }

    public void setBestMoves(List<String> bestMoves) {
        this.bestMoves = bestMoves;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Puzzle{fen='" + fen + "', bestMoves=" + bestMoves + ", description='" + description + "'}";
    }
}