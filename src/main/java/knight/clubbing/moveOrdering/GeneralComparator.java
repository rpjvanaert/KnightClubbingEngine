package knight.clubbing.moveOrdering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

import java.util.Comparator;

import static knight.clubbing.evaluation.MaterialEvaluation.getValuePiece;

public class GeneralComparator implements Comparator<BMove> {

    private final BBoard board;

    public GeneralComparator(BBoard board) {
        this.board = board;
    }

    @Override
    public int compare(BMove a, BMove b) {
        return Integer.compare(scoreGeneral(board, a), scoreGeneral(board, b));
    }

    public static int scoreGeneral(BBoard board, BMove move) {
        int score = 0;

        if (board.getPieceBoards()[move.targetSquare()] != 0)
            score += 1000 + MvvLvaComparator.score(board, move);

        if (move.isPromotion())
            score += 900 + getValuePiece(move.promotionPieceType());

        if (board.isInCheck())
            score += 100;

        int targetSquare = move.targetSquare();

        if (targetSquare >= 27 && targetSquare <= 36)
            score += 25;

        /*
        BBoard copy = board.copy();
        copy.makeMove(move, true);
        if (copy.isInCheck())
            score += 420;

         */

        if (move.isCastle())
            score += 50;

        if (move.isPawnTwoUp())
            score += 50;

        return score;
    }
}
