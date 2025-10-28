package knight.clubbing.moveOrdering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

import java.util.function.Predicate;

public class QuiescentPredicate implements Predicate<BMove> {

    private final BBoard board;

    public QuiescentPredicate(BBoard board) {
        this.board = board;
    }

    @Override
    public boolean test(BMove move) {
        if (isCapture(move))
            return true;

        if (isCheck(move))
            return true;

        if (move.isPromotion())
            return true;

        return false;
    }

    private boolean isCapture(BMove move) {
        return board.getPieceBoards()[move.targetSquare()] != 0;
    }

    private boolean isCheck(BMove move) {
        BBoard copy = board.copy();
        copy.makeMove(move, true);
        return copy.isInCheck();
    }
}
