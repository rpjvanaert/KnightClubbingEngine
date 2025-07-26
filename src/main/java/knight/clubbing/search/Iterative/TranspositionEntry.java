package knight.clubbing.search.Iterative;

import knight.clubbing.core.BMove;

public record TranspositionEntry(
        long key,
        int value,
        BMove move,
        short depth,
        short nodeType
) {

}
