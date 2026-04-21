package observers;

import game.Move;
import game.Player;
import model.Piece;

public interface GameObserver {
    // Observer Pattern
    void onMoveMade(Move move);
    void onPieceCaptured(Piece piece);
    void onPlayerSwitch(Player currentPlayer);
}
