package knight.clubbing.integration;

import knight.clubbing.UCI;
import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
