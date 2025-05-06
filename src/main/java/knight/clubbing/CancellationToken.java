package knight.clubbing;

import java.util.concurrent.atomic.AtomicBoolean;

public class CancellationToken {
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    public boolean isCancelled() {
        return cancelled.get();
    }

    public void cancel() {
        cancelled.set(true);
    }
}
