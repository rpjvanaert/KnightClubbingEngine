package knight.clubbing.revamp.ordering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;

public class BasicMoveOrderer implements MoveOrderer {

    private final OrderFeature[] features;

    public BasicMoveOrderer(OrderFeature... features) {
        this.features = features;
    }

    @Override
    public void order(BMove[] moves, BBoard board) {
        int[] scores = new int[moves.length];

        for (int i = 0; i < moves.length; i++) {
            for (OrderFeature feature : features) {
                scores[i] += feature.score(moves[i], board);
            }
        }

        // Sort moves by descending score
        for (int i = 0; i < moves.length - 1; i++) {
            for (int j = i + 1; j < moves.length; j++) {
                if (scores[j] > scores[i]) {
                    // Swap moves
                    BMove tempMove = moves[i];
                    moves[i] = moves[j];
                    moves[j] = tempMove;

                    // Swap scores
                    int tempScore = scores[i];
                    scores[i] = scores[j];
                    scores[j] = tempScore;
                }
            }
        }
    }

    @Override
    public String name() {
        return "";
    }
}
