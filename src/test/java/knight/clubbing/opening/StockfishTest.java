package knight.clubbing.opening;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Tag("stockfish")
class StockfishTest {

    @Test
    void testStartAndQuit() {
        Stockfish stockfish = new Stockfish();
        assertTrue(stockfish.start(), "Stockfish should start successfully");

        try {
            stockfish.quit();
        } catch (Exception e) {
            fail("Failed to quit Stockfish: " + e.getMessage());
        }
    }

    @Test
    void testUciCommand() {
        Stockfish stockfish = new Stockfish();
        assertTrue(stockfish.start(), "Stockfish should start successfully");

        try {
            stockfish.uci();
        } catch (IOException e) {
            fail("Failed to execute UCI command: " + e.getMessage());
        } finally {
            try {
                stockfish.quit();
            } catch (Exception e) {
                fail("Failed to quit Stockfish: " + e.getMessage());
            }
        }
    }

    @Test
    void testIsReadyCommand() {
        Stockfish stockfish = new Stockfish();
        assertTrue(stockfish.start(), "Stockfish should start successfully");

        try {
            stockfish.isReady();
        } catch (IOException e) {
            fail("Failed to execute isReady command: " + e.getMessage());
        } finally {
            try {
                stockfish.quit();
            } catch (Exception e) {
                fail("Failed to quit Stockfish: " + e.getMessage());
            }
        }
    }

    @Test
    void testBestMove() {
        Stockfish stockfish = new Stockfish();
        assertTrue(stockfish.start(), "Stockfish should start successfully");

        System.out.println("Stockfish started successfully");
        try {
            System.out.println("Stockfish started successfully");
            String bestMove = stockfish.bestMove( 5);
            assertNotNull(bestMove, "Best move should not be null");
            assertFalse(bestMove.isEmpty(), "Best move should not be empty");
            assertEquals("e2e4", bestMove, "Best move should be e2e4 for the starting position");
        } catch (IOException e) {
            fail("Failed to get best move: " + e.getMessage());
        } finally {
            try {
                stockfish.quit();
            } catch (Exception e) {
                fail("Failed to quit Stockfish: " + e.getMessage());
            }
        }
    }

    @Test
    void testTopMoves() {
        Stockfish stockfish = new Stockfish();
        assertTrue(stockfish.start(), "Stockfish should start successfully");

        try {
            stockfish.topMove("position startpos",1L, 10);
        } catch (IOException e) {
            fail("Failed to get top moves: " + e.getMessage());
        } finally {
            try {
                stockfish.quit();
            } catch (Exception e) {
                fail("Failed to quit Stockfish: " + e.getMessage());
            }
        }
    }

    @Test
    void testResourceCleanup() {
        Stockfish stockfish = new Stockfish();
        assertTrue(stockfish.start(), "Stockfish should start successfully");

        try {
            stockfish.quit();
        } catch (Exception e) {
            fail("Failed to quit Stockfish: " + e.getMessage());
        }

        assertDoesNotThrow(stockfish::close, "Resources should be cleaned up without exceptions");
    }
}