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

        //*
        long start = System.currentTimeMillis();

        BBoard game = new BBoard("r1bq1rk1/p1p1bppp/1pn1pn2/3p4/2PP4/2N1PN2/PP2BPPP/R1BQ1RK1 w - - 0 8");

        NegaMaxStart minimaxStart = new NegaMaxStart(MAX_DEPTH, executor);

        System.out.println("Best move: " + minimaxStart.findBestMove(game));
        long stop = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (stop - start) + "ms");
        executor.shutdown();
    }
}
