package knight.clubbing.search;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.evaluation.Evaluation;
import knight.clubbing.moveOrdering.MoveOrdering;
import knight.clubbing.moveOrdering.OrderStrategy;
import knight.clubbing.movegen.MoveGenerator;
import knight.clubbing.opening.OpeningBookEntry;
import knight.clubbing.opening.OpeningService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static knight.clubbing.search.SearchConstants.INF;
import static knight.clubbing.search.SearchConstants.MATE;

public class IterativeDeepening {

    private static final int ASPIRATION_WINDOW = 50;

    private BBoard board;
    private SearchConfig config;
    private volatile boolean stopSearch;
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

            SearchResult result = searchAtDepth(depth, this.bestResult, config);

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
        this.transpositionTable.incrementAge();
    }

    private boolean cantUseTime() {
        return System.currentTimeMillis() - startTime >= config.timeLimit();
    }

    private SearchResult searchAtDepth(int depth, SearchResult prevResult, SearchConfig config) {
        System.out.println("Searching at depth: " + depth);
        SearchResult result = new SearchResult();

        int alpha = prevResult == null ? -INF : prevResult.getEvaluation() - ASPIRATION_WINDOW;
        int beta = prevResult == null ? INF : prevResult.getEvaluation() + ASPIRATION_WINDOW;

        BMove[] moves = new MoveGenerator(board).generateMoves(false);
        MoveOrdering.orderMoves(board, moves, OrderStrategy.GENERAL);

        if (config.threads() == 1)
            return searchSingleThreaded(depth, moves, beta, alpha, result);

        if (config.threads() > 1) {
            return searchMultiThreaded(depth, config, moves, beta, alpha);
        }

        return result;
    }

    private SearchResult searchMultiThreaded(int depth, SearchConfig config, BMove[] moves, int beta, int alpha) {
        ExecutorService executor = Executors.newFixedThreadPool(config.threads());
        List<Future<SearchResult>> futures = new ArrayList<>();
        SearchResult bestResult = new SearchResult();
        bestResult.setEvaluation(-INF);

        int movesPerThread = Math.max(1, moves.length / config.threads());
        int remainingMoves = moves.length % config.threads();

        int moveIndex = 0;
        for (int threadId = 0; threadId < config.threads() && moveIndex < moves.length; threadId++) {
            int currentThreadMoves = movesPerThread + (threadId < remainingMoves ? 1 : 0);

            BMove[] threadMoves = new BMove[Math.min(currentThreadMoves, moves.length - moveIndex)];
            System.arraycopy(moves, moveIndex, threadMoves, 0, threadMoves.length);
            moveIndex += threadMoves.length;

            Future<SearchResult> future = executor.submit(() -> {
                SearchResult threadResult = new SearchResult();
                threadResult.setEvaluation(-INF);

                for (BMove move : threadMoves) {
                    if (stopSearch || cantUseTime() || Thread.currentThread().isInterrupted()) {
                        stopSearch = true;
                        break;
                    }

                    BBoard threadBoard = board.copy();
                    threadBoard.makeMove(move, true);

                    int score;
                    int threadAlpha = alpha;
                    int threadBeta = beta;

                    do {
                        score = -negamax(threadBoard, depth, -threadBeta, -threadAlpha, 0);

                        if (score <= threadAlpha) {
                            threadAlpha -= ASPIRATION_WINDOW;
                        } else if (score >= threadBeta) {
                            threadBeta += ASPIRATION_WINDOW;
                        } else {
                            break;
                        }
                    } while (true);

                    threadBoard.undoMove(move, true);

                    if (score > threadResult.getEvaluation()) {
                        threadResult.setEvaluation(score);
                        threadResult.setBestMove(move.toString());
                    }
                }

                return threadResult;
            });

            futures.add(future);
        }

        try {
            for (Future<SearchResult> future : futures) {
                if (stopSearch) {
                    future.cancel(true);
                    continue;
                }
                SearchResult threadResult = future.get();
                if (threadResult.getEvaluation() > bestResult.getEvaluation()) {
                    bestResult = threadResult;
                }
            }
        } catch (InterruptedException e) {
            // Restore interrupt status and signal stop
            Thread.currentThread().interrupt();
            stopSearch = true;
            System.err.println("Search interrupted");
        } catch (ExecutionException e) {
            System.err.println("Error in multi-threaded search: " + e.getCause());
        } finally {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                executor.shutdownNow();
            }
        }

        return bestResult;
    }

    private SearchResult searchSingleThreaded(int depth, BMove[] moves, int beta, int alpha, SearchResult result) {
        for (BMove move : moves) {
            if (stopSearch || cantUseTime() || Thread.currentThread().isInterrupted()) {
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
        if (stopSearch || cantUseTime() || Thread.currentThread().isInterrupted()) {
            stopSearch = true;
            return 0;
        }

        int alphaOrig = alpha;
        long zobristKey = board.state.getZobristKey();
        TranspositionEntry entry;
        synchronized (transpositionTable) {
            entry = transpositionTable.get(zobristKey);
        }
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
        BMove bestMove = null;

        BMove[] moves = new MoveGenerator(board).generateMoves(false);
        MoveOrdering.orderMoves(board, moves, OrderStrategy.GENERAL);

        if (moves.length == 0) {
            if (board.isInCheck())
                return board.isWhiteToMove ? -MATE + ply : MATE - ply;
            else
                return 0;
        }

        for (BMove move : moves) {
            BBoard childBoard = board.copy();
            childBoard.makeMove(move, true);

            int score = -negamax(childBoard, depth - 1, -beta, -alpha, ply + 1);

            childBoard.undoMove(move, true);

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }

            alpha = Math.max(alpha, score);
            if (alpha >= beta) {
                break;
            }
        }

        // Determine the bound type based on how the score relates to the window
        short flag;
        if (bestScore <= alphaOrig) {
            flag = TranspositionEntry.UPPER_BOUND;
        } else if (bestScore >= beta) {
            flag = TranspositionEntry.LOWER_BOUND;
        } else {
            flag = TranspositionEntry.EXACT;
        }

        // Always store entry with depth, score, bound, best-move, and age
        synchronized (transpositionTable) {
            transpositionTable.put(new TranspositionEntry(
                zobristKey,
                bestScore,
                bestMove,
                (short) depth,
                flag,
                transpositionTable.getCurrentAge()
            ));
        }

        return bestScore;
    }

    private int quiesce(BBoard board, int alpha, int beta, int ply) {
        if (stopSearch || cantUseTime() || Thread.currentThread().isInterrupted()) {
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