package test.scenarios;

import exceptions.InvalidCommandException;
import game.Game;
import game.Player;
import model.Colors;
import model.Position;
import pieces.King;
import pieces.Queen;
import exceptions.InvalidMoveException;

public class CheckScenarioTest {
    public static void main(String[] args) {
        Player white = new Player("CheckScenario", Colors.WHITE);
        Player black = new Player("computer", Colors.BLACK);

        Game game = new Game(100, white, black);

        game.getBoard().addPiece(new King(Colors.WHITE, new Position('E', 1)));
        game.getBoard().addPiece(new Queen(Colors.WHITE, new Position('D', 1)));
        game.getBoard().addPiece(new King(Colors.BLACK, new Position('E', 8)));

        white.initializeOwnedPieces(game.getBoard());
        black.initializeOwnedPieces(game.getBoard());

        System.out.println("CheckScenarioTest");
        System.out.println("Move: D1-H5");

        try {
            Position from = new Position('D', 1);
            Position to = new Position('H', 5);

            white.makeMove(from, to, game.getBoard());
            game.addMove(white, from, to, null);

            boolean inCheck = game.getBoard().isKingAttacked(
                    game.getBoard().getKingPosition(Colors.BLACK),
                    Colors.BLACK
            );

            if (inCheck) {
                System.out.println("SUCCESS: check detected");
            } else {
                System.out.println("FAIL: check not detected");
            }

        } catch (InvalidMoveException | InvalidCommandException e) {
            System.out.println("FAIL: invalid move");
        }
    }
}
