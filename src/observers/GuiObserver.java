package observers;

import javax.swing.SwingUtilities;

import game.Game;
import game.Move;
import game.Player;
import model.Colors;
import model.Piece;
import ui.GamePanel;

public class GuiObserver implements GameObserver {
    private final Game game;
    private final GamePanel panel;
    private Colors lastMoveColor;
    private final LoggerObserver logger;

    public GuiObserver(Game game, GamePanel panel, LoggerObserver logger) {
        this.game = game;
        this.panel = panel;
        this.lastMoveColor = null;
        this.logger = logger;
    }

    @Override
    public void onMoveMade(Move move) {
        if (move == null) {
            return;
        }
        lastMoveColor = move.getPlayerColor();
        
        final String lastEntry = (logger != null) ? logger.getLastEntry() : null;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (lastEntry != null) {
                    panel.appendMoveText(lastEntry);
                }
            }
        });
    }

    @Override
    public void onPieceCaptured(Piece piece) {
        if (piece == null || lastMoveColor == null) {
            return;
        }
        final boolean humanCaptured = lastMoveColor == game.getHumanPlayer().getColor();
        final Player scorer = (lastMoveColor == Colors.WHITE)
            ? (game.getPlayer1().getColor() == Colors.WHITE ? game.getPlayer1() : game.getPlayer2())
            : (game.getPlayer1().getColor() == Colors.BLACK ? game.getPlayer1() : game.getPlayer2());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                panel.addCapturedPiece(piece, humanCaptured);
                panel.setScore(scorer.getPoints());
            }
        });
    }

    @Override
    public void onPlayerSwitch(Player currentPlayer) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                panel.handlePlayerSwitch(currentPlayer);
            }
        });
    }
}
