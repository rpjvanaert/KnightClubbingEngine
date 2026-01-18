package knight.clubbing.ordering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.ordering.BasicMoveOrderer;
import knight.clubbing.ordering.OrderFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicMoveOrdererTest {
    private BBoard mockBoard;
    private BMove move1, move2, move3;

    @BeforeEach
    void setUp() {
        mockBoard = new BBoard();
        move1 = new BMove(1, 2);
        move2 = new BMove(3, 2);
        move3 = new BMove(2, 3);
    }

    @Test
    void testOrderMovesWithSingleFeature() {
        OrderFeature feature = new OrderFeature() {
            @Override
            public int score(BMove move, BBoard board) {
                if (move == move1) return 10;
                if (move == move2) return 30;
                if (move == move3) return 20;
                return 0;
            }

            @Override
            public String name() {
                return "";
            }
        };

        BasicMoveOrderer orderer = new BasicMoveOrderer(feature);
        BMove[] moves = {move1, move2, move3};

        orderer.order(moves, mockBoard, null);

        assertEquals(move2, moves[0]);
        assertEquals(move3, moves[1]);
        assertEquals(move1, moves[2]);
    }

    @Test
    void testOrderMovesWithMultipleFeatures() {
        OrderFeature feature1 = new OrderFeature() {
            @Override
            public int score(BMove move, BBoard board) {
                if (move == move1) return 10;
                if (move == move2) return 20;
                return 0;
            }

            @Override
            public String name() {
                return "";
            }
        };

        OrderFeature feature2 = new OrderFeature() {
            @Override
            public int score(BMove move, BBoard board) {
                if (move == move1) return 15;
                if (move == move3) return 10;
                return 0;
            }

            @Override
            public String name() {
                return "";
            }
        };

        BasicMoveOrderer orderer = new BasicMoveOrderer(feature1, feature2);
        BMove[] moves = {move1, move2, move3};

        orderer.order(moves, mockBoard, null);

        assertEquals(move1, moves[0]);
        assertEquals(move2, moves[1]);
        assertEquals(move3, moves[2]);
    }

    @Test
    void testOrderMovesWithEmptyArray() {
        OrderFeature feature = new OrderFeature() {
            @Override
            public int score(BMove move, BBoard board) {
                return 10;
            }

            @Override
            public String name() {
                return "";
            }
        };
        BasicMoveOrderer orderer = new BasicMoveOrderer(feature);
        BMove[] moves = {};

        assertDoesNotThrow(() -> orderer.order(moves, mockBoard, null));
    }

    @Test
    void testOrderMovesWithSingleMove() {
        OrderFeature feature = new OrderFeature() {
            @Override
            public int score(BMove move, BBoard board) {
                return 10;
            }

            @Override
            public String name() {
                return "";
            }
        };
        BasicMoveOrderer orderer = new BasicMoveOrderer(feature);
        BMove[] moves = {move1};

        orderer.order(moves, mockBoard, null);

        assertEquals(move1, moves[0]);
    }

    @Test
    void testOrderMovesWithEqualScores() {
        OrderFeature feature = new OrderFeature() {
            @Override
            public int score(BMove move, BBoard board) {
                return 10;
            }

            @Override
            public String name() {
                return "";
            }
        };
        BasicMoveOrderer orderer = new BasicMoveOrderer(feature);
        BMove[] moves = {move1, move2, move3};

        orderer.order(moves, mockBoard, null);

        assertEquals(move1, moves[0]);
        assertEquals(move2, moves[1]);
        assertEquals(move3, moves[2]);
    }

    @Test
    void testOrderMovesWithNoFeatures() {
        BasicMoveOrderer orderer = new BasicMoveOrderer();
        BMove[] moves = {move1, move2, move3};

        orderer.order(moves, mockBoard, null);

        assertEquals(move1, moves[0]);
        assertEquals(move2, moves[1]);
        assertEquals(move3, moves[2]);
    }

}