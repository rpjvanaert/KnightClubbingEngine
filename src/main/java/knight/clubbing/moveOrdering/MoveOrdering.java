package knight.clubbing.moveOrdering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

import java.util.Arrays;
import java.util.function.Predicate;

public class MoveOrdering {

    private MoveOrdering() {}

    public static BMove[] orderMoves(BBoard board, BMove[] moves, OrderStrategy strategy) {
        return orderMoves(board, moves, strategy, null);
    }

    public static BMove[] orderMoves(BBoard board, BMove[] moves, OrderStrategy strategy, BMove ttBestMove) {
        BMove[] orderedMoves = switch (strategy) {
            case GENERAL -> {
                Arrays.sort(moves, new GeneralComparator(board));
                yield moves;
            }
            case MVV_LVA -> {
                Arrays.sort(moves, new MvvLvaComparator(board));
                yield moves;
            }
            case QUIESCENT -> {
                BMove[] filtered = filter(moves, new QuiescentPredicate(board));

                Arrays.sort(filtered, new GeneralComparator(board));
                yield filtered;
            }

        };

        if (ttBestMove != null) {
            return moveTTBestMoveToFront(orderedMoves, ttBestMove);
        }

        return orderedMoves;
    }

    private static BMove[] filter(BMove[] moves, Predicate<BMove> predicate) {
        BMove[] temp = new BMove[moves.length];
        int count = 0;
        for (BMove move : moves) {
            if (predicate.test(move))
                temp[count++] = move;
        }

        return Arrays.copyOf(temp, count);
    }

    private static BMove[] moveTTBestMoveToFront(BMove[] moves, BMove ttBestMove) {
        int ttMoveIndex = -1;
        for (int i = 0; i < moves.length; i++) {
            if (moves[i].equals(ttBestMove)) {
                ttMoveIndex = i;
                break;
            }
        }

        if (ttMoveIndex > 0) {
            BMove temp = moves[0];
            moves[0] = ttBestMove;
            moves[ttMoveIndex] = temp;
        }

        return moves;
    }
}
