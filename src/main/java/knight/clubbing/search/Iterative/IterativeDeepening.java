package knight.clubbing.search.Iterative;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.evaluation.Evaluation;
import knight.clubbing.moveOrdering.MoveOrdering;
import knight.clubbing.moveOrdering.OrderStrategy;
import knight.clubbing.movegen.MoveGenerator;
import knight.clubbing.opening.OpeningBookEntry;
import knight.clubbing.opening.OpeningService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static knight.clubbing.search.singleThreaded.SearchConstants.INF;
import static knight.clubbing.search.singleThreaded.SearchConstants.MATE;

public class IterativeDeepening {

    private BBoard board;
    private long timeLimit;
    private boolean stopSearch;
    private SearchResult bestResult;
    private long startTime;

    private Map<Long, Transposition> transpositionTable;

    private final OpeningService openingService;

    public IterativeDeepening(BBoard board) {
        this.board = board;
        this.timeLimit = 0;
        this.stopSearch = false;
        this.bestResult = new SearchResult();
        this.transpositionTable = new ConcurrentHashMap<>();
        this.openingService = new OpeningService();
    }

    public SearchResult search(long timeLimit) {
        if (openingService.exists(board.state.getZobristKey())) {
            OpeningBookEntry entry = openingService.getBest(board.state.getZobristKey());
            return new SearchResult(entry.getMove(), entry.getScore());
        }
        this.timeLimit = timeLimit;
        this.stopSearch = false;
        this.bestResult = new SearchResult();

        initializeSearch();

        for (int depth = 1; !stopSearch; depth++) {

            SearchResult result = searchAtDepth(depth);

            if (stopSearch) break;

            this.bestResult = result;

            if (isDecisive(result)) {
                break;
            }
        }

        finalizeSearch();
        return bestResult;
    }

    private void initializeSearch() {
        this.startTime = System.currentTimeMillis();
    }

    private boolean cantUseTime() {
        return System.currentTimeMillis() - startTime >= timeLimit;
    }

    private SearchResult searchAtDepth(int depth) {
        SearchResult result = new SearchResult();

        BMove[] moves = new MoveGenerator(board).generateMoves(false);
        MoveOrdering.orderMoves(board, moves, OrderStrategy.GENERAL);

        for (BMove move : moves) {
            if (stopSearch || cantUseTime()) {
                stopSearch = true;
                break;
            }

            board.makeMove(move, true);

            int score = -negamax(board, depth, -INF, INF);

            board.undoMove(move, true);

            if (score > result.getEvaluation()) {
                result.setEvaluation(score);
                result.setBestMove(move.toString());
            }
        }

        return result;
    }

    private int negamax(BBoard board, int depth, int alpha, int beta) {
        if (stopSearch || cantUseTime()) {
            stopSearch = true;
            return 0;
        }

        long zobristKey = board.state.getZobristKey();
        if (transpositionTable.containsKey(zobristKey)) {
            Transposition transposition = transpositionTable.get(zobristKey);
            if (transposition.depth() >= depth) {
                return transposition.score();
            }
        }


        if (depth <= 0 || stopSearch)
            return quiesce(board, alpha, beta);

        int bestScore = -INF;

        BMove[] moves = new MoveGenerator(board).generateMoves(false);
        MoveOrdering.orderMoves(board, moves, OrderStrategy.GENERAL);

        if (moves.length == 0) {
            if (board.isInCheck())
                return board.isWhiteToMove ? -MATE + depth : MATE - depth;
            else
                return 0;
        }

        for (BMove move : moves) {
            board.makeMove(move, true);

            int score = -negamax(board, depth - 1, -beta, -alpha);

            board.undoMove(move, true);

            bestScore = Math.max(bestScore, score);
            alpha = Math.max(alpha, score);
            if (alpha >= beta)
                break;
        }

        transpositionTable.put(zobristKey, new Transposition(bestScore, depth));

        return bestScore;
    }

    private int quiesce(BBoard board, int alpha, int beta) {
        if (stopSearch || cantUseTime()) {
            stopSearch = true;
            return 0;
        }

        int standPat = evaluateBoard(board);
        if (standPat >= beta) {
            return beta;
        }
        if (alpha < standPat) {
            alpha = standPat;
        }

        BMove[] moves = new MoveGenerator(board).generateMoves(true);
        moves = MoveOrdering.orderMoves(board, moves, OrderStrategy.QUIESCENT);

        for (BMove move : moves) {
            board.makeMove(move, true);

            int score = -quiesce(board, -beta, -alpha);

            board.undoMove(move, true);

            if (score >= beta) {
                return beta;
            }
            if (score > alpha) {
                alpha = score;
            }
        }

        return alpha;
    }

    private int evaluateBoard(BBoard board) {
        return Evaluation.evaluate(board);
    }

    private void finalizeSearch() {
    }

    public void stop() {
        this.stopSearch = true;
    }

    private boolean isDecisive(SearchResult result) {
        return false;
    }
}