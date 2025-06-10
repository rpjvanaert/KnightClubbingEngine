package knight.clubbing.moveOrdering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BBoardHelper;
import knight.clubbing.core.BMove;
import knight.clubbing.core.BPiece;

import java.util.Arrays;
import java.util.Comparator;

import static knight.clubbing.evaluation.MaterialEvaluation.*;

public class MoveOrdering {

    private MoveOrdering() {}

    public static void orderMoves(BBoard board, BMove[] moves, OrderStrategy strategy) {
        switch (strategy) {
            case GENERAL -> Arrays.sort(moves, compareGeneral(board, 0));
            case MVV_LVA -> Arrays.sort(moves, mvvLva(board));
            case KILLER_MOVE -> System.out.println("Killer move order strategy");
            case STATIC_EVALUATION -> System.out.println("Static evaluation order strategy");
        }
    }

    private static Comparator<BMove> compareGeneral(BBoard board, int depth) {
        return (a, b) -> Integer.compare(scoreGeneral(board, depth, b), scoreGeneral(board, depth, a));
    }

    private static int scoreGeneral(BBoard board, int depth, BMove move) {
        int score = 0;

        if (board.getPieceBoards()[move.targetSquare()] != 0)
            score += 1000 + scoreMvvLva(board, move);

        if (move.isPromotion())
            score += 900 + getValuePiece(move.promotionPieceType());

        if (board.isInCheck())
            score -= 100;

        int targetSquare = move.targetSquare();

        if (targetSquare >= 27 && targetSquare <= 36)
            score += 25;

        board.makeMove(move, true);
        if (board.isInCheck())
            score += 420;
        board.undoMove(move, true);

        if (move.isCastle())
            score += 50;

        if (move.isPawnTwoUp())
            score += 50;

        return score;
    }

    private static Comparator<BMove> mvvLva(BBoard board) {
        return (a, b) -> Integer.compare(scoreMvvLva(board, b), scoreMvvLva(board, a));
    }

    private static int scoreMvvLva(BBoard board, BMove move) {
        int victimPiece = board.getPieceBoards()[move.targetSquare()];
        int aggressorPiece = board.getPieceBoards()[move.startSquare()];

        int victimValue = getValuePiece(victimPiece);

        int aggressorValue = getValuePiece(aggressorPiece);

        return victimValue - aggressorValue;
    }


}
