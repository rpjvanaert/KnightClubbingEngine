package knight.clubbing.ordering;

import knight.clubbing.PieceValues;
import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.core.BPiece;

public class MvvLvaFeature implements OrderFeature {

    @Override
    public int score(BMove move, BBoard board) {
        int victimPiece = board.getPieceBoards()[move.targetSquare()];
        int aggressorPiece = board.getPieceBoards()[move.startSquare()];

        int victimValue = PieceValues.MVVLVA_VALUES[BPiece.getPieceType(victimPiece)];
        int aggressorValue = PieceValues.MVVLVA_VALUES[BPiece.getPieceType(aggressorPiece)];

        return victimValue - aggressorValue;
    }

    @Override
    public String name() {
        return "MVV-LVA Feature";
    }
}
