package strategies;

import model.Piece;

public class StandardScoringStrategy implements ScoringStrategy {
    @Override
    public int getCapturePoints (Piece piece) {
        if (piece == null) {
            return 0;
        }

        switch (Character.toUpperCase(piece.type())) {
            case 'Q':
                return 90;
            case 'R':
                return 50;
            case 'B':
                return 30;
            case 'N':
                return 30;
            case 'P':
                return 10;
            default:
                return 0;
        }
    }

    @Override
    public int getEndGamePoints(EndGameCondition condition) {
        switch (condition) {
            case WIN_CHECKMATE:
                return 300;
            case LOSE_CHECKMATE:
                return -300;
            case DRAW:
            case RESIGN_OPPONENT:
                return 150;
            case RESIGN_OWN:
                return -150;
            default:
                return 0;
        }
    }
}
