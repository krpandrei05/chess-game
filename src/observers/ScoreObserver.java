package observers;

import java.util.List;

import game.Move;
import game.Player;
import model.Colors;
import model.Piece;
import strategies.ScoringStrategy;
import strategies.StandardScoringStrategy;

public class ScoreObserver implements GameObserver {
    private final ScoringStrategy scoringStrategy;
    private final Player whitePlayer;
    private final Player blackPlayer;
    private Colors lastMoveColor;

    public ScoreObserver(Player player1, Player player2) {
        this.scoringStrategy = new StandardScoringStrategy();
        if (player1.getColor() == Colors.WHITE) {
            this.whitePlayer = player1;
            this.blackPlayer = player2;
        }
        else {
            this.whitePlayer = player2;
            this.blackPlayer = player1;
        }
        this.lastMoveColor = null;
    }

    @Override
    public void onMoveMade(Move move) {
        if (move == null) {
            return;
        }
        lastMoveColor = move.getPlayerColor();
    }

    @Override
    public void onPieceCaptured(Piece piece) {
        if (piece == null || lastMoveColor == null) {
            return;
        }
        int points = scoringStrategy.getCapturePoints(piece);
        if (lastMoveColor == Colors.WHITE) {
            whitePlayer.setPoints(whitePlayer.getPoints() + points);
        }
        else {
            blackPlayer.setPoints(blackPlayer.getPoints() + points);
        }
    }

    @Override
    public void onPlayerSwitch(Player currentPlayer) {
    }
}
