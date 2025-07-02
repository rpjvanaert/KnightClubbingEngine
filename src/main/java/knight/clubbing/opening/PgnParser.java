package knight.clubbing.opening;

import knight.clubbing.core.BBoard;
import knight.clubbing.core.BBoardHelper;
import knight.clubbing.core.BMove;
import knight.clubbing.core.BPiece;
import knight.clubbing.movegen.MoveGenerator;

import java.util.ArrayList;
import java.util.List;

public class PgnParser {

    public static final String EVENT = "Event";
    public static final String WHITE_ELO = "WhiteElo";
    public static final String BLACK_ELO = "BlackElo";
    public static final String TERMINATION = "Termination";

    public static final int ELO_THRESHOLD = 2000;
    public static final String CLASSICAL = "Classical";
    public static final String ABANDONED = "Abandoned";

    private PgnParser() {}

    public static boolean isWorthy(String pgn) {
        int whiteElo = Integer.parseInt(getPgnData(pgn, WHITE_ELO));
        int blackElo = Integer.parseInt(getPgnData(pgn, BLACK_ELO));

        if (!getPgnData(pgn, EVENT).contains(CLASSICAL))
            return false;

        if (getPgnData(pgn, TERMINATION).contains(ABANDONED))
            return false;

        return whiteElo > ELO_THRESHOLD || blackElo > ELO_THRESHOLD;
    }

    protected static String getPgnData(String pgn, String identifier) {
        for (String line : pgn.split("\n")) {
            if (line.startsWith("[" + identifier)) {
                int start = line.indexOf("\"") + 1;
                int end = line.lastIndexOf("\"");
                return line.substring(start, end);
            }
        }
        return "0";
    }

    public static PgnInfo parse(String pgn, int movesDeep) {
        Result result = Result.parse(getPgnData(pgn, "Result"));

        List<String> notatedMoves = getNotatedMoves(pgn);
        notatedMoves = notatedMoves.subList(0, Math.min(notatedMoves.size(), movesDeep * 2));
        List<BMove> moves = getMovesFromPgn(notatedMoves);

        return new PgnInfo(moves, result);
    }

    protected static List<String> getNotatedMoves(String pgn) {
        List<String> moves = new ArrayList<>();
        // Step 1: Remove PGN headers and comments
        pgn = pgn.replaceAll("(?m)^\\[.*\\]$", "");      // Remove tags
        pgn = pgn.replaceAll("\\{[^}]*\\}", "");         // Remove comments

        // Step 2: Remove result notations (1-0, etc.)
        pgn = pgn.replaceAll("\\b(1-0|0-1|1/2-1/2|\\*)\\b", "");

        // Step 3: Remove move numbers (e.g., "1." and "1...")
        pgn = pgn.replaceAll("\\d+\\.\\.\\.", "");       // Remove black move numbers
        pgn = pgn.replaceAll("\\d+\\.", "");             // Remove white move numbers

        // Step 4: Remove numeric annotation glyphs (like $1, $2)
        pgn = pgn.replaceAll("\\$\\d+", "");

        // Step 5: Clean up extra spaces
        pgn = pgn.replaceAll("\\s+", " ").trim();

        // Step 6: Split and clean individual move tokens
        for (String token : pgn.split(" ")) {
            // Remove suffix annotations like !, ?, !?, ?? etc.
            token = token.replaceAll("[!?]+$", "");

            if (!token.isEmpty())
                moves.add(token);
        }
        return moves;
    }

    protected static List<BMove> getMovesFromPgn(List<String> notatedMoves) {
        ArrayList<BMove> moves = new ArrayList<>();

        BBoard board = new BBoard();

        for (String san : notatedMoves) {
            MoveGenerator moveGenerator = new MoveGenerator(board);
            BMove[] possibleMoves = moveGenerator.generateMoves(false);

            BMove candidateMove = determineMoveFromSan(san, possibleMoves, board);

            if (candidateMove == null)
                throw new IllegalArgumentException("Move not found: " + san);

            moves.add(candidateMove);
            board.makeMove(candidateMove, false);
        }

        return moves;
    }

    private static BMove determineMoveFromSan(String san, BMove[] possibleMoves, BBoard board) {
        for (BMove possibleMove : possibleMoves) {
            String moveSan = determineSan(board.copy(), possibleMove, possibleMoves);
            if (moveSan.equals(san)) {
                return possibleMove;
            }
        }
        return null;
    }

    protected static String determineSan(BBoard board, BMove possibleMove, BMove[] possibleMoves) {
        char piece = Character.toUpperCase(BPiece.getChar(board.getPieceBoards()[possibleMove.startSquare()]));
        String from = BBoardHelper.indexToStringCoord(possibleMove.startSquare());
        String to = BBoardHelper.indexToStringCoord(possibleMove.targetSquare());
        boolean isCapture = board.getPieceBoards()[possibleMove.targetSquare()] != BPiece.none;

        if (piece == 'P' && BBoardHelper.fileIndex(possibleMove.startSquare()) != BBoardHelper.fileIndex(possibleMove.targetSquare()))
            isCapture = true;

        StringBuilder san = new StringBuilder();

        if (piece == 'K') {
            if (possibleMove.startSquare() == BBoardHelper.e1 && possibleMove.targetSquare() == BBoardHelper.g1) san.append("O-O");
            if (possibleMove.startSquare() == BBoardHelper.e1 && possibleMove.targetSquare() == BBoardHelper.c1) san.append("O-O-O");
            if (possibleMove.startSquare() == BBoardHelper.e8 && possibleMove.targetSquare() == BBoardHelper.g8) san.append("O-O");
            if (possibleMove.startSquare() == BBoardHelper.e8 && possibleMove.targetSquare() == BBoardHelper.c8) san.append("O-O-O");
        }



        if (piece != 'P' && san.isEmpty()) {
            san.append(piece);

            if (needsDisambiguation(board, possibleMove, possibleMoves)) {
                san.append(getDisambiguation(board, possibleMove, possibleMoves));
            }
        } else if (isCapture) {
            san.append(from.charAt(0));
        }

        if (isCapture) {
            san.append('x');
        }

        if (!san.toString().contains("O-O"))
            san.append(to);

        if (possibleMove.isPromotion()) {
            san.append('=').append(Character.toUpperCase(BPiece.getChar(possibleMove.promotionPieceType())));
        }

        board.makeMove(possibleMove, true);

        MoveGenerator moveGenerator = new MoveGenerator(board);
        BMove[] nextMoves = moveGenerator.generateMoves(false);
        if (nextMoves.length == 0 && board.isInCheck()) {
            san.append('#');
        } else if (board.isInCheck()) {
            san.append('+');
        }

        return san.toString();
    }

    protected static String getDisambiguation(BBoard board, BMove move, BMove[] possibleMoves) {
        char piece = Character.toUpperCase(BPiece.getChar(board.getPieceBoards()[move.startSquare()]));
        int from = move.startSquare();
        int to = move.targetSquare();

        int fromFile = BBoardHelper.fileIndex(from);
        int fromRank = BBoardHelper.rankIndex(from);

        boolean sameFile = false;
        boolean sameRank = false;

        for (BMove eachMove : possibleMoves) {
            if (eachMove.equals(move)) continue;
            if (eachMove.targetSquare() == to &&
                    Character.toUpperCase(BPiece.getChar(board.getPieceBoards()[eachMove.startSquare()])) == piece) {
                int otherFrom = eachMove.startSquare();
                if (BBoardHelper.fileIndex(otherFrom) == fromFile) sameFile = true;
                if (BBoardHelper.rankIndex(otherFrom) == fromRank) sameRank = true;
            }
        }

        if (!sameFile) {
            return BBoardHelper.indexToStringCoord(from).substring(0, 1);
        } else if (!sameRank) {
            return BBoardHelper.indexToStringCoord(from).substring(1);
        }
        return BBoardHelper.indexToStringCoord(from);
    }

    protected static boolean needsDisambiguation(BBoard board, BMove move, BMove[] possibleMoves) {
        char piece = Character.toUpperCase(BPiece.getChar(board.getPieceBoards()[move.startSquare()]));
        if (piece == 'P') return false;

        for (BMove eachMove : possibleMoves) {
            if (eachMove.equals(move)) continue;

            int otherPiece = board.getPieceBoards()[eachMove.startSquare()];

            if (eachMove.targetSquare() == move.targetSquare()) {
                char otherChar = Character.toUpperCase(BPiece.getChar(otherPiece));
                if (otherChar == piece) return true;
            }
        }
        return false;
    }
}
