package knight.clubbing.ordering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

public class CenterFeature implements OrderFeature {

    @Override
    public int score(BMove move, BBoard board) {
            int targetSquare = move.targetSquare();
            int rank = targetSquare / 8;
            int file = targetSquare % 8;

            if ((rank == 3 || rank == 4) && (file == 3 || file == 4)) {
                return 50;
            }

        return 0;
    }

    @Override
    public String name() {
        return "Center Feature";
    }
}
