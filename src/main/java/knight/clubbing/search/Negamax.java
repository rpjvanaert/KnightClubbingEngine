package knight.clubbing.search;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.movegen.MoveGenerator;
import knight.clubbing.opening.OpeningBookEntry;
import knight.clubbing.opening.OpeningService;
import knight.clubbing.evaluation.CpuEvaluator;
import knight.clubbing.evaluation.Evaluator;
import knight.clubbing.ordering.BasicMoveOrderer;
import knight.clubbing.ordering.MoveOrderer;
import knight.clubbing.ordering.MoveOrderingContext;
import knight.clubbing.ordering.MvvLvaFeature;

import static knight.clubbing.search.EngineConst.INF;
import static knight.clubbing.search.EngineConst.MATE_SCORE;

public class Negamax implements Search {
    private volatile boolean stop;
    private long startTime;
    private long timeLimit;
    private long nodes;

    private SearchSettings settings;

    private final Evaluator evaluator;
    private final MoveOrderer orderer;

    private final OpeningService openingService;

    private static final int MAX_DEPTH_KILLER = 32;
    private final BMove[][] killerMoves = new BMove[MAX_DEPTH_KILLER][2];

    public Negamax() {
        this.openingService = new OpeningService();
        this.evaluator = new CpuEvaluator();
        this.orderer = new BasicMoveOrderer(new MvvLvaFeature());
    }

    public Negamax(OpeningService openingService) {
        this.openingService = openingService;
        this.evaluator = new CpuEvaluator();
        this.orderer = new BasicMoveOrderer(new MvvLvaFeature());
    }

    @Override
    public SearchResponse search(BBoard board, SearchSettings settings) {
        startTime = System.currentTimeMillis();
        timeLimit = settings.timeLimit();

        if (openingService.exists(board.state.getZobristKey())) {
            OpeningBookEntry entry = openingService.getBest(board.state.getZobristKey());
            return new SearchResponse(entry.getScore(), entry.getMove(), 0, 0, getTimeTakenMillis());
        }

        this.settings = settings;
        this.stop = false;
        SearchResponse bestResponse = null;

        for (int depth = 1; !stop && depth <= settings.maxDepth(); depth++) {

            SearchResponse result = searchAtDepth(board, depth);
            bestResponse = result;

            if (shouldStop()) break;

            long elapsed = getTimeTakenMillis();
            String pv = result.bestMove() != null ? result.bestMove() : "";
            System.out.println("info depth " + depth + " score cp " + result.score() + " time " + elapsed + " pv " + pv);

            if (isDecisive(result)) break;
        }


        return bestResponse;
    }

    private SearchResponse searchAtDepth(BBoard board, int depth) {
        String bestMove = null;
        int bestScore = -INF;

        int alpha = -INF;
        int beta = INF;

        BMove[] nextMoves = new MoveGenerator(board).generateMoves(false);

        return searchSingleThreaded(board, depth, nextMoves, beta, alpha, bestScore);
    }

    private SearchResponse searchSingleThreaded(BBoard board, int depth, BMove[] nextMoves, int beta, int alpha, int bestScore) {
        String bestMove = null;

        orderer.order(nextMoves, board, new MoveOrderingContext(0, killerMoves));

        for (BMove move : nextMoves) {
            if (shouldStop()) break;

            board.makeMove(move, true);
            int score = -negamax(board, depth - 1, -beta, -alpha, 1);
            board.undoMove(move, true);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move.getUci();
            }

            alpha = Math.max(alpha, score);
        }

        return new SearchResponse(bestScore, bestMove, depth, 0, getTimeTakenMillis());
    }

    private int negamax(BBoard board, int depth, int alpha, int beta, int ply) {
        if (shouldStop()) {
            return 0;
        }

        if (depth <= 0)
            return evaluator.evaluate(board);

        int bestScore = -INF;
        BMove bestMove = null;

        BMove[] nextMoves = new MoveGenerator(board).generateMoves(false);

        orderer.order(nextMoves, board, new MoveOrderingContext(ply, killerMoves));

        if (nextMoves.length == 0) {
            if (board.isInCheck())
                return -MATE_SCORE + ply;
            else
                return 0;
        }

        for (BMove move : nextMoves) {
            board.makeMove(move, true);
            int score = -negamax(board, depth - 1, -beta, -alpha, ply + 1);
            board.undoMove(move, true);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }

            alpha = Math.max(alpha, score);
            if (alpha >= beta) {
                if (killerMoves[ply][0] == null || !killerMoves[ply][0].equals(move)) {
                    killerMoves[ply][1] = killerMoves[ply][0];
                    killerMoves[ply][0] = move;
                }
                break;
            }
        }

        return bestScore;
    }


    private boolean isDecisive(SearchResponse response) {
        return Math.abs(response.score()) >= MATE_SCORE - settings.maxDepth();
    }

    private long getTimeTakenMillis() {
        return System.currentTimeMillis() - startTime;
    }

    private boolean shouldStop() {
        if (stop) return true;
        stop = timeLimit > 0 && getTimeTakenMillis() >= timeLimit;
        return stop;
    }
}
