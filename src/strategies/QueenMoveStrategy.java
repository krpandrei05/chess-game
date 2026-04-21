package strategies;

import game.Board;
import model.Piece;
import model.Position;

import java.util.ArrayList;
import java.util.List;

public class QueenMoveStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();

        Piece currentPiece = board.getPieceAt(from);
        if (currentPiece == null) {
            return moves;
        }

        int currentX = from.getX();
        int currentY = from.getY();
        int[][] directions = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}, // Rook
                                {1, 1}, {-1, 1}, {1, -1}, {-1, -1}}; // Bishop

        int dist, nextY;
        char nextX;
        Position nextPossiblePosition;
        Piece nextPossiblePiece;
        for (int[] dir : directions) {
            for (dist = 1; dist < 8; dist++) {
                nextX = (char) (currentX + dist * dir[0]);
                nextY = currentY + dist * dir[1];

                if (nextX < 'A' || nextX > 'H' || nextY < 1 || nextY > 8) {
                    break;
                }

                nextPossiblePosition = new Position(nextX, nextY);
                nextPossiblePiece = board.getPieceAt(nextPossiblePosition);

                if (nextPossiblePiece == null) {
                    moves.add(nextPossiblePosition);
                }
                else {
                    if (nextPossiblePiece.getColor() != currentPiece.getColor()) {
                        moves.add(nextPossiblePosition);
                    }
                    // Au aceeasi culoare, nu pot sari
                    break;
                }
            }
        }
        return moves;
    }
}
