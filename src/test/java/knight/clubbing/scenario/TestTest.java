package knight.clubbing.scenario;

import knight.clubbing.core.FenHelper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class TestTest extends AbstractScenarioTest {

    @Test
    @Tag("strength")
    public void test() throws InterruptedException {
        expectMoveInPosition(FenHelper.startFen, null);
    }
}
