package knight.clubbing.search.singleThreaded;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.evaluation.Evaluation;
import knight.clubbing.moveGeneration.MoveGenerator;
import knight.clubbing.moveOrdering.MoveOrdering;
import knight.clubbing.moveOrdering.OrderStrategy;

import static knight.clubbing.search.singleThreaded.SearchConstants.INF;
import static knight.clubbing.search.singleThreaded.SearchConstants.MATE;

public class Search {
    private final SearchConfig config;

    public Search(SearchConfig config) {
        this.config = config;
    }

    public SearchResult search(BBoard board) {
        BMove[] moves = new MoveGenerator(board).generateMoves(false);

        MoveOrdering.orderMoves(board, moves, OrderStrategy.GENERAL);

        BMove bestmove = null;
        int bestscore = -INF;

        for (BMove move : moves) {
            BBoard newBoard = board.copy();
            newBoard.makeMove(move, true);
            int score = -negamax(newBoard, config, 0, -INF, INF);
            if (score > bestscore) {
                bestscore = score;
                bestmove = move;
            }
        }


        System.out.println(bestmove);
        return new SearchResult(bestmove, bestscore);
    }

    private int negamax(BBoard board, SearchConfig config, int depth, int alpha, int beta) {
        if (depth >= config.maxDepth()) {
            return quiesce(board, config, depth, alpha, beta);
            //return Evaluation.evaluate(board);
        }

        BMove[] moves = new MoveGenerator(board).generateMoves(false);

        if (moves.length == 0) {
            if (board.isInCheck())
                return -MATE + depth;
            return 0;
        }

        int bestScore = -INF;

        MoveOrdering.orderMoves(board, moves, OrderStrategy.GENERAL);

        for (BMove move : moves) {

            //BBoard newBoard = board.copy();
            //newBoard.makeMove(move, true);
            board.makeMove(move, true);
            int score = -negamax(board, config, depth + 1, -beta, -alpha);
            board.undoMove(move, true);

            if (score > bestScore) bestScore = score;
            if (score > alpha) alpha = score;
            if (alpha >= beta) break;
        }

        return bestScore;
    }


    private int quiesce(BBoard board, SearchConfig config, int depth, int alpha, int beta) {
        int standPat = Evaluation.evaluate(board);

        if (standPat >= beta) return beta;
        if (standPat > alpha) alpha = standPat;

        BMove[] moves = new MoveGenerator(board).generateMoves(false);
        moves = MoveOrdering.orderMoves(board, moves, OrderStrategy.QUIESCENT);

        if (moves.length == 0) {
            return standPat;
        }

        for (BMove move : moves) {

            int piece = board.getPieceBoards()[move.startSquare()];
            if (piece == 0) {
                System.err.println("Invalid move: " + move.getUci() + " from empty square!");
                continue;
            }
            board.makeMove(move, true);
            int score = -quiesce(board, config, depth + 1, -beta, -alpha);
            board.undoMove(move, true);

            if (score >= beta) return beta;
            if (score > alpha) alpha = score;
        }

        return alpha;
    }
}
