package knight.clubbing;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

class UciIntegrationTest {

    private static final String UCI_TEST_DIR = "src/test/resources/uci-tests";

    private static Stream<File> provideUciTestFiles() {
        File folder = new File(UCI_TEST_DIR);
        return Stream.of(folder.listFiles((e, name) -> name.endsWith(".uci")));
    }

    @Tag("uci")
    @ParameterizedTest
    @MethodSource("provideUciTestFiles")
    void testUci(File file) throws IOException {
        List<String> lines = new BufferedReader(new FileReader(file))
                .lines()
                .toList();


        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;

        try {
            System.setOut(new PrintStream(output));
            String input = String.join("\n", lines) + "\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));

            UCI uci = new UCI();
            Thread uciThread = new Thread(uci::run);
            uciThread.start();

            Thread.sleep(10000); // todo remove sleep and instead wait if needed?
            uciThread.interrupt(); // gracefully interrupt

        } catch (InterruptedException e) {
            fail("Test was interrupted");
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }

        String outputStr = output.toString();
        System.out.println(outputStr);
    }
}
