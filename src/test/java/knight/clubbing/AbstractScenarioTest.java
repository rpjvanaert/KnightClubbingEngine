package knight.clubbing;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BBoardHelper;
import knight.clubbing.core.BMove;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractScenarioTest {

    protected ExecutorService executor;
    protected int MAX_DEPTH;

    @BeforeEach
    protected void setUp() {
        executor = Executors.newCachedThreadPool();
        MAX_DEPTH = 7;
    }

    @AfterEach
    protected void tearDown() {
        executor.shutdown();
    }

    protected void expectMoveInPosition(BBoard game, BMove expected) throws InterruptedException {
        NegaMaxStart minimaxStart = new NegaMaxStart(MAX_DEPTH, executor);

        BMove move = minimaxStart.findBestMove(game);

        assertEquals(expected, move);
    }

    protected void expectMoveInPosition(String fen, BMove expected) throws InterruptedException {
        expectMoveInPosition(new BBoard(fen), expected);
    }

    protected void expectMoveInPosition(String fen, String from, String to) throws InterruptedException {
        expectMoveInPosition(fen, new BMove(BBoardHelper.stringCoordToIndex(from), BBoardHelper.stringCoordToIndex(to)));
    }
}
