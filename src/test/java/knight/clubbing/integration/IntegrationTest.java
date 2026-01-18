package knight.clubbing.integration;

import knight.clubbing.UCI;
import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("uci")
public class IntegrationTest {

    InputStream originalIn;
    PrintStream originalOut;

    ByteArrayOutputStream output;

    Thread engineThread;

    @BeforeEach
    void setup() {
        this.originalIn = System.in;
        this.originalOut = System.out;

        this.output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void teardown() throws InterruptedException {
        System.setIn(originalIn);
        System.setOut(originalOut);

        if (engineThread != null && engineThread.isAlive()) {
            engineThread.interrupt();
            engineThread.join();
        }
    }

    private void startEngineWithInput(String text) {
        System.setIn(new ByteArrayInputStream(text.getBytes()));
        this.engineThread = new Thread(new UCI()::run);
        this.engineThread.start();
    }

    private void waitForOutput(String expected, long millis) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < millis) {
            String currentOutput = output.toString();
            if (currentOutput.contains(expected)) {
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Assertions.fail("Interrupted while waiting for output: " + expected);
            }
        }

        // Restore output and print for debugging
        System.setOut(originalOut);
        System.out.println("Actual output:\n" + output.toString());
        Assertions.fail("Timeout waiting for output: " + expected);
    }

    @Test
    void testUciOk() {
        // Input and wait
        startEngineWithInput("uci\n");
        waitForOutput("uciok", 5000);

        // Restore output for assertions
        System.setOut(originalOut);
        String actualOutput = output.toString();

        // Assert
        System.out.println("Test output:\n" + actualOutput);
        assertTrue(actualOutput.contains("id name"), "Should contain engine name");
        assertTrue(actualOutput.contains("id author"), "Should contain engine author");
        assertTrue(actualOutput.contains("uciok"), "Should contain uciok");
    }

    @Test
    void testisReady() {
        // Input and wait
        startEngineWithInput("isready\n");
        waitForOutput("readyok", 5000);

        // Restore output for assertions
        System.setOut(originalOut);
        String actualOutput = output.toString();

        // Assert
        System.out.println("Test output:\n" + actualOutput);
        assertEquals("readyok", actualOutput.trim(), "Should respond with readyok");
    }

    @Test
    void testPositionCommand() {
        // Input and wait
        startEngineWithInput("position startpos moves e2e4 e7e5 g1f3 b8c6 d2d4 e5d4 f3d4 c6d4 d1d4 g8f6 e4e5 f6h5\nisready\n");
        waitForOutput("readyok", 5000);

        // Restore output for assertions
        System.setOut(originalOut);
        String actualOutput = output.toString();

        // Assert
        System.out.println("Test output:\n" + actualOutput);
        assertTrue(actualOutput.contains("readyok"), "Should respond with readyok after position command");
    }

    @Test
    void testGoCommand() {
        // Input and wait
        startEngineWithInput("""
                        position startpos moves e2e4 e7e5 g1f3 b8c6 d2d4 e5d4 f3d4 c6d4 d1d4 g8f6 e4e5 f6h5
                        isready
                        go wtime 1000 btime 1000 winc 0 binc 0
                        """
        );
        waitForOutput("bestmove", 1500);

        // Restore output for assertions
        System.setOut(originalOut);
        String actualOutput = output.toString();

        // Assert
        System.out.println("Test output:\n" + actualOutput);
        assertTrue(actualOutput.contains("info depth"), "Should respond with search info after go command");
        assertTrue(actualOutput.contains("bestmove"), "Should respond with bestmove after go command");
    }

    @Test
    void testIssue1() {
        // Input and wait
        startEngineWithInput("""
                        uci
                        isready
                        ucinewgame
                        position startpos moves c2c4
                        isready
                        go wtime 1000 btime 1000 winc 0 binc 0
                        """
        );
        waitForOutput("bestmove", 1500);

        // Restore output for assertions
        System.setOut(originalOut);
        String actualOutput = output.toString();

        // Assert
        System.out.println("Test output:\n" + actualOutput);
        int readyokCount = actualOutput.split("readyok", -1).length - 1;
        assertEquals(2, readyokCount, "Should respond with readyok twice");
        assertTrue(actualOutput.contains("bestmove"), "Should respond with bestmove after go command");
    }
}
