package knight.clubbing.moveOrdering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

import java.util.Arrays;
import java.util.Objects;

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
            case QUIESCENT -> Arrays.stream(moves)
                    .filter(Objects::nonNull)
                    .filter(new QuiescentPredicate(board))
                    .sorted(new GeneralComparator(board))
                    .toArray(BMove[]::new);
        };
    }
}
