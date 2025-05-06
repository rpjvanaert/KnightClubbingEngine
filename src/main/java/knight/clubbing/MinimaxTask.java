package knight.clubbing;

import knight.clubbing.data.details.Color;
import knight.clubbing.data.details.ThreatType;
import knight.clubbing.data.move.Move;
import knight.clubbing.logic.ChessGame;

public class MinimaxTask implements Runnable {

    private final ChessGame game;
    private final Move move;
    private final int depth;
    private final boolean isMaximizing;
    private final ResultCollector collector;
    private final CancellationToken cancelToken;

    public MinimaxTask(ChessGame game, Move move, int depth, boolean isMaximizing, ResultCollector collector, CancellationToken cancelToken) {
        this.game = game;
        this.move = move;
        this.depth = depth;
        this.isMaximizing = isMaximizing;
        this.collector = collector;
        this.cancelToken = cancelToken;
    }

    @Override
    public void run() {
        try {
            if (cancelToken.isCancelled()) return;
            int score = minimax(game, depth, isMaximizing, Integer.MIN_VALUE, Integer.MAX_VALUE);
            collector.report(move, score);

            if (score == Integer.MAX_VALUE || score == Integer.MIN_VALUE) {
                cancelToken.cancel();
            }
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }
    }

    private int minimax(ChessGame game, int depth, boolean isMaximizing, int alpha, int beta) throws InterruptedException {
        if (cancelToken.isCancelled()) return isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if (game.assessThreat(Color.WHITE).equals(ThreatType.CHECKMATE))
            return Integer.MIN_VALUE;
        if (game.assessThreat(Color.BLACK).equals(ThreatType.CHECKMATE))
            return Integer.MAX_VALUE;
        if (depth == 0) {
            return game.getMaterialBalance();
        }

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move eachMove : game.determineAllLegalMoves()) {
            if (cancelToken.isCancelled()) break;

            ChessGame newGame = new ChessGame(game.getBoard().exportFEN());
            newGame.executeMove(eachMove);
            int score = minimax(newGame, depth - 1, !isMaximizing, alpha, beta);

            if (isMaximizing) {
                bestScore = Math.max(score, bestScore);
                alpha = Math.max(alpha, bestScore);
            } else {
                bestScore = Math.min(score, bestScore);
                beta = Math.min(beta, bestScore);
            }

            if (beta <= alpha) {
                break;
            }
        }

        return bestScore;
    }
}
