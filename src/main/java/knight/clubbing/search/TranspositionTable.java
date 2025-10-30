package knight.clubbing.search;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TranspositionTable {

    private final Map<Long, TranspositionEntry> table;
    private int currentAge;

    public TranspositionTable() {
        this.table = new ConcurrentHashMap<>();
        this.currentAge = 0;
    }

    public void put(TranspositionEntry entry) {
        TranspositionEntry existing = table.get(entry.key());

        // Always insert if no existing entry
        if (existing == null) {
            table.put(entry.key(), entry);
            return;
        }

        // Always replace stale entries (from previous searches)
        if (existing.age() < currentAge) {
            table.put(entry.key(), entry);
            return;
        }

        if (entry.depth() >= existing.depth()) {
            table.put(entry.key(), entry);
            return;
        }

        int score = calculateReplacementScore(entry, existing);
        if (score > 0) {
            table.put(entry.key(), entry);
        }
    }

    private int calculateReplacementScore(TranspositionEntry newEntry, TranspositionEntry existing) {
        final int DEPTH_WEIGHT = 4;
        final int AGE_WEIGHT = 1;

        int depthDiff = newEntry.depth() - existing.depth();
        int ageDiff = newEntry.age() - existing.age();

        return depthDiff * DEPTH_WEIGHT + ageDiff * AGE_WEIGHT;
    }

    public TranspositionEntry get(long key) {
        return table.get(key);
    }

    public boolean contains(long key) {
        return table.containsKey(key);
    }

    public void clear() {
        table.clear();
        currentAge = 0;
    }

    public int getCurrentAge() {
        return currentAge;
    }

    public void incrementAge() {
        currentAge++;
    }

}