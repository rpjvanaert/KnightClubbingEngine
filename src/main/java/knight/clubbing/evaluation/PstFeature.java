package knight.clubbing.evaluation;

import com.fasterxml.jackson.databind.ObjectMapper;
import knight.clubbing.core.BBoard;
import knight.clubbing.core.BBoardHelper;
import knight.clubbing.core.BPiece;
import knight.clubbing.core.PopLsbResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class PstFeature implements EvalFeature {

    private static final int[][] PST = initializePst("eval_config/pst.json");

    private static int[][] initializePst(String fileLocation) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ClassLoader loader = PstFeature.class.getClassLoader();
            Map<String, Object> pstConfig = mapper.readValue(loader.getResource(fileLocation), Map.class);

            int[][] pst = new int[7][64];
            pst[BPiece.pawn] = convertToIntArray((ArrayList<?>) pstConfig.get("pawn"));
            pst[BPiece.knight] = convertToIntArray((ArrayList<?>) pstConfig.get("knight"));
            pst[BPiece.bishop] = convertToIntArray((ArrayList<?>) pstConfig.get("bishop"));
            pst[BPiece.rook] = convertToIntArray((ArrayList<?>) pstConfig.get("rook"));
            pst[BPiece.queen] = convertToIntArray((ArrayList<?>) pstConfig.get("queen"));
            pst[BPiece.king] = convertToIntArray((ArrayList<?>) pstConfig.get("king"));

            return pst;
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Failed to load PST configuration", e);
        }
    }

    private static int[] convertToIntArray(ArrayList<?> list) {
        return list.stream().mapToInt(o -> (int) o).toArray();
    }

    @Override
    public int compute(BBoard board) {

        int score = 0;
        long pieceBitboard = board.getAllPiecesBoard();

        while (pieceBitboard != 0) {
            PopLsbResult popResult = PopLsbResult.popLsb(pieceBitboard);
            int squareIndex = popResult.index;
            int piece = board.getPieceBoards()[squareIndex];
            boolean isWhite = BPiece.isWhite(piece);
            squareIndex = isWhite ? BBoardHelper.mirrorSquare(squareIndex) : squareIndex;
            score += isWhite ? PST[BPiece.getPieceType(piece)][squareIndex]: -PST[BPiece.getPieceType(piece)][squareIndex];
            pieceBitboard = popResult.remaining;
        }

        return score;
    }

    @Override
    public String name() {
        return "Piece-Square Tables";
    }
}
