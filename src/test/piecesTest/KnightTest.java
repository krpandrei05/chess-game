package test.piecesTest;

import game.*;
import model.Colors;
import model.Position;
import pieces.Knight;
import test.TestUtils;

import java.util.List;

public class KnightTest {
    public static void main(String[] args) {
        TestUtils.printTestStart("Knight moves");

        Board board = new Board();
        Knight knight = new Knight(Colors.WHITE, new Position('B', 1));
        board.addPiece(knight);

        List<Position> moves = knight.getPossibleMoves(board);

        TestUtils.assertTrue(moves.contains(new Position('A', 3)), "knight to A3");
        TestUtils.assertTrue(moves.contains(new Position('C', 3)), "knight to C3");
        TestUtils.assertFalse(moves.contains(new Position('B', 3)), "knight cannot move straight");

        TestUtils.printTestPassed("Knight moves");
    }
}
