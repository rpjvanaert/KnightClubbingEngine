package knight.clubbing.evaluation.tune.extract;

import knight.clubbing.core.BMove;

import java.util.List;

public record PgnInfo(List<BMove> moves , PgnResult result) {
}