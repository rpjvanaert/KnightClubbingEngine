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

    @Test @Tag("strength")
    public void testBasic1() {
        BBoard board = new BBoard("r1bqkb1r/pppp1ppp/8/4P2n/3Q4/8/PPP2PPP/RNB1KB1R w KQkq - 1 7");
        SearchConfig config = new SearchConfig(5);
        Search search = new Search(config);
        SearchResult result = search.search(board);
        System.out.println(result);
    }
}