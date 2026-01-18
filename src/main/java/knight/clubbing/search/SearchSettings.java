package knight.clubbing.search;

public record SearchSettings (
        int maxDepth,
        long timeLimit,
        int threads,
        boolean pondering
) {
}
