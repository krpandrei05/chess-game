package test.piecesTest;

import game.*;
import model.Colors;
import model.Position;
import pieces.King;
import test.TestUtils;

import java.util.List;

public class KingTest {
    public static void main(String[] args) {
        TestUtils.printTestStart("King moves");

        Board board = new Board();
        King king = new King(Colors.WHITE, new Position('E', 4));
        board.addPiece(king);

        List<Position> moves = king.getPossibleMoves(board);

        TestUtils.assertTrue(moves.contains(new Position('E', 5)), "king up");
        TestUtils.assertTrue(moves.contains(new Position('F', 5)), "king diagonal");
        TestUtils.assertFalse(moves.contains(new Position('E', 6)), "king cannot move two");

        TestUtils.printTestPassed("King moves");
    }
}
