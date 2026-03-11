package knight.clubbing.search;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.movegen.MoveGenerator;
import knight.clubbing.evaluation.CpuEvaluator;
import knight.clubbing.evaluation.Evaluator;
import knight.clubbing.ordering.*;

import java.util.HashMap;
import java.util.Map;

import static knight.clubbing.search.EngineConst.INF;
import static knight.clubbing.search.EngineConst.MATE_SCORE;

public class Negamax implements Search {
    private volatile boolean stop;
    private long startTime;
    private long timeLimit;
    private long nodes;

    private SearchSettings settings;

    private final Evaluator evaluator;
    private final DefaultMoveOrderer orderer;

    private static final int MAX_DEPTH_KILLER = 32;
    private final BMove[][] killerMoves = new BMove[MAX_DEPTH_KILLER][2];

    private final Map<Long, TranspositionEntry> transpositionTable = new HashMap<>();

    public Negamax() {
        this.evaluator = new CpuEvaluator();
        this.orderer = new DefaultMoveOrderer();
    }

    @Override
    public SearchResponse search(BBoard board, SearchSettings settings) {
        startTime = System.currentTimeMillis();
        timeLimit = settings.timeLimit();

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
        int bestScore = -INF;
        nodes = 0;

        int alpha = -INF;
        int beta = INF;

        BMove[] nextMoves = new MoveGenerator(board).generateMoves(false);

        return searchSingleThreaded(board, depth, nextMoves, beta, alpha, bestScore);
    }

    private SearchResponse searchSingleThreaded(BBoard board, int depth, BMove[] nextMoves, int beta, int alpha, int bestScore) {
        String bestMove = null;
        nodes++;

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

        return new SearchResponse(bestScore, bestMove, depth, nodes, getTimeTakenMillis());
    }

    private int negamax(BBoard board, int depth, int alpha, int beta, int ply) {
        nodes++;

        if (containsTransposition(board)) {
            TranspositionEntry entry = getEntry(board);
            if (entry.getDepth() > depth) {
                if (entry.getFlag() == 0) return entry.getScore();
                if (entry.getFlag() == 1 && entry.getScore() <= alpha) return entry.getScore();
                if (entry.getFlag() == 2 && entry.getScore() >= beta) return entry.getScore();
            }
        }

        if (shouldStop()) return 0;
        if (depth <= 0) return evaluator.evaluate(board);

        int bestScore = -INF;
        BMove bestMove = null;
        int originalAlpha = alpha;

        BMove[] nextMoves = new MoveGenerator(board).generateMoves(false);

        orderer.order(nextMoves, board, new MoveOrderingContext(ply, killerMoves));

        if (nextMoves.length == 0) {
            if (board.isInCheck())
                return -MATE_SCORE + ply;
            else
                return 0;
        }

        if (board.isDrawByRepetition()) {
            return 0;
        }

        for (BMove move : nextMoves) {
            boolean isCapture = board.getPieceBoards()[move.targetSquare()] != 0;

            board.makeMove(move, true);
            int score = -negamax(board, depth - 1, -beta, -alpha, ply + 1);
            board.undoMove(move, true);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }

            alpha = Math.max(alpha, score);
            if (alpha >= beta) {
                orderer.updateHistory(move, depth, board.isWhiteToMove, isCapture);

                if (killerMoves[ply][0] == null || !killerMoves[ply][0].equals(move)) {
                    killerMoves[ply][1] = killerMoves[ply][0];
                    killerMoves[ply][0] = move;
                }
                break;
            } else {
                orderer.penalizeHistory(move, depth, board.isWhiteToMove, isCapture);
            }
        }



        transpositionTable.put(board.state.getZobristKey(), new TranspositionEntry(depth, bestScore, TranspositionEntry.determineFlag(beta, bestScore, originalAlpha)));

        return bestScore;
    }

    private boolean containsTransposition(BBoard board) {
        return transpositionTable.containsKey(board.state.getZobristKey());
    }

    private TranspositionEntry getEntry(BBoard board) {
        return transpositionTable.get(board.state.getZobristKey());
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
