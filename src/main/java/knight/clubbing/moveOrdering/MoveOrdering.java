package knight.clubbing.moveOrdering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

import java.util.Arrays;
import java.util.function.Predicate;

public class MoveOrdering {

    private MoveOrdering() {}

    public static BMove[] orderMoves(BBoard board, BMove[] moves, OrderStrategy strategy) {
        return switch (strategy) {
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
}
