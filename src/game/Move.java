package game;

import model.Colors;
import model.Position;
import model.Piece;

public class Move {
    private Colors playerColor;
    private Position from;
    private Position to;
    private Piece capturedPiece;

    public Move(Colors playerColor, Position from, Position to, Piece capturedPiece) {
        this.playerColor = playerColor;
        this.from = from;
        this.to = to;
        this.capturedPiece = capturedPiece;
    }

    // Pentru JsonReaderUtil
    public Move(String playerColor, String fromStr, String toStr) {
        if ("WHITE".equals(playerColor)) {
            this.playerColor = Colors.WHITE;
        }
        else {
            this.playerColor = Colors.BLACK;
        }
        this.from = new Position(fromStr);
        this.to = new Position(toStr);
        this.capturedPiece = null;
    }

    // Getters
    public Colors getPlayerColor() {
        return this.playerColor;
    }

    public Position getFrom() {
        return this.from;
    }

    public Position getTo() {
        return this.to;
    }

    public Piece getCapturedPiece() {
        return this.capturedPiece;
    }

    // Setters
    public void setPlayerColor(Colors playerColor) {
        this.playerColor = playerColor;
    }

    public void setFrom(Position from) {
        this.from = from;
    }

    public void setTo(Position to) {
        this.to = to;
    }

    public void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(playerColor + ": " + from + " -> " + to);
        if (capturedPiece != null) {
            sb.append(" (Captured + " + capturedPiece.type() + ")");
        }
        return sb.toString();
    }
}
