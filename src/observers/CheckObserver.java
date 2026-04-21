package observers;

import game.Board;
import game.Game;
import game.Move;
import game.Player;
import model.Piece;
import model.Position;

public class CheckObserver implements GameObserver {
    private final Game game;
    private boolean check;
    private boolean checkMate;
    private Player checkedPlayer;
    
    public CheckObserver(Game game) {
        this.game = game;
        clearStatus();
    }

    private void clearStatus() {
        check = false;
        checkMate = false;
        checkedPlayer = null;
    }

    public boolean isCheck() {
        return check;
    }

    public boolean isCheckMate() {
        return checkMate;
    }

    public Player getCheckedPlayer() {
        return checkedPlayer;
    }

    @Override
    public void onMoveMade(Move move) {
    }

    @Override
    public void onPieceCaptured(Piece piece) {
    }

    @Override
    public void onPlayerSwitch(Player currentPlayer) {
        if (currentPlayer == null) {
            clearStatus();
            return;
        }

        Board board = game.getBoard();
        Position kingPosition = board.getKingPosition(currentPlayer.getColor());

        if (kingPosition != null && board.isKingAttacked(kingPosition, currentPlayer.getColor())) {
            check = true;
            checkedPlayer = currentPlayer;
            checkMate = game.checkForCheckMate();
        }
        else {
            clearStatus();
        }
    }
}
