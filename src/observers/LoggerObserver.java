package observers;

import java.util.ArrayList;
import java.util.List;

import game.Move;
import game.Player;
import model.Colors;
import model.Piece;

public class LoggerObserver implements GameObserver {
    private final List<String> moveHistory;
    private final List<String> moveHistoryWithCaptures;

    private int moveIndex;

    public LoggerObserver() {
        this.moveHistory = new ArrayList<>();
        this.moveHistoryWithCaptures = new ArrayList<>();
        this.moveIndex = 0;
    }

    public void clear() {
        moveHistory.clear();
        moveHistoryWithCaptures.clear();
        moveIndex = 0;
    }

    public List<String> getMoveHistory() {
        return moveHistory;
    }

    public List<String> getMoveHistoryWithCaptures() {
        return moveHistoryWithCaptures;
    }

    public void loadFromMoves(List<Move> moves) {
        clear();
        if (moves == null) {
            return;
        }        
        for (Move move : moves) {
            onMoveMade(move);
            if (move.getCapturedPiece() != null) {
                onPieceCaptured(move.getCapturedPiece());
            }
        }
    }

    public String getLastEntry() {
        if (moveHistory.isEmpty()) {
            return null;
        }
        return moveHistory.get(moveHistory.size() - 1);
    }

    public String getLastEntryWithCaptures() {
        if (moveHistoryWithCaptures.isEmpty()) {
            return null;
        }
        return moveHistoryWithCaptures.get(moveHistoryWithCaptures.size() - 1);
    }


    @Override
    public void onMoveMade(Move move) {
        if (move == null) {
            return; 
        }
        moveIndex++;

        String idx = String.format("%2d.", moveIndex);
        String circle = (move.getPlayerColor() == Colors.WHITE) ? "\u25CB" : "\u25CF";
        String base = idx + " " + circle + " " + move.getFrom() + "->" + move.getTo();

        moveHistory.add(base);
        moveHistoryWithCaptures.add(base);
    }

    @Override
    public void onPieceCaptured(Piece piece) {
        if (piece == null || moveHistoryWithCaptures.isEmpty()) {
            return;
        }
        int lastIdx = moveHistoryWithCaptures.size() - 1;
        String last = moveHistoryWithCaptures.get(lastIdx);

        moveHistoryWithCaptures.set(lastIdx, last + " (captured " + piece.type() + ")");
    }

    @Override
    public void onPlayerSwitch(Player currentPlayer) {
    }
}
