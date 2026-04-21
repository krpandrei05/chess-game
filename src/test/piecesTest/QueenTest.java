package test.piecesTest;

import game.*;
import model.Colors;
import model.Position;
import pieces.Queen;
import test.TestUtils;

import java.util.List;

public class QueenTest {
    public static void main(String[] args) {
        TestUtils.printTestStart("Queen moves");

        Board board = new Board();
        Queen queen = new Queen(Colors.WHITE, new Position('D', 4));
        board.addPiece(queen);

        List<Position> moves = queen.getPossibleMoves(board);

        TestUtils.assertTrue(moves.contains(new Position('D', 5)), "queen vertical");
        TestUtils.assertTrue(moves.contains(new Position('E', 5)), "queen diagonal");
        TestUtils.assertTrue(moves.contains(new Position('A', 4)), "queen horizontal");

        TestUtils.printTestPassed("Queen moves");
    }
}
