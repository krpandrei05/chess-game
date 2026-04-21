package test.piecesTest;

import game.Board;
import model.Position;
import pieces.Pawn;
import model.Colors;
import test.TestUtils;

import java.util.List;

public class PawnTest {
    public static void main(String[] args) {
        TestUtils.printTestStart("Pawn basic moves");

        Board board = new Board();
        Pawn pawn = new Pawn(Colors.WHITE, new Position('A', 2));
        board.addPiece(pawn);

        List<Position> moves = pawn.getPossibleMoves(board);

        TestUtils.assertTrue(moves.contains(new Position('A', 3)), "pawn can move one forward");
        TestUtils.assertTrue(moves.contains(new Position('A', 4)), "pawn can move two forward");

        TestUtils.printTestPassed("Pawn basic moves");
    }
}
