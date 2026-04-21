package strategies;

import game.Board;
import model.Colors;
import model.Piece;
import model.Position;

import java.util.ArrayList;
import java.util.List;

public class PawnMoveStrategy implements MoveStrategy {
    private boolean isValidPosition(Position position) {
        return position.getX() >= 'A' && position.getX() <= 'H' && position.getY() >= 1 && position.getY() <= 8;
    }

    @Override
    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();

        Piece currentPiece = board.getPieceAt(from);
        if (currentPiece == null) {
            return moves;
        }

        int currentX = from.getX();
        int currentY = from.getY();

        int direction;
        // Alb (in sus)
        if (currentPiece.getColor() == Colors.WHITE) {
            direction = 1;
        }
        // Negru (in jos)
        else {
            direction = -1;
        }

        Position oneStep = new Position((char)currentX, currentY + direction);
        if (isValidPosition(oneStep) && board.getPieceAt(oneStep) == null) {
            moves.add(oneStep);

            // Varianta cu 2 pasi
            boolean isStartRow = false;
            if ((currentPiece.getColor() == Colors.WHITE && currentY == 2) || (currentPiece.getColor() == Colors.BLACK && currentY == 7)) {
                isStartRow = true;
            }

            if (isStartRow) {
                Position twoSteps = new Position((char)currentX, currentY + (2 * direction));
                if (isValidPosition(twoSteps) && board.getPieceAt(twoSteps) == null) {
                    moves.add(twoSteps);
                }
            }
        }

        // Capturez pe diagonala
        int[] diagOffsets = {-1, 1};

        char captureX;
        int captureY;
        for (int diag : diagOffsets) {
            captureX = (char) (currentX + diag);
            captureY = currentY + direction;
            Position capturePosition = new Position(captureX, captureY);

            if (isValidPosition(capturePosition)) {
                // O noua piesa de capturat
                Piece capturePiece = board.getPieceAt(capturePosition);
                if (capturePiece != null && capturePiece.getColor() != currentPiece.getColor()) {
                    moves.add(capturePosition);
                }
            }
        }
        return moves;
    }
}
