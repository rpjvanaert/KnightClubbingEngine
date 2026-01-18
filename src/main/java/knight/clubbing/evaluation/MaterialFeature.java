package knight.clubbing.evaluation;

import knight.clubbing.PieceValues;
import knight.clubbing.core.BBoard;
import knight.clubbing.core.BPiece;

public class MaterialFeature implements EvalFeature {

    @Override
    public int compute(BBoard board) {

        int wPawn = Long.bitCount(board.getBitboard(BPiece.whitePawn));
        int wKnight = Long.bitCount(board.getBitboard(BPiece.whiteKnight));
        int wBishop = Long.bitCount(board.getBitboard(BPiece.whiteBishop));
        int wRook = Long.bitCount(board.getBitboard(BPiece.whiteRook));
        int wQueen = Long.bitCount(board.getBitboard(BPiece.whiteQueen));
        int white = wPawn * PieceValues.EVAL_VALUES[BPiece.pawn] +
                    wKnight * PieceValues.EVAL_VALUES[BPiece.knight] +
                    wBishop * PieceValues.EVAL_VALUES[BPiece.bishop] +
                    wRook * PieceValues.EVAL_VALUES[BPiece.rook] +
                    wQueen * PieceValues.EVAL_VALUES[BPiece.queen];

        int bPawn = Long.bitCount(board.getBitboard(BPiece.blackPawn));
        int bKnight = Long.bitCount(board.getBitboard(BPiece.blackKnight));
        int bBishop = Long.bitCount(board.getBitboard(BPiece.blackBishop));
        int bRook = Long.bitCount(board.getBitboard(BPiece.blackRook));
        int bQueen = Long.bitCount(board.getBitboard(BPiece.blackQueen));
        int black = bPawn * PieceValues.EVAL_VALUES[BPiece.pawn] +
                    bKnight * PieceValues.EVAL_VALUES[BPiece.knight] +
                    bBishop * PieceValues.EVAL_VALUES[BPiece.bishop] +
                    bRook * PieceValues.EVAL_VALUES[BPiece.rook] +
                    bQueen * PieceValues.EVAL_VALUES[BPiece.queen];

        return white - black;
    }

    @Override
    public String name() {
        return "Material";
    }
}
