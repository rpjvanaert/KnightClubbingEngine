package knight.clubbing.evaluation;

import knight.clubbing.PieceValues;
import knight.clubbing.core.BBoard;
import knight.clubbing.core.BPiece;

public class MaterialFeature implements EvalFeature {

    private final PieceValues pieceValues;

    public MaterialFeature() {
        this(PieceValues.material());
    }

    public MaterialFeature(PieceValues pieceValues) {
        this.pieceValues = pieceValues;
    }

    @Override
    public int compute(BBoard board) {

        int wPawn = Long.bitCount(board.getBitboard(BPiece.whitePawn));
        int wKnight = Long.bitCount(board.getBitboard(BPiece.whiteKnight));
        int wBishop = Long.bitCount(board.getBitboard(BPiece.whiteBishop));
        int wRook = Long.bitCount(board.getBitboard(BPiece.whiteRook));
        int wQueen = Long.bitCount(board.getBitboard(BPiece.whiteQueen));
        int white = wPawn * pieceValues.value(BPiece.pawn) +
                    wKnight * pieceValues.value(BPiece.knight) +
                    wBishop * pieceValues.value(BPiece.bishop) +
                    wRook * pieceValues.value(BPiece.rook) +
                    wQueen * pieceValues.value(BPiece.queen);

        int bPawn = Long.bitCount(board.getBitboard(BPiece.blackPawn));
        int bKnight = Long.bitCount(board.getBitboard(BPiece.blackKnight));
        int bBishop = Long.bitCount(board.getBitboard(BPiece.blackBishop));
        int bRook = Long.bitCount(board.getBitboard(BPiece.blackRook));
        int bQueen = Long.bitCount(board.getBitboard(BPiece.blackQueen));
        int black = bPawn * pieceValues.value(BPiece.pawn) +
                    bKnight * pieceValues.value(BPiece.knight) +
                    bBishop * pieceValues.value(BPiece.bishop) +
                    bRook * pieceValues.value(BPiece.rook) +
                    bQueen * pieceValues.value(BPiece.queen);

        return white - black;
    }

    @Override
    public String name() {
        return "Material";
    }
}
