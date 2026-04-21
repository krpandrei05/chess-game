package test;

import exceptions.InvalidCommandException;
import game.Board;
import game.Game;
import game.Move;
import game.Player;
import model.Colors;
import model.Position;
import model.Piece;
import pieces.King;
import pieces.Queen;

import exceptions.InvalidMoveException;

import java.util.List;

public class GamePersistenceTest {

    private static void testResumeConsistency() {
        Player white = new Player("user", Colors.WHITE);
        Player black = new Player("computer", Colors.BLACK);

        Game game = new Game(1, white, black);

        Board board = game.getBoard();
        board.addPiece(new King(Colors.WHITE, new Position('E', 1)));
        board.addPiece(new King(Colors.BLACK, new Position('E', 8)));
        board.addPiece(new Queen(Colors.WHITE, new Position('D', 1)));

        game.addMove(new Move(Colors.WHITE, new Position('D', 1), new Position('H', 5), null));

        game.resume();

        int boardCount = countBoardPieces(board);
        int ownedCount = white.getOwnedPieces().size() + black.getOwnedPieces().size();

        TestUtils.assertEquals(boardCount, ownedCount, "no ghost pieces after resume");

        int pointsBefore = white.getPoints();
        int capturesBefore = white.getCapturedPieces().size();

        try {
            white.makeRandomMove(board);
        } catch (InvalidMoveException | InvalidCommandException e) {
            throw new RuntimeException(e.getMessage());
        }

        TestUtils.assertEquals(pointsBefore, white.getPoints(), "points not duplicated");
        TestUtils.assertEquals(capturesBefore, white.getCapturedPieces().size(), "captures not duplicated");
    }

    private static int countBoardPieces(Board board) {
        int count = 0;
        char x;
        int y;

        for (x = 'A'; x <= 'H'; x++) {
            for (y = 1; y <= 8; y++) {
                if (board.getPieceAt(new Position(x, y)) != null) {
                    count++;
                }
            }
        }
        return count;
    }

    public static void main(String[] args) {
        testResumeConsistency();
        System.out.println("GamePersistenceTest PASSED");
    }
}
