package pieces;

import game.Board;
import model.Piece;
import model.Position;
import model.Colors;
import strategies.PawnMoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(Colors color, Position positon) {
        super(color, positon);
        this.setMoveStrategy(new PawnMoveStrategy());
    }

    // Cod vechi -> Strategy Pattern
//    private boolean isValidPosition(Position position) {
//        return position.getX() >= 'A' && position.getX() <= 'H'
//                && position.getY() >= 1 && position.getY() <= 8;
//    }
//
//    @Override
//    public List<Position> getPossibleMoves(Board board) {
//        List<Position> moves = new ArrayList<>();
//
//        int currentX = (int) this.getPosition().getX();
//        int currentY = this.getPosition().getY();
//
//        int direction;
//        // Alb (in sus)
//        if (this.getColor() == Colors.WHITE) {
//            direction = 1;
//        }
//        // Negru (in jos)
//        else {
//            direction = -1;
//        }
//
//        Position oneStep = new Position((char)currentX, currentY + direction);
//        if (isValidPosition(oneStep) && board.getPieceAt(oneStep) == null) {
//            moves.add(oneStep);
//
//            // Varianta cu 2 pasi
//            boolean isStartRow = false;
//            if ((this.getColor() == Colors.WHITE && currentY == 2) || (this.getColor() == Colors.BLACK && currentY == 7)) {
//                isStartRow = true;
//            }
//
//            if (isStartRow) {
//                Position twoSteps = new Position((char)currentX, currentY + (2 * direction));
//                if (isValidPosition(twoSteps) && board.getPieceAt(twoSteps) == null) {
//                    moves.add(twoSteps);
//                }
//            }
//        }
//
//        // Capturez pe diagonala
//        int[] diagOffsets = {-1, 1};
//
//        char captureX;
//        int captureY;
//        for (int diag : diagOffsets) {
//            captureX = (char) (currentX + diag);
//            captureY = currentY + direction;
//            Position capturePosition = new Position(captureX, captureY);
//
//            if (isValidPosition(capturePosition)) {
//                // O noua piesa de capturat
//                Piece capturePiece = board.getPieceAt(capturePosition);
//                if (capturePiece != null && capturePiece.getColor() != this.getColor()) {
//                    moves.add(capturePosition);
//                }
//            }
//        }
//        return moves;
//    }

    // Logic, pionul nu captureaza daca merge inainte
    // Nu pot folosi getPossibleMoves, trebuie sa verific doar pe diagonala
    @Override
    public boolean checkForCheck(Board board, Position kingPosition) {
        int currentX = (int)this.getPosition().getX();
        int currentY = this.getPosition().getY();

        int direction;
        // Alb (in sus)
        if (this.getColor() == Colors.WHITE) {
            direction = 1;
        }
        // Negru (in jos)
        else {
            direction = -1;
        }

        int[] diagOffsets = {-1, 1};

        char captureX;
        int captureY;
        for (int diag : diagOffsets) {
            captureX = (char) (currentX + diag);
            captureY = currentY + direction;

            if (kingPosition.getX() == captureX && kingPosition.getY() == captureY) {
                return true;
            }
        }
        return false;
    }

    @Override
    public char type() {
        return 'P';
    }
}
