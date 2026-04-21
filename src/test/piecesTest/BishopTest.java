package test.piecesTest;

import model.Colors;
import model.Position;
import game.*;
import pieces.Bishop;
import test.TestUtils;

import java.util.List;

public class BishopTest {
    public static void main(String[] args) {
        TestUtils.printTestStart("Bishop diagonal moves");

        Board board = new Board();
        Bishop bishop = new Bishop(Colors.WHITE, new Position('C', 1));
        board.addPiece(bishop);

        List<Position> moves = bishop.getPossibleMoves(board);

        TestUtils.assertTrue(moves.contains(new Position('D', 2)), "bishop to D2");
        TestUtils.assertTrue(moves.contains(new Position('E', 3)), "bishop to E3");
        TestUtils.assertFalse(moves.contains(new Position('C', 2)), "bishop cannot move straight");

        TestUtils.printTestPassed("Bishop diagonal moves");
    }
}
