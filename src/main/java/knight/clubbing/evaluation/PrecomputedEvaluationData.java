package knight.clubbing.evaluation;

import knight.clubbing.core.BCoord;

import java.util.ArrayList;
import java.util.List;

public class PrecomputedEvaluationData {

    protected static final int[][] pawnShieldSquaresWhite;
    protected static final int[][] pawnShieldSquaresBlack;

    static {
        pawnShieldSquaresWhite = new int[64][];
        pawnShieldSquaresBlack = new int[64][];
        for (int squareIndex = 0; squareIndex < 64; squareIndex++) {
            createPawnShieldSquare(squareIndex);
        }
    }

    private static void createPawnShieldSquare(int squareIndex) {
        List<Integer> shieldSquaresWhite = new ArrayList<>();
        List<Integer> shieldSquaresBlack = new ArrayList<>();
        BCoord coord = new BCoord(squareIndex);
        int rank = coord.getRankIndex();
        int file = Math.clamp(coord.getFileIndex(), 1, 6);

        for (int fileOffset = -1; fileOffset <= 1; fileOffset++) {
            addToList(file, fileOffset, rank + 1, shieldSquaresWhite);
            addToList(file, fileOffset, rank - 1, shieldSquaresBlack);

            addToList(file, fileOffset, rank + 2, shieldSquaresWhite);
            addToList(file, fileOffset, rank - 2, shieldSquaresBlack);
        }

        pawnShieldSquaresWhite[squareIndex] = shieldSquaresWhite.stream().mapToInt(i -> i).toArray();
        pawnShieldSquaresBlack[squareIndex] = shieldSquaresBlack.stream().mapToInt(i -> i).toArray();
    }

    private static void addToList(int file, int fileOffset, int rank, List<Integer> shieldSquares) {
        BCoord coordWhite1 = new BCoord(file + fileOffset, rank);
        if (coordWhite1.isValidSquare()) {
            shieldSquares.add(coordWhite1.getSquareIndex());
        }
    }
}
