package knight.clubbing.search.redo;

import knight.clubbing.core.BBoard;
import knight.clubbing.search.singleThreaded.Search;
import knight.clubbing.search.singleThreaded.SearchConfig;
import knight.clubbing.search.singleThreaded.SearchResult;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchTest {

    @Test @Tag("strength")
    public void testBasic() {
        BBoard board = new BBoard();
        SearchConfig config = new SearchConfig(5);
        Search search = new Search(config);
        SearchResult result = search.search(board);
        System.out.println(result);
    }
}