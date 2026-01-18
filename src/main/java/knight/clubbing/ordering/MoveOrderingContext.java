package knight.clubbing.ordering;

import knight.clubbing.core.BMove;

public class MoveOrderingContext {
    private final int ply;
    private final BMove[][] killerMoves;

    public MoveOrderingContext(int ply, BMove[][] killerMoves) {
        this.ply = ply;
        this.killerMoves = killerMoves;
    }

    public int getPly() {
        return ply;
    }

    public BMove[][] getKillerMoves() {
        return killerMoves;
    }
}