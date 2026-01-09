package knight.clubbing.search;

public record SearchResponse (
        int score,
        String bestMove,
        int depth,
        long nodesSearched,
        long timeTakenMillis
){
}
