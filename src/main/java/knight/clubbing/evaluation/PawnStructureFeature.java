package knight.clubbing.evaluation;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BBoardHelper;
import knight.clubbing.core.BPiece;
import knight.clubbing.core.PopLsbResult;

public class PawnStructureFeature implements EvalFeature {

    public static final int[] PASSED_PAWN_BONUS = new int[]{
            0, 10, 10, 20, 35, 60, 100, 150
    };
    public static final int ISOLATED_PAWN_PENALTY = 10;
    public static final int DOUBLED_PAWN_PENALTY = 15;
    public static final int PAWN_CHAIN_BONUS = 5;

    private final PawnHashTable hashTable;

    public PawnStructureFeature() {
        this(PawnHashTable.getInstance());
    }

    public PawnStructureFeature(PawnHashTable hashTable) {
        this.hashTable = hashTable;
    }

    @Override
    public int compute(BBoard board) {
        long pawnKey = computePawnKey(board);

        // Probe hash table
        Integer cachedScore = hashTable.probe(pawnKey);
        if (cachedScore != null) {
            return cachedScore;
        }

        // Cache miss - evaluate and store
        int score = evaluatePawnStructure(board);
        hashTable.store(pawnKey, score);

        return score;
    }

    private long computePawnKey(BBoard board) {
        long whitePawns = board.getBitboard(BPiece.whitePawn);
        long blackPawns = board.getBitboard(BPiece.blackPawn);

        return whitePawns ^ (blackPawns * 31);
    }

    private int evaluatePawnStructure(BBoard board) {
        long whitePawns = board.getBitboard(BPiece.whitePawn);
        long blackPawns = board.getBitboard(BPiece.blackPawn);

        int score = 0;
        score += evaluateDoubledPawns(whitePawns, blackPawns);
        score += evaluateIsolatedPawns(whitePawns, blackPawns);
        score += evaluatePassedPawns(whitePawns, blackPawns);
        score += evaluatePawnChains(whitePawns, blackPawns);

        return score;
    }

    private int evaluateDoubledPawns(long whitePawns, long blackPawns) {
        int score = 0;

        for (int file = 0; file < 8; file++) {
            long fileMask = 0x0101010101010101L << file;
            int whitePawnsOnFile = Long.bitCount(whitePawns & fileMask);
            int blackPawnsOnFile = Long.bitCount(blackPawns & fileMask);

            if (whitePawnsOnFile > 1) score -= (whitePawnsOnFile - 1) * DOUBLED_PAWN_PENALTY;
            if (blackPawnsOnFile > 1) score += (blackPawnsOnFile - 1) * DOUBLED_PAWN_PENALTY;
        }

        return score;
    }

    private int evaluateIsolatedPawns(long whitePawns, long blackPawns) {
        int score = 0;

        for (int file = 0; file < 8; file++) {
            long fileMask = 0x0101010101010101L << file;

            long whitePawnsOnFile = whitePawns & fileMask;
            long blackPawnsOnFile = blackPawns & fileMask;

            if (whitePawnsOnFile != 0) {
                long adjacentFilesMask = 0L;
                if (file > 0) adjacentFilesMask |= (0x0101010101010101L << (file - 1));
                if (file < 7) adjacentFilesMask |= (0x0101010101010101L << (file + 1));

                if ((whitePawns & adjacentFilesMask) == 0) {
                    int isolatedCount = Long.bitCount(whitePawnsOnFile);
                    score -= isolatedCount * ISOLATED_PAWN_PENALTY;
                }
            }

            if (blackPawnsOnFile != 0) {
                long adjacentFilesMask = 0L;
                if (file > 0) adjacentFilesMask |= (0x0101010101010101L << (file - 1));
                if (file < 7) adjacentFilesMask |= (0x0101010101010101L << (file + 1));

                if ((blackPawns & adjacentFilesMask) == 0) {
                    int isolatedCount = Long.bitCount(blackPawnsOnFile);
                    score += isolatedCount * ISOLATED_PAWN_PENALTY;
                }
            }
        }

        return score;
    }

    private int evaluatePassedPawns(long whitePawns, long blackPawns) {
        int score = 0;

        // White passed pawns
        long tempWhitePawns = whitePawns;
        while (tempWhitePawns != 0) {
            PopLsbResult result = PopLsbResult.popLsb(tempWhitePawns);
            int square = result.index;
            int file = BBoardHelper.fileIndex(square);
            int rank = BBoardHelper.rankIndex(square);

            long frontMask = 0L;
            for (int r = rank + 1; r < 8; r++) {
                if (file > 0) frontMask |= (1L << (r * 8 + file - 1));
                frontMask |= (1L << (r * 8 + file));
                if (file < 7) frontMask |= (1L << (r * 8 + file + 1));
            }

            if ((blackPawns & frontMask) == 0) {
                score += PASSED_PAWN_BONUS[rank];
            }

            tempWhitePawns = result.remaining;
        }

        // Black passed pawns
        long tempBlackPawns = blackPawns;
        while (tempBlackPawns != 0) {
            PopLsbResult result = PopLsbResult.popLsb(tempBlackPawns);
            int square = result.index;
            int file = BBoardHelper.fileIndex(square);
            int rank = BBoardHelper.rankIndex(square);

            long frontMask = 0L;
            for (int r = rank - 1; r >= 0; r--) {
                if (file > 0) frontMask |= (1L << (r * 8 + file - 1));
                frontMask |= (1L << (r * 8 + file));
                if (file < 7) frontMask |= (1L << (r * 8 + file + 1));
            }

            if ((whitePawns & frontMask) == 0) {
                score -= PASSED_PAWN_BONUS[7 - rank];
            }

            tempBlackPawns = result.remaining;
        }

        return score;
    }

    private int evaluatePawnChains(long whitePawns, long blackPawns) {
        int score = 0;

        // White pawn chains
        long tempWhitePawns = whitePawns;
        while (tempWhitePawns != 0) {
            PopLsbResult result = PopLsbResult.popLsb(tempWhitePawns);
            if (isProtected(result.index, whitePawns, true)) {
                score += PAWN_CHAIN_BONUS;
            }
            tempWhitePawns =  result.remaining;
        }

        // Black pawn chains
        long tempBlackPawns = blackPawns;
        while (tempBlackPawns != 0) {
            PopLsbResult result = PopLsbResult.popLsb(tempBlackPawns);
            if (isProtected(result.index, blackPawns, false)) {
                score -= PAWN_CHAIN_BONUS;
            }
            tempBlackPawns = result.remaining;
        }

        return score;
    }

    private static boolean isProtected(int square, long allPawns, boolean isWhite) {
        int file = BBoardHelper.fileIndex(square);
        int rank = BBoardHelper.rankIndex(square);

        int protectorRank = rank + (isWhite ? -1 : 1);

        if (protectorRank < 0 || protectorRank > 7)
            return false;

        if (file > 0) {
            int leftDiagonal = (rank + (isWhite ? -1 : 1)) * 8 + (file - 1);
            if ((allPawns & (1L << leftDiagonal)) != 0)
                return true;
        }
        if (file < 7) {
            int rightDiagonal = (rank + (isWhite ? -1 : 1)) * 8 + (file + 1);
            if ((allPawns & (1L << rightDiagonal)) != 0)
                return true;
        }
        return false;
    }

    @Override
    public String name() {
        return "Pawn Structure";
    }

    public String getCacheStats() {
        return hashTable.getStats();
    }
}
