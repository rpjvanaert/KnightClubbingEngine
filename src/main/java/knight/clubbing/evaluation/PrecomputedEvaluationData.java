package knight.clubbing.evaluation;

import knight.clubbing.core.BBoardHelper;
import knight.clubbing.core.BCoord;
import knight.clubbing.moveGeneration.MoveUtility;

import java.util.ArrayList;
import java.util.List;

import static knight.clubbing.moveGeneration.MoveUtility.FileA;

public class PrecomputedEvaluationData {

    protected static final int[][] pawnShieldSquaresWhite;
    protected static final int[][] pawnShieldSquaresBlack;

    private static long[] whitePassedPawnMask;
    private static long[] blackPassedPawnMask;

    private static long[] whitePawnSupportMask;
    private static long[] blackPawnSupportMask;

    private static long[] whiteForwardFileMask;
    private static long[] blackForwardFileMask;

    private static long[] fileMask;
    private static long[] adjacentFileMask;

    static {
        fileMask = new long[8];
        adjacentFileMask = new long[8];

        for (int i = 0; i < 8; i++) {
            fileMask[i] = FileA << i;
            long left = i > 0 ? FileA << (i - 1) : 0;
            long right= i < 7 ? FileA << (i + 1) : 0;
            adjacentFileMask[i] = left | right;
        }

        pawnShieldSquaresWhite = new int[64][];
        pawnShieldSquaresBlack = new int[64][];
        whitePassedPawnMask = new long[64];
        blackPassedPawnMask = new long[64];
        whitePawnSupportMask = new long[64];
        blackPawnSupportMask = new long[64];
        whiteForwardFileMask = new long[64];
        blackForwardFileMask = new long[64];

        for (int squareIndex = 0; squareIndex < 64; squareIndex++) {
            createPawnShieldSquare(squareIndex);

            int file = BBoardHelper.fileIndex(squareIndex);
            int rank = BBoardHelper.rankIndex(squareIndex);
            long adjacentFiles = FileA << Math.max(0, file - 1) | FileA << Math.min(7, file + 1);

            long whiteForwardMask = (rank < 7) ? ~(BBoardHelper.allBitsSet >>> (64 - 8 * (rank + 1))) : BBoardHelper.allBitsSet;
            long blackForwardMask = (rank > 0) ? ((1L << (8 * rank)) - 1) : 0L;

            whitePassedPawnMask[squareIndex] = (FileA << file | adjacentFiles) & whiteForwardMask;
            blackPassedPawnMask[squareIndex] = (FileA << file | adjacentFiles) & blackForwardMask;

            long left = (file > 0) ? (1L << (squareIndex - 1)) : 0L;
            long right = (file < 7) ? (1L << (squareIndex + 1)) : 0L;
            long adjacent = (left | right);
            whitePawnSupportMask[squareIndex] = adjacent | (adjacent >> 8);
            blackPawnSupportMask[squareIndex] = adjacent | (adjacent << 8);

            whiteForwardFileMask[squareIndex] = whiteForwardMask & fileMask[file];
            blackForwardFileMask[squareIndex] = blackForwardMask & fileMask[file];
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

    public static long[] getWhitePassedPawnMask() {
        return whitePassedPawnMask;
    }

    public static long[] getBlackPassedPawnMask() {
        return blackPassedPawnMask;
    }

    public static long[] getWhitePawnSupportMask() {
        return whitePawnSupportMask;
    }

    public static long[] getBlackPawnSupportMask() {
        return blackPawnSupportMask;
    }

    public static long[] getWhiteForwardFileMask() {
        return whiteForwardFileMask;
    }

    public static long[] getBlackForwardFileMask() {
        return blackForwardFileMask;
    }

    public static long[] getFileMask() {
        return fileMask;
    }

    public static long[] getAdjacentFileMask() {
        return adjacentFileMask;
    }
}
