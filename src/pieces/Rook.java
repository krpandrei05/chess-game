package pieces;

import game.Board;
import model.Piece;
import model.Position;
import model.Colors;
import strategies.RookMoveStrategy;

public class Rook extends Piece {
    public Rook(Colors color, Position position) {
        super(color, position);
        this.setMoveStrategy(new RookMoveStrategy());
    }

    // Cod vechi -> Strategy Pattern
//    @Override
//    public List<Position> getPossibleMoves(Board board) {
//        List<Position> moves = new ArrayList<>();
//
//        int currentX = (int) this.getPosition().getX();
//        int currentY = this.getPosition().getY();
//
//        // Doar 4 pozitii -> Sus, Jos, Stanga, Dreapta
//        int[] dx = {0, 0, -1, 1};
//        int[] dy = {1, -1, 0, 0};
//
//        int i, dist;
//        char nextX;
//        int nextY;
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
//                // Daca nu se afla o piesa acolo, sau daca nu se afla o piesa de aceeasi culoare
//                if (nextPossiblePiece == null) {
//                    moves.add(nextPossiblePosition);
//                }
//                else {
//                    if (nextPossiblePiece.getColor() != this.getColor()) {
//                        // O capturam
//                        moves.add(nextPossiblePosition);
//                    }
//                    // Daca am capturat, s-a terminat mutarea
//                    // Nu putem sari peste piesle de acceasi culoare
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
        return 'R';
    }
}
