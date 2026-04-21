package pieces;

import game.Board;
import model.Piece;
import model.Position;
import model.Colors;
import strategies.KingMoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(Colors color, Position position) {
        super(color, position);
        this.setMoveStrategy(new KingMoveStrategy());
    }

    // Cod vechi -> Strategy Pattern
//    @Override
//    public List<Position> getPossibleMoves(Board board) {
//        List<Position> moves = new ArrayList<>();
//        int currentX = (int) this.getPosition().getX();
//        int currentY = this.getPosition().getY();
//
//        // Toate cele 8 directii posibile (Rege), doar o pozitie
//        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
//        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
//
//        char nextX;
//        int nextY;
//        int i;
//        for (i = 0; i < 8; i++) {
//            nextX = (char) (currentX + dx[i]);
//            nextY = currentY + dy[i];
//
//            if (nextX >= 'A' && nextX <= 'H' && nextY >= 1 && nextY <= 8) {
//                Position nextPossiblePosition = new Position(nextX, nextY);
//                Piece nextPossiblePiece = board.getPieceAt(nextPossiblePosition);
//
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
        return 'K';
    }
}
