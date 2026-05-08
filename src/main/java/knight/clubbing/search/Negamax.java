package knight.clubbing.search;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.core.BPiece;
import knight.clubbing.evaluation.DefaultEvaluator;
import knight.clubbing.movegen.MoveGenerator;
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
        this.evaluator = new DefaultEvaluator();
        this.orderer = new DefaultMoveOrderer();
    }

    @Override
    public SearchResponse search(BBoard board, SearchSettings settings) {
        startTime = System.currentTimeMillis();
        timeLimit = settings.timeLimit();

        this.settings = settings;
        this.stop = false;
        SearchResponse bestResponse = null;

        orderer.clearHistory();

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

        for (int moveIndex = 0; moveIndex < nextMoves.length; moveIndex++) {
            BMove move = nextMoves[moveIndex];
            if (shouldStop()) break;

            board.makeMove(move, true);
            int score;

            if (moveIndex == 0) {
                score = -negamax(board, depth - 1, -beta, -alpha, 1);
            } else {
                score = -negamax(board, depth - 1, -alpha - 1, -alpha, 1);

                if (score > alpha && score < beta) {
                    score = -negamax(board, depth - 1, -beta, -alpha, 1);
                }
            }

            board.undoMove(move, true);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move.getUci();
            }

            alpha = Math.max(alpha, score);
            if (alpha >= beta) {
                break;
            }
        }

        return new SearchResponse(bestScore, bestMove, depth, nodes, getTimeTakenMillis());
    }

    private int negamax(BBoard board, int depth, int alpha, int beta, int ply) {
        nodes++;

        if (containsTransposition(board)) {
            TranspositionEntry entry = getEntry(board);
            if (entry.getDepth() >= depth) {
                if (entry.getFlag() == 0) return entry.getScore();
                if (entry.getFlag() == 1 && entry.getScore() <= alpha) return entry.getScore();
                if (entry.getFlag() == 2 && entry.getScore() >= beta) return entry.getScore();
            }
        }

        if (shouldStop()) return 0;
        if (depth <= 0) return quiescence(board, alpha, beta, ply);

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

        if (depth >= 3 && !board.isInCheck() && ply > 0 && hasNonPawnMaterial(board)) {
            board.makeNullMove();
            int score = -negamax(board, depth - 3, -beta, -alpha, ply + 1);
            board.undoNullMove();

            if (score >= beta) {
                return beta;
            }
        }

        // PVS loop
        for (int moveIndex = 0; moveIndex < nextMoves.length; moveIndex++) {
            BMove move = nextMoves[moveIndex];

            // Capture status and color BEFORE making the move (bug fix!)
            boolean isCapture = board.getPieceBoards()[move.targetSquare()] != 0;
            boolean isWhite = board.isWhiteToMove();

            board.makeMove(move, true);
            int score;

            if (moveIndex == 0) {
                score = -negamax(board, depth - 1, -beta, -alpha, ply + 1);
            } else {
                score = -negamax(board, depth - 1, -alpha - 1, -alpha, ply + 1);

                if (score > alpha && score < beta) {
                    score = -negamax(board, depth - 1, -beta, -alpha, ply + 1);
                }
            }

            board.undoMove(move, true);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }

            alpha = Math.max(alpha, score);
            if (alpha >= beta) {
                if (!isCapture) {
                    orderer.updateHistory(move, depth, isWhite);

                    killerMoves[ply][1] = killerMoves[ply][0];
                    killerMoves[ply][0] = move;
                }
                break;
            } else {
                orderer.penalizeHistory(move, depth, isWhite, isCapture);
            }
        }

        transpositionTable.put(board.getState().getZobristKey(), new TranspositionEntry(depth, bestScore, TranspositionEntry.determineFlag(beta, bestScore, originalAlpha)));

        return bestScore;
    }

    private int quiescence(BBoard board, int alpha, int beta, int ply) {
        nodes++;

        int standPat = evaluator.evaluate(board);

        if (standPat >= beta) {
            return beta;
        }

        if (alpha < standPat) {
            alpha = standPat;
        }

        BMove[] captures = new MoveGenerator(board).generateMoves(true);
        orderer.order(captures, board, new MoveOrderingContext(ply, killerMoves));

        for (BMove move : captures) {
            board.makeMove(move, true);
            int score = -quiescence(board, -beta, -alpha, ply + 1);
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


    private boolean containsTransposition(BBoard board) {
        return transpositionTable.containsKey(board.getState().getZobristKey());
    }

    private TranspositionEntry getEntry(BBoard board) {
        return transpositionTable.get(board.getState().getZobristKey());
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

    private boolean hasNonPawnMaterial(BBoard board) {

        boolean whiteToMove = board.isWhiteToMove();

        if (checkIfHasPiece(board, whiteToMove, BPiece.rook)) return true;
        if (checkIfHasPiece(board, whiteToMove, BPiece.bishop)) return true;
        if (checkIfHasPiece(board, whiteToMove, BPiece.knight)) return true;
        if (checkIfHasPiece(board, whiteToMove, BPiece.queen)) return true;

        return false;
    }

    private static boolean checkIfHasPiece(BBoard board, boolean whiteToMove, int pieceIndex) {
        long bitboard = board.getBitboard(BPiece.makePiece(pieceIndex, whiteToMove));
        return bitboard != 0;
    }
}
