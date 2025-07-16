package knight.clubbing.opening;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PgnStreamer implements Iterable<String> {

    private final BufferedReader reader;
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private volatile boolean finished = false;

    public PgnStreamer(InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        startProducerThread();
    }

    private void startProducerThread() {
        new Thread(() -> {
            try {
                StringBuilder game = new StringBuilder();
                String line;
                boolean collecting = false;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("[Event ")) {
                        if (collecting) {
                            String potentialPgn = game.toString().trim();
                            if (PgnParser.isWorthy(potentialPgn)) {
                                queue.put(potentialPgn);
                            }
                            game.setLength(0);
                        }
                        collecting = true;
                    }
                    if (collecting) {
                        game.append(line).append("\n");
                    }
                }
                if (game.length() > 0) {
                    String potentialPgn = game.toString().trim();
                    if (PgnParser.isWorthy(potentialPgn)) {
                        queue.put(potentialPgn);
                    }
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                finished = true;
            }
        }).start();
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<>() {
            String nextPgn = null;

            @Override
            public boolean hasNext() {
                if (nextPgn != null) return true;
                try {
                    while (!finished || !queue.isEmpty()) {
                        nextPgn = queue.poll();
                        if (nextPgn != null) return true;
                    }
                    return false;
                } catch (Exception e) {
                    throw new RuntimeException(e);
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