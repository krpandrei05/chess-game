package strategies;

import model.Piece;
import strategies.EndGameCondition;

public interface ScoringStrategy {
    // Strategy Pattern
    int getCapturePoints(Piece piece);
    int getEndGamePoints(EndGameCondition condition);
}
