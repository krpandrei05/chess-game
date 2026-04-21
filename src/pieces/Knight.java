package pieces;

import game.Board;
import model.Piece;
import model.Position;
import model.Colors;
import strategies.KnightMoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(Colors color, Position position) {
        super(color, position);
        this.setMoveStrategy(new KnightMoveStrategy());
    }

    // Cod vechi -> Strategy Pattern
//    @Override
//    public List<Position> getPossibleMoves(Board board) {
//        List<Position> moves = new ArrayList<>();
//
//        int currentX = (int) this.getPosition().getX();
//        int currentY = this.getPosition().getY();
//
//        // Toate configuratiile de L
//        int[] dx = {-2, -2, -1, -1, 1, 1, 2, 2};
//        int[] dy = {-1, 1, -2, 2, -2, 2, -1, 1};
//
//        int i, dist;
//        char nextX;
//        int nextY;
//        for (i = 0; i < 8; i++) {
//            nextX = (char) (currentX + dx[i]);
//            nextY = currentY + dy[i];
//
//            if (nextX >= 'A' && nextX <= 'H' && nextY >= 1 && nextY <= 8) {
//                Position nextPossiblePosition = new Position(nextX, nextY);
//                Piece nextPossiblePiece = board.getPieceAt(nextPossiblePosition);
//
//                // Poate sari oriunde, cat timp nu cade peste o piesa proprie
//                if (nextPossiblePiece == null || nextPossiblePiece.getColor() != this.getColor()) {
//                    moves.add(nextPossiblePosition);
//                }
//            }
//        }
//        return moves;
//    }

    @Override
    public boolean checkForCheck(Board board, Position kingPosition) {
        return this.getPossibleMoves(board).contains(kingPosition);
    }

    @Override
    public char type() {
        return 'N';
    }
}
