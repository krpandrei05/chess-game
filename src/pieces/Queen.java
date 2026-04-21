package pieces;

import game.Board;
import model.Piece;
import model.Position;
import model.Colors;
import strategies.QueenMoveStrategy;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(Colors color, Position position) {
        super(color, position);
        this.setMoveStrategy(new QueenMoveStrategy());
    }

    // Cod vechi -> Strategy Pattern
//    @Override
//    public List<Position> getPossibleMoves(Board board) {
//        List<Position> moves = new ArrayList<>();
//
//        int currentX = (int) this.getPosition().getX();
//        int currentY = this.getPosition().getY();
//
//        // Directiile de la Rock si Bishop (mai pe scurt toate) -> 8 directii
//        int[] dx = {0, 0, 1, -1, 1, 1, -1, -1};
//        int[] dy = {1, -1, 0, 0, 1, -1, 1, -1};
//
//        int i, dist;
//        char nextX;
//        int nextY;
//        for (i = 0; i < 8; i++) {
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
//                if (nextPossiblePiece == null) {
//                    moves.add(nextPossiblePosition);
//                }
//                else {
//                    if (nextPossiblePiece.getColor() != this.getColor()) {
//                        moves.add(nextPossiblePosition);
//                    }
//                    // Au aceeasi culoare, nu pot sari
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
        return 'Q';
    }
}
