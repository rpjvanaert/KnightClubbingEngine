package knight.clubbing.opening;

import java.util.regex.Pattern;

public enum Result {
    WHITE_WIN("1-0"),
    BLACK_WIN("0-1"),
    DRAW("1/2-1/2");

    private String notation;

    Result(String notation) {
        this.notation = notation;
    }

    public String notation() {
        return notation;
    }

    public static String notationPattern() {
        StringBuilder resultPattern = new StringBuilder();
        for (Result result : Result.values()) {
            if (resultPattern.length() > 0) resultPattern.append("|");
            resultPattern.append(Pattern.quote(result.notation()));
        }
        return resultPattern.toString();
    }

    public static Result parse(String result) {
        for (Result resultType : Result.values()) {
            if (resultType.notation.equals(result)) {
                return resultType;
            }
        }

        throw new IllegalArgumentException("Invalid result: " + result);
    }
}
