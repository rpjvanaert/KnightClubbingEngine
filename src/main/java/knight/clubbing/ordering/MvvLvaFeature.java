package knight.clubbing.ordering;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BMove;
import knight.clubbing.core.BPiece;

public class MvvLvaFeature implements OrderFeature {
    private static final int[] PIECE_VALUES = {
        0,      // EMPTY
        100,    // PAWN
        320,    // KNIGHT
        330,    // BISHOP
        500,    // ROOK
        900,    // QUEEN
        0       // KING
    };

    @Override
    public int score(BMove move, BBoard board) {
        int victimPiece = board.getPieceBoards()[move.targetSquare()];
        int aggressorPiece = board.getPieceBoards()[move.startSquare()];

        int victimValue = PIECE_VALUES[BPiece.getPieceType(victimPiece)];
        int aggressorValue = PIECE_VALUES[BPiece.getPieceType(aggressorPiece)];

        return victimValue - aggressorValue;
    }

    @Override
    public String name() {
        return "MVV-LVA Feature";
    }
}
