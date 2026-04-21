package pieces;

import game.Board;
import model.Piece;
import model.Position;
import model.Colors;
import strategies.BishopMoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(Colors color, Position position) {
        super(color, position);
        this.setMoveStrategy(new BishopMoveStrategy());
    }

    // Cod vechi -> Strategy Pattern
//    @Override
//    public List<Position> getPossibleMoves(Board board) {
//        List<Position> moves = new ArrayList<>();
//
//        int currentX = (int) this.getPosition().getX();
//        int currentY = this.getPosition().getY();
//
//        // Pe diagonala: N-E, N-V, S-E, S-V
//        int[] dx = {1, -1, 1, -1};
//        int[] dy = {1, 1, -1, -1};
//
//        int i, dist;
//        char nextX;
//        int nextY;
//
//        for (i = 0; i < 4; i++) {
//            for (dist = 1; dist < 8; dist++) {
//                nextX = (char) (currentX + dx[i] * dist);
//                nextY = currentY + dy[i] * dist;
//
//                if (nextX < 'A' || nextX > 'H' || nextY < 1 || nextY > 8) {
//                    break;
//                }
//
//                Position nextPossiblePosition = new Position(nextX, nextY);
//                Piece nextPossiblePiece = board.getPieceAt(nextPossiblePosition);
//
//                // Loc liber
//                if (nextPossiblePiece == null) {
//                    moves.add(nextPossiblePosition);
//                }
//                else {
//                    if (nextPossiblePiece.getColor() != this.getColor()) {
//                        moves.add(nextPossiblePosition);
//                    }
//                    break;
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
        return 'B';
    }
}
