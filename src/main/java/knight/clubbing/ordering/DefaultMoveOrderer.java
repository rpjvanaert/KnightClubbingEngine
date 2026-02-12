package knight.clubbing.ordering;

import knight.clubbing.PieceValues;
import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.core.BPiece;

public class DefaultMoveOrderer implements MoveOrderer {
    @Override
    public void order(BMove[] moves, BBoard board, MoveOrderingContext context) {
        int[] scores = new int[moves.length];

        for (int i = 0; i < moves.length; i++) {
            scores[i] = determineScore(moves[i], board, context);
        }

        sortMovesByScore(moves, scores);
    }

    private int determineScore(BMove move, BBoard board, MoveOrderingContext context) {
        int score = 0;


        int victimPiece = board.getPieceBoards()[move.targetSquare()];
        int aggressorPiece = board.getPieceBoards()[move.startSquare()];

        int rank = move.targetSquare() / 8;
        int file = move.targetSquare() % 8;

        // MVV-LVA
        int victimValue = PieceValues.MVVLVA_VALUES[BPiece.getPieceType(victimPiece)];
        int aggressorValue = PieceValues.MVVLVA_VALUES[BPiece.getPieceType(aggressorPiece)];

        score += victimValue - aggressorValue;

        // Center
        if ((rank == 3 || rank == 4) && (file == 3 || file == 4)) {
            score += 50;
        }

        // Killer moves
        if (context != null) {
            BMove[][] killerMoves = context.getKillerMoves();
            int ply = context.getPly();

            if (killerMoves != null && ply >= 0) {
                if (move.equals(killerMoves[ply][0])) {
                    score += 10000;
                } else if (move.equals(killerMoves[ply][1])) {
                    score += 9000;
                }
            }
        }
        return score;
    }

    private void sortMovesByScore(BMove[] moves, int[] scores) {
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
        return "Default Move Orderer";
    }
}
