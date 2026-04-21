package model;

import java.util.List;
import game.Board;

public interface ChessPiece {
    List<Position> getPossibleMoves(Board board);
    boolean checkForCheck(Board board, Position kingPosition);
    char type();
}
