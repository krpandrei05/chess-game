package pieces;

import exceptions.InvalidCommandException;
import model.Colors;
import model.Piece;
import model.Position;

// Factory Pattern
public class PieceFactory {
    // Constructor privat pentru a nu instantia clasa inutil
    private PieceFactory(){}

    public static Piece createPiece(char type, Colors color, Position position) throws InvalidCommandException {
        switch (Character.toUpperCase(type)) {
            case 'P':
                return new Pawn(color, position);
            case 'R':
                return new Rook(color, position);
            case 'N':
                return new Knight(color, position);
            case 'B':
                return new Bishop(color, position);
            case 'Q':
                return new Queen(color, position);
            case 'K':
                return new King(color, position);
            default:
                // Exceptia apare cand este o greseala de typo in json sau in cod la initialize()
                throw new InvalidCommandException("Tip piesa invalid: " + type);
        }
    }
}
