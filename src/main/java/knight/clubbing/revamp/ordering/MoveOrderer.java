package knight.clubbing.revamp.ordering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

public interface MoveOrderer {
    void order(BMove[] moves, BBoard board);
    String name();
}
