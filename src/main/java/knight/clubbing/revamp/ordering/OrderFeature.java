package knight.clubbing.revamp.ordering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

public interface OrderFeature {
    int score (BMove move, BBoard board);
    String name();
}
