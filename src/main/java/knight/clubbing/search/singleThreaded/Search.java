package knight.clubbing.search.singleThreaded;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.evaluation.Evaluation;
import knight.clubbing.movegen.MoveGenerator;
import knight.clubbing.moveOrdering.MoveOrdering;
import knight.clubbing.moveOrdering.OrderStrategy;
import knight.clubbing.opening.OpeningBookEntry;
import knight.clubbing.opening.OpeningService;

import static knight.clubbing.search.singleThreaded.SearchConstants.INF;
import static knight.clubbing.search.singleThreaded.SearchConstants.MATE;

public class Search {
    private final SearchConfig config;
    private final OpeningService openingService;

    public Search(SearchConfig config) {
        this.config = config;
        this.openingService = new OpeningService(OpeningService.jdbcUrl);
    }

    public SearchResult search(BBoard board) {

        if (openingService.exists(board.state.getZobristKey())) {
            OpeningBookEntry entry = openingService.getBest(board.state.getZobristKey());
            return new SearchResult(BMove.fromUci(entry.getMove(), board), entry.getScore());
        }

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


        return new SearchResult(bestmove, bestscore);
    }

    private int negamax(BBoard board, SearchConfig config, int depth, int alpha, int beta) {
        if (depth >= config.maxDepth()) {
            return quiesce(board, config, depth, alpha, beta);
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
