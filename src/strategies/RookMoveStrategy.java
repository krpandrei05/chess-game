package strategies;

import game.Board;
import model.Piece;
import model.Position;

import java.util.ArrayList;
import java.util.List;

public class RookMoveStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();

        Piece currentPiece = board.getPieceAt(from);
        if (currentPiece == null) {
            return moves;
        }

        int currentX = (int) from.getX();
        int currentY = from.getY();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        int dist, nextY;
        char nextX;
        Position nextPossiblePosition;
        Piece nextPossiblePiece;
        for(int[] dir : directions) {
            for (dist = 1; dist < 8; dist++) {
                nextX = (char) (currentX + dist * dir[0]);
                nextY = currentY + dist * dir[1];

                if (nextX < 'A' || nextX > 'H' || nextY < 1 || nextY > 8) {
                    break;
                }

                nextPossiblePosition = new Position(nextX, nextY);
                nextPossiblePiece = board.getPieceAt(nextPossiblePosition);

                // Daca nu se afla o piesa acolo, sau daca nu se afla o piesa de aceeasi culoare
                if (nextPossiblePiece == null) {
                    moves.add(nextPossiblePosition);
                }
                else {
                    if (nextPossiblePiece.getColor() != currentPiece.getColor()) {
                        // O capturam
                        moves.add(nextPossiblePosition);
                    }
                    // Daca am capturat, s-a terminat mutarea
                    // Nu putem sari peste piesle de acceasi culoare
                    break;
                }
            }
        }
        return moves;
    }
}
