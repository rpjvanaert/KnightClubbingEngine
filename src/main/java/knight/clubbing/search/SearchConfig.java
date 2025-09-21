package knight.clubbing.search;

public record SearchConfig (
        int maxDepth,
        long timeLimit,
        int threads
) {
}
