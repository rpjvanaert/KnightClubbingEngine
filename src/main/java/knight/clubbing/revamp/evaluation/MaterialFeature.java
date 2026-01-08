package knight.clubbing.revamp.evaluation;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BPiece;

public class MaterialFeature implements EvalFeature {

    private final int[] pieceValue;

    public MaterialFeature() {
        pieceValue = new int[7];
        pieceValue[BPiece.none] = 0;
        pieceValue[BPiece.pawn] = 100;
        pieceValue[BPiece.knight] = 310;
        pieceValue[BPiece.bishop] = 330;
        pieceValue[BPiece.rook] = 500;
        pieceValue[BPiece.queen] = 950;
        pieceValue[BPiece.king] = 0;
    }

    @Override
    public int compute(BBoard board) {

        int wPawn = Long.bitCount(board.getBitboard(BPiece.whitePawn));
        int wKnight = Long.bitCount(board.getBitboard(BPiece.whiteKnight));
        int wBishop = Long.bitCount(board.getBitboard(BPiece.whiteBishop));
        int wRook = Long.bitCount(board.getBitboard(BPiece.whiteRook));
        int wQueen = Long.bitCount(board.getBitboard(BPiece.whiteQueen));
        int white = wPawn * pieceValue[BPiece.pawn] +
                    wKnight * pieceValue[BPiece.knight] +
                    wBishop * pieceValue[BPiece.bishop] +
                    wRook * pieceValue[BPiece.rook] +
                    wQueen * pieceValue[BPiece.queen];

        int bPawn = Long.bitCount(board.getBitboard(BPiece.blackPawn));
        int bKnight = Long.bitCount(board.getBitboard(BPiece.blackKnight));
        int bBishop = Long.bitCount(board.getBitboard(BPiece.blackBishop));
        int bRook = Long.bitCount(board.getBitboard(BPiece.blackRook));
        int bQueen = Long.bitCount(board.getBitboard(BPiece.blackQueen));
        int black = bPawn * pieceValue[BPiece.pawn] +
                    bKnight * pieceValue[BPiece.knight] +
                    bBishop * pieceValue[BPiece.bishop] +
                    bRook * pieceValue[BPiece.rook] +
                    bQueen * pieceValue[BPiece.queen];

        return white - black;
    }

    @Override
    public String name() {
        return "Material";
    }
}
