package knight.clubbing.search.Iterative;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TranspositionTable {

    private final Map<Long, TranspositionEntry> table;

    public TranspositionTable() {
        this.table = new ConcurrentHashMap<>();
    }

    public void put(TranspositionEntry entry) {
        table.put(entry.key(), entry);
    }

    public TranspositionEntry get(long key) {
        return table.get(key);
    }

    public boolean contains(long key) {
        return table.containsKey(key);
    }

    public void clear() {
        table.clear();
    }

}