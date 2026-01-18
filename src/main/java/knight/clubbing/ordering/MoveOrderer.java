package knight.clubbing.ordering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

public interface MoveOrderer {
    void order(BMove[] moves, BBoard board, MoveOrderingContext context);
    String name();
}