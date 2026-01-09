package knight.clubbing.search;

import knight.clubbing.core.BBoard;

public interface Search {
    SearchResponse search(BBoard board, SearchSettings settings);
}
