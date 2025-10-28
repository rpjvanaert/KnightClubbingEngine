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

        // Always replace if:
        // - No existing entry
        // - New entry has greater depth
        // - Same depth but newer age
        // - Entry is from a previous search (old age)
        if (existing == null ||
            entry.depth() >= existing.depth() ||
            entry.age() > existing.age()) {
            table.put(entry.key(), entry);
        }
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