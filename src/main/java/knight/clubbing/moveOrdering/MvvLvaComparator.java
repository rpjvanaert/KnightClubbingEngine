package knight.clubbing.moveOrdering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

import java.util.Comparator;

import static knight.clubbing.evaluation.MaterialEvaluation.getValuePiece;

public class MvvLvaComparator implements Comparator<BMove> {

    private final BBoard board;

    public MvvLvaComparator(BBoard board) {
        this.board = board;
    }

    @Override
    public int compare(BMove a, BMove b) {
        return Integer.compare(score(b), score(a));
    }

    private int score(BMove move) {
        return score(board, move);
    }

    public static int score(BBoard board, BMove move) {
        int victimPiece = board.getPieceBoards()[move.targetSquare()];
        int aggressorPiece = board.getPieceBoards()[move.startSquare()];

        int victimValue = getValuePiece(victimPiece);

        int aggressorValue = getValuePiece(aggressorPiece);

        return victimValue - aggressorValue;
    }
}
