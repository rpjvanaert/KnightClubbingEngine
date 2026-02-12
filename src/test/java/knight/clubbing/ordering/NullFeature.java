package knight.clubbing.ordering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

public class NullFeature implements OrderFeature {

    @Override
    public int score(BMove move, BBoard board) {
        return 0;
    }

    @Override
    public String name() {
        return "Null Feature";
    }
}
