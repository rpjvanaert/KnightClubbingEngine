package knight.clubbing.search;

import knight.clubbing.core.BMove;

public record TranspositionEntry(
        long key,
        int value,
        BMove move,
        short depth,
        short nodeType
) {

    public static final short EXACT = 0;
    public static final short LOWER_BOUND = 1;
    public static final short UPPER_BOUND = 2;

}
