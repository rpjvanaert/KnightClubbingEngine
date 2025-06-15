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
        if (board.getPieceBoards()[move.targetSquare()] != 0)
            return true;

        /*
        board.makeMove(move, true);
        if (board.isInCheck())
            return true;
        board.undoMove(move, true);

         */

        return false;
    }
}
