package knight.clubbing.revamp.ordering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.core.BPiece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MvvLvaFeatureTest {

    private MvvLvaFeature mvvLvaFeature;

    @BeforeEach
    void setUp() {
        mvvLvaFeature = new MvvLvaFeature();
    }

    @Test
    void testName() {
        assertEquals("MVV-LVA Feature", mvvLvaFeature.name());
    }

}