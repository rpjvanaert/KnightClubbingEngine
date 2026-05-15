package knight.clubbing.evaluation.tune.extract;

import java.util.regex.Pattern;

public enum PgnResult {
    WHITE_WIN("1-0"),
    BLACK_WIN("0-1"),
    DRAW("1/2-1/2");

    private String notation;

    PgnResult(String notation) {
        this.notation = notation;
    }

    public String notation() {
        return notation;
    }

    public static String notationPattern() {
        StringBuilder resultPattern = new StringBuilder();
        for (PgnResult result : PgnResult.values()) {
            if (resultPattern.length() > 0) resultPattern.append("|");
            resultPattern.append(Pattern.quote(result.notation()));
        }
        return resultPattern.toString();
    }

    public static PgnResult parse(String result) {
        for (PgnResult resultType : PgnResult.values()) {
            if (resultType.notation.equals(result)) {
                return resultType;
            }
        }

        throw new IllegalArgumentException("Invalid result: " + result);
    }
}
