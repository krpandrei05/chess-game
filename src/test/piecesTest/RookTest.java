package test.piecesTest;

import model.Colors;
import model.Position;
import game.*;
import pieces.Rook;
import test.TestUtils;

import java.util.List;

public class RookTest {
    public static void main(String[] args) {
        TestUtils.printTestStart("Rook straight moves");

        Board board = new Board();
        Rook rook = new Rook(Colors.WHITE, new Position('A', 1));
        board.addPiece(rook);

        List<Position> moves = rook.getPossibleMoves(board);

        TestUtils.assertTrue(moves.contains(new Position('A', 2)), "rook up");
        TestUtils.assertTrue(moves.contains(new Position('B', 1)), "rook right");
        TestUtils.assertFalse(moves.contains(new Position('B', 2)), "rook cannot move diagonal");

        TestUtils.printTestPassed("Rook straight moves");
    }
}
