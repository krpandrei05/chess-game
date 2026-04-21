package strategies;

import game.Board;
import model.Piece;
import model.Position;

import java.util.ArrayList;
import java.util.List;

public class KingMoveStrategy implements MoveStrategy {
    @Override
    public List<Position> getPossibleMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();

        Piece currentPiece = board.getPieceAt(from);
        if (currentPiece == null) {
            return moves;
        }

        int currentX = from.getX();
        int currentY = from.getY();
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1},
                                {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        int nextY;
        char nextX;
        Position nextPossiblePosition;
        Piece nextPossiblePiece;
        for (int[] dir : directions) {
            nextX = (char) (currentX + dir[0]);
            nextY = currentY + dir[1];

            if (nextX >= 'A' && nextX <= 'H' && nextY >= 1 && nextY <= 8) {
                nextPossiblePosition = new Position(nextX, nextY);
                nextPossiblePiece = board.getPieceAt(nextPossiblePosition);

                if (nextPossiblePiece == null || nextPossiblePiece.getColor() != currentPiece.getColor()) {
                    moves.add(nextPossiblePosition);
                }
            }
        }
        return moves;
    }
}
