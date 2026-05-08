package knight.clubbing.search;

/**
 * Exception thrown when search is interrupted due to time limit or external stop signal.
 */
public class SearchInterruptedException extends RuntimeException {
    public SearchInterruptedException() {
        super();
    }

    public SearchInterruptedException(String message) {
        super(message);
    }
}

