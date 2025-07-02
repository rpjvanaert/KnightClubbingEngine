package knight.clubbing.opening;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PgnStreamer implements Iterable<String> {

    private final BufferedReader reader;

    public PgnStreamer(InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<>() {
            String nextPgn = null;

            @Override
            public boolean hasNext() {
                if (nextPgn != null) return true;
                try {
                    StringBuilder game = new StringBuilder();
                    String line;
                    boolean collecting = false;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("[Event ")) {
                            if (collecting) {
                                nextPgn = game.toString().trim();
                                game.setLength(0);
                                game.append(line).append("\n");
                                return true;
                            }
                            collecting = true;
                        }
                        if (collecting) {
                            game.append(line).append("\n");
                        }
                    }
                    if (game.length() == 0) return false;
                    nextPgn = game.toString().trim();
                    return true;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public String next() {
                if (!hasNext()) throw new NoSuchElementException();
                String result = nextPgn;
                nextPgn = null;
                return result;
            }
        };
    }

    public void close() throws IOException {
        reader.close();
    }
}
