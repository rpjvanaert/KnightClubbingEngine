package knight.clubbing.ordering;

import knight.clubbing.ordering.MvvLvaFeature;
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