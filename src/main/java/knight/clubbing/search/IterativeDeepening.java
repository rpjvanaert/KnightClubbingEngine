package knight.clubbing.search;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.evaluation.Evaluation;
import knight.clubbing.moveOrdering.MoveOrdering;
import knight.clubbing.moveOrdering.OrderStrategy;
import knight.clubbing.movegen.MoveGenerator;
import knight.clubbing.opening.OpeningBookEntry;
import knight.clubbing.opening.OpeningService;

import static knight.clubbing.search.SearchConstants.INF;
import static knight.clubbing.search.SearchConstants.MATE;

public class IterativeDeepening {

    private static final int ASPIRATION_WINDOW = 50;

    private BBoard board;
    private SearchConfig config;
    private boolean stopSearch;
    private SearchResult bestResult;
    private long startTime;

    private final TranspositionTable transpositionTable;

    private final OpeningService openingService;

    public IterativeDeepening(BBoard board) {
        this.board = board;
        this.config = null;
        this.stopSearch = false;
        this.bestResult = new SearchResult();
        this.transpositionTable = new TranspositionTable();
        this.openingService = new OpeningService();
    }

    public SearchResult search(SearchConfig config) {
        if (openingService.exists(board.state.getZobristKey())) {
            OpeningBookEntry entry = openingService.getBest(board.state.getZobristKey());
            return new SearchResult(entry.getMove(), entry.getScore());
        }


        this.config = config;
        this.stopSearch = false;
        this.bestResult = new SearchResult();

        initializeSearch();

        for (int depth = 1; !stopSearch; depth++) {

            SearchResult result = searchAtDepth(depth, this.bestResult);

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

        return System.currentTimeMillis() - startTime >= config.timeLimit();
    }

    private SearchResult searchAtDepth(int depth, SearchResult prevResult) {
        System.out.println("Searching at depth: " + depth);
        SearchResult result = new SearchResult();

        int alpha = prevResult == null ? -INF : prevResult.getEvaluation() - ASPIRATION_WINDOW;
        int beta = prevResult == null ? INF : prevResult.getEvaluation() + ASPIRATION_WINDOW;

        BMove[] moves = new MoveGenerator(board).generateMoves(false);
        MoveOrdering.orderMoves(board, moves, OrderStrategy.GENERAL);

        for (BMove move : moves) {
            if (stopSearch || cantUseTime()) {
                stopSearch = true;
                break;
            }

            board.makeMove(move, true);

            int score;
            do {
                score = -negamax(board.copy(), depth, -beta, -alpha, 0);

                if (score <= alpha) {
                    alpha -= ASPIRATION_WINDOW;
                } else if (score >= beta) {
                    beta += ASPIRATION_WINDOW;
                } else {
                    break;
                }
            } while (true);

            board.undoMove(move, true);

            if (score > result.getEvaluation()) {
                result.setEvaluation(score);
                result.setBestMove(move.toString());
            }
        }

        return result;
    }

    private int negamax(BBoard board, int depth, int alpha, int beta, int ply) {
        if (stopSearch || cantUseTime()) {
            stopSearch = true;
            return 0;
        }

        long zobristKey = board.state.getZobristKey();
        TranspositionEntry entry = transpositionTable.get(zobristKey);
        if (entry != null && entry.depth() >= depth) {
            if (entry.nodeType() == TranspositionEntry.EXACT) {
                return entry.value();
            } else if (entry.nodeType() == TranspositionEntry.LOWER_BOUND && entry.value() > alpha) {
                alpha = entry.value();
            } else if (entry.nodeType() == TranspositionEntry.UPPER_BOUND && entry.value() < beta) {
                beta = entry.value();
            }

            if (alpha >= beta) {
                return entry.value();
            }
        }

        if (depth <= 0 || stopSearch)
            return quiesce(board, alpha, beta, ply);

        int bestScore = -INF;

        BMove[] moves = new MoveGenerator(board).generateMoves(false);
        MoveOrdering.orderMoves(board, moves, OrderStrategy.GENERAL);

        if (moves.length == 0) {
            if (board.isInCheck())
                return board.isWhiteToMove ? -MATE + ply : MATE - ply;
            else
                return 0;
        }

        for (BMove move : moves) {
            board.makeMove(move, true);

            int score = -negamax(board, depth - 1, -beta, -alpha, ply + 1);

            board.undoMove(move, true);

            bestScore = Math.max(bestScore, score);
            alpha = Math.max(alpha, score);
            if (alpha >= beta)
                break;
        }

        short flag;
        if (bestScore <= alpha) {
            flag = TranspositionEntry.UPPER_BOUND;
        } else if (bestScore >= beta) {
            flag = TranspositionEntry.LOWER_BOUND;
        } else {
            flag = TranspositionEntry.EXACT;
        }

        transpositionTable.put(new TranspositionEntry(zobristKey, bestScore, null, (short) depth, flag));

        return bestScore;
    }

    private int quiesce(BBoard board, int alpha, int beta, int ply) {
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

        BMove[] moves = new MoveGenerator(board).generateMoves(false);
        moves = MoveOrdering.orderMoves(board, moves, OrderStrategy.QUIESCENT);

        for (BMove move : moves) {
            board.makeMove(move, true);

            int score = -quiesce(board, -beta, -alpha, ply + 1);

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