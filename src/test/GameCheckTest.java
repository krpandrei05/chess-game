package test;

import exceptions.InvalidCommandException;
import game.Board;
import game.Game;
import game.Player;
import model.Colors;
import model.Position;
import pieces.King;
import pieces.Queen;
import pieces.Rook;
import exceptions.InvalidMoveException;

public class GameCheckTest {
    private static void testSimpleCheck() {
        Board board = new Board();

        Player white = new Player("w", Colors.WHITE);
        Player black = new Player("b", Colors.BLACK);

        King whiteKing = new King(Colors.WHITE, new Position('E', 1));
        Rook blackRook = new Rook(Colors.BLACK, new Position('E', 8));

        board.addPiece(whiteKing);
        board.addPiece(blackRook);

        white.initializeOwnedPieces(board);
        black.initializeOwnedPieces(board);

        boolean inCheck = board.isKingAttacked(new Position('E', 1), Colors.WHITE);
        TestUtils.assertTrue(inCheck, "white king is in check");
    }

    private static void testCheckMate() {
        Board board = new Board();

        Player white = new Player("w", Colors.WHITE);
        Player black = new Player("b", Colors.BLACK);

        King whiteKing = new King(Colors.WHITE, new Position('A', 1));
        Queen blackQueen = new Queen(Colors.BLACK, new Position('B', 2));
        King blackKing = new King(Colors.BLACK, new Position('C', 3));

        board.addPiece(whiteKing);
        board.addPiece(blackQueen);
        board.addPiece(blackKing);

        Game game = new Game(1, white, black);
        game.getBoard().initialize();
        game.setBoard(java.util.List.of(whiteKing, blackQueen, blackKing));

        white.initializeOwnedPieces(board);
        black.initializeOwnedPieces(board);

        boolean mate = game.checkForCheckMate();
        TestUtils.assertTrue(mate, "checkmate detected");
    }

    private static void testIllegalMoveIntoCheck() {
        Board board = new Board();

        Player white = new Player("w", Colors.WHITE);
        Player black = new Player("b", Colors.BLACK);

        King whiteKing = new King(Colors.WHITE, new Position('E', 1));
        Rook whiteRook = new Rook(Colors.WHITE, new Position('A', 1));
        Rook blackRook = new Rook(Colors.BLACK, new Position('E', 8));

        board.addPiece(whiteKing);
        board.addPiece(whiteRook);
        board.addPiece(blackRook);

        white.initializeOwnedPieces(board);
        black.initializeOwnedPieces(board);

        boolean thrown = false;
        try {
            white.makeMove(new Position('A', 1), new Position('A', 2), board);
        } catch (InvalidMoveException | InvalidCommandException e) {
            thrown = true;
        }

        TestUtils.assertTrue(thrown, "cannot move and leave king in check");
    }

    public static void main(String[] args) {
        testSimpleCheck();
        testCheckMate();
        testIllegalMoveIntoCheck();
        System.out.println("GameCheckTest PASSED");
    }
}
