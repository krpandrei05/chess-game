package strategies;

import game.Board;
import model.Position;

import java.util.List;

public interface MoveStrategy {
    // Strategy Pattern
    List<Position> getPossibleMoves(Board board, Position from);
}
