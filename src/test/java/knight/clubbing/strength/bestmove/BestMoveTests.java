package knight.clubbing.strength.bestmove;

import com.fasterxml.jackson.databind.ObjectMapper;
import knight.clubbing.core.BBoard;
import knight.clubbing.search.Negamax;
import knight.clubbing.search.Search;
import knight.clubbing.search.SearchResponse;
import knight.clubbing.search.SearchSettings;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("strength")
class BestMoveTests {

    static Stream<BestmovePuzzle> providePuzzles() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = BestMoveTests.class
                .getResourceAsStream("/bestmove-puzzles.json");

        assertNotNull(inputStream, "Could not find bestmove-puzzles.json");

        BestmovePuzzleContainer container = mapper.readValue(inputStream, BestmovePuzzleContainer.class);
        return container.getPuzzles().stream();
    }

    @ParameterizedTest @Tag("strength")
    @MethodSource("providePuzzles")
    void testBestMoveInPuzzle(BestmovePuzzle puzzle) {
        // Arrange
        BBoard board = new BBoard(puzzle.getFen());
        Search search = new Negamax();

        // Act
        SearchResponse result = search.search(board, new SearchSettings(10, 1000, 1, false));

        // Assert
        assertNotNull(result, "Search should return a result");
        assertNotNull(result.bestMove(), "Search should return a best move");
        assertFalse(result.bestMove().isEmpty(), "Best move should not be empty");

        String description = puzzle.getDescription() != null
                ? puzzle.getDescription()
                : "No description provided";

        assertTrue(
                puzzle.getBestMoves().contains(result.bestMove()),
                String.format(
                        "[%s] Expected best move to be one of %s for position '%s', but got '%s'",
                        description,
                        puzzle.getBestMoves(),
                        puzzle.getFen(),
                        result.bestMove()
                )
        );
    }
}