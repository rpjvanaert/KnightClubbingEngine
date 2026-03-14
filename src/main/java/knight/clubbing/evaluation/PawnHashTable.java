package knight.clubbing.evaluation;


public class PawnHashTable {
    private static final int DEFAULT_SIZE = 65536; // 64KB
    private static PawnHashTable instance;

    private final PawnHashEntry[] table;
    private long hits = 0;
    private long misses = 0;

    private static class PawnHashEntry {
        long key;
        int score;

        PawnHashEntry(long key, int score) {
            this.key = key;
            this.score = score;
        }
    }

    private PawnHashTable(int size) {
        this.table = new PawnHashEntry[size];
    }
    public static synchronized PawnHashTable getInstance() {
        if (instance == null) {
            instance = new PawnHashTable(DEFAULT_SIZE);
        }
        return instance;
    }

    public synchronized Integer probe(long key) {
        int index = (int)(key & (table.length - 1));
        PawnHashEntry entry = table[index];

        if (entry != null && entry.key == key) {
            hits++;
            return entry.score;
        }

        misses++;
        return null;
    }

    public synchronized void store(long key, int score) {
        int index = (int)(key & (table.length - 1));
        table[index] = new PawnHashEntry(key, score);
    }

    public synchronized void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        hits = 0;
        misses = 0;
    }

    public synchronized String getStats() {
        long total = hits + misses;
        double hitRate = total > 0 ? (100.0 * hits / total) : 0;
        return String.format("Pawn hash: %d hits, %d misses (%.1f%% hit rate, %d entries)",
                hits, misses, hitRate, table.length);
    }

    public synchronized double getHitRate() {
        long total = hits + misses;
        return total > 0 ? (100.0 * hits / total) : 0;
    }
}
