package knight.clubbing;

import knight.clubbing.core.BBoard;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KnightClubbingEngine {
    private static final int MAX_DEPTH = 7;
    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public static void main(String[] args) throws InterruptedException {
        // Other
        // r1bq1rk1/p1p1bppp/1pn1pn2/3p4/2PP4/2N1PN2/PP2BPPP/R1BQ1RK1 w - - 0 8

        // Mate in 1:
        // 4r2k/1p3rbp/2p1N1p1/p3n3/P2NB1nq/1P6/4R1P1/B1Q2RK1 b - - 4 32

        // Mate in 2:
        // r1bq2r1/b4pk1/p1pp1p2/1p2pP2/1P2P1PB/3P4/1PPQ2P1/R3K2R w - - 0 1
        // kbK5/pp6/1P6/8/8/8/8/R7 w - - 0 1

        // Mate in 3:
        // r5rk/5p1p/5R2/4B3/8/8/7P/7K w - - 0 1

        //*
        long start = System.currentTimeMillis();

        BBoard game = new BBoard("r1bq1rk1/p1p1bppp/1pn1pn2/3p4/2PP4/2N1PN2/PP2BPPP/R1BQ1RK1 w - - 0 8");

        MinimaxStart minimaxStart = new MinimaxStart(MAX_DEPTH, executor);

        System.out.println("Best move: " + minimaxStart.findBestMove(game));
        long stop = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (stop - start) + "ms");
        executor.shutdown();
    }
}
