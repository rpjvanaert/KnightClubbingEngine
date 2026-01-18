package knight.clubbing.ordering;

import knight.clubbing.PieceValues;
import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.core.BPiece;

public class MvvLvaFeature implements OrderFeature {
    private final PieceValues pieceValues;

    public MvvLvaFeature() {
        this(PieceValues.mvvLva());
    }

    public MvvLvaFeature(PieceValues pieceValues) {
        this.pieceValues = pieceValues;
    }

    @Override
    public int score(BMove move, BBoard board) {
        int victimPiece = board.getPieceBoards()[move.targetSquare()];
        int aggressorPiece = board.getPieceBoards()[move.startSquare()];

        int victimValue = pieceValues.value(BPiece.getPieceType(victimPiece));
        int aggressorValue = pieceValues.value(BPiece.getPieceType(aggressorPiece));

        return victimValue - aggressorValue;
    }

    @Override
    public String name() {
        return "MVV-LVA Feature";
    }
}
