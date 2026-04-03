package knight.clubbing.ordering;

import knight.clubbing.PieceValues;
import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.core.BPiece;

public class DefaultMoveOrderer implements MoveOrderer {

    private final int[][][] historyTable = new int[2][64][64];
    private static final int HISTORY_BONUS = 8;
    private static final int MAX_HISTORY_SCORE = 100_000;
    private static final int HISTORY_SCALE = 5_000;

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

        // History heuristic
        score += historyTable[board.isWhiteToMove() ? 0 : 1][move.startSquare()][move.targetSquare()] / HISTORY_SCALE;

        // Center
        if ((rank == 3 || rank == 4) && (file == 3 || file == 4))
            score += 50;

        // Piece activity & specifics
        switch(BPiece.getPieceType(aggressorPiece)) {
            case BPiece.pawn, BPiece.knight, BPiece.bishop:
                score += 10;
                break;
            case BPiece.king:
                score -= 20;
                break;
            default:
                break;
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

    public void updateHistory2(BMove move, int depth, boolean isWhite, boolean isCapture) {
        if (isCapture) return;

        int color = isWhite ? 0 : 1;
        int bonus = depth * depth * HISTORY_BONUS;

        int clampedBonus = Math.max(-MAX_HISTORY_SCORE, Math.min(MAX_HISTORY_SCORE, bonus));
        int current = historyTable[color][move.startSquare()][move.targetSquare()];

        historyTable[color][move.startSquare()][move.targetSquare()] =
                current + clampedBonus - current * Math.abs(clampedBonus) / MAX_HISTORY_SCORE;
    }

    public void updateHistory(BMove move, int depth, boolean isWhite) {
        int color = isWhite ? 0 : 1;
        int bonus = depth * depth;
        historyTable[color][move.startSquare()][move.targetSquare()] += bonus;
    }

    public void penalizeHistory(BMove move, int depth, boolean isWhite, boolean isCapture) {
        if (isCapture) return;

        int color = isWhite ? 0 : 1;
        int penalty = depth;
        historyTable[color][move.startSquare()][move.targetSquare()] -= penalty;
    }

    public void penalizeHistory2(BMove move, int depth, boolean isWhite, boolean isCapture) {
        if (isCapture) return;

        int color = isWhite ? 0 : 1;
        int penalty = depth * HISTORY_BONUS;
        int clampedPenalty = Math.max(-MAX_HISTORY_SCORE, Math.min(MAX_HISTORY_SCORE, penalty));
        int negClamped = -clampedPenalty;

        int current = historyTable[color][move.startSquare()][move.targetSquare()];
        historyTable[color][move.startSquare()][move.targetSquare()] =
                current + negClamped - current * Math.abs(negClamped) / MAX_HISTORY_SCORE;
    }

    public void clearHistory() {
        for (int color = 0; color < 2; color++) {
            for (int from = 0; from < 64; from++) {
                for (int to = 0; to < 64; to++) {
                    historyTable[color][from][to] = 0;
                }
            }
        }
    }


    @Override
    public String name() {
        return "Default Move Orderer";
    }
}
