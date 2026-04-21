package test;

import exceptions.InvalidCommandException;
import game.Board;
import model.Colors;
import model.Position;
import model.Piece;
import pieces.Pawn;
import pieces.Rook;
import pieces.Queen;
import exceptions.InvalidMoveException;

public class BoardTest {
    private static void testValidMove() {
        Board board = new Board();
        Pawn pawn = new Pawn(Colors.WHITE, new Position('A', 2));
        board.addPiece(pawn);

        boolean valid = board.isValidMove(new Position('A', 2), new Position('A', 3));
        TestUtils.assertTrue(valid, "pawn A2 -> A3 valid");
    }

    private static void testInvalidMove() {
        Board board = new Board();
        Pawn pawn = new Pawn(Colors.WHITE, new Position('A', 2));
        board.addPiece(pawn);

        boolean valid = board.isValidMove(new Position('A', 2), new Position('A', 5));
        TestUtils.assertFalse(valid, "pawn A2 -> A5 invalid");
    }

    private static void testCapture() {
        Board board = new Board();
        Pawn whitePawn = new Pawn(Colors.WHITE, new Position('A', 2));
        Pawn blackPawn = new Pawn(Colors.BLACK, new Position('B', 3));
        board.addPiece(whitePawn);
        board.addPiece(blackPawn);

        try {
            board.movePiece(new Position('A', 2), new Position('B', 3), 'Q');
        } catch (InvalidMoveException | InvalidCommandException e) {
            throw new RuntimeException(e.getMessage());
        }

        Piece captured = board.getPieceAt(new Position('B', 3));
        TestUtils.assertNotNull(captured, "piece exists after capture");
        TestUtils.assertEquals(Colors.WHITE, captured.getColor(), "white pawn captured black pawn");
    }

    private static void testPawnPromotion() {
        Board board = new Board();
        Pawn pawn = new Pawn(Colors.WHITE, new Position('A', 7));
        board.addPiece(pawn);

        try {
            board.movePiece(new Position('A', 7), new Position('A', 8), 'Q');
        } catch (InvalidMoveException | InvalidCommandException e) {
            throw new RuntimeException(e.getMessage());
        }

        Piece promoted = board.getPieceAt(new Position('A', 8));
        TestUtils.assertNotNull(promoted, "promotion piece exists");
        TestUtils.assertTrue(promoted instanceof Queen, "pawn promoted to queen");
    }

    public static void main(String[] args) {
        testValidMove();
        testInvalidMove();
        testCapture();
        testPawnPromotion();
        System.out.println("BoardTest PASSED");
    }
}
