package test.testing;

import exceptions.InvalidCommandException;
import game.Game;
import game.Move;
import game.Player;
import model.Colors;
import model.Position;
import pieces.PieceFactory;

import java.util.ArrayList;
import java.util.List;

public final class ManualScenarioLoader {
    private ManualScenarioLoader() {}

    public static final class Scenario {
        private final Game game;
        private final Move autoMove;

        public Scenario(Game game, Move autoMove) {
            this.game = game;
            this.autoMove = autoMove;
        }

        public Game getGame() {
            return game;
        }

        public Move getAutoMove() {
            return autoMove;
        }
    }

    private static void addPiece(Game game, char type, Colors color, char file, int rank) {
        try {
            game.getBoard().addPiece(PieceFactory.createPiece(type, color, new Position(file, rank)));
        } catch (InvalidCommandException e) {
            throw new RuntimeException("PieceFactory error: " + e.getMessage());
        }
    }

    private static Scenario finalizeScenario(Game game, String currentColor, List<Move> moves, Move autoMove) {
        game.setMoves(moves);
        game.setCurrentPlayerColor(currentColor);

        game.getPlayer1().initializeOwnedPieces(game.getBoard());
        game.getPlayer2().initializeOwnedPieces(game.getBoard());
        game.getPlayer1().recalculatePointsFromMoves(game.getMoves());
        game.getPlayer2().recalculatePointsFromMoves(game.getMoves());

        return new Scenario(game, autoMove);
    }

    // ========= DRAW (Test-Draw.txt) =========
    public static Scenario draw1() {
        Player computer = new Player("computer", Colors.WHITE);
        Player human = new Player("ana@example.com", Colors.BLACK);
        Game game = new Game(201, computer, human);

        addPiece(game, 'K', Colors.WHITE, 'E', 1);
        addPiece(game, 'R', Colors.WHITE, 'A', 2);
        addPiece(game, 'K', Colors.BLACK, 'E', 8);
        addPiece(game, 'R', Colors.BLACK, 'H', 8);

        List<Move> moves = new ArrayList<>();
        moves.add(new Move("WHITE", "A1", "A2"));
        moves.add(new Move("BLACK", "H8", "H7"));
        moves.add(new Move("WHITE", "A1", "A2"));
        moves.add(new Move("BLACK", "H8", "H7"));
        moves.add(new Move("WHITE", "A1", "A2"));

        Move auto = new Move(Colors.BLACK, new Position('H', 8), new Position('H', 7), null);
        return finalizeScenario(game, "BLACK", moves, auto);
    }

    public static Scenario draw2() {
        Player computer = new Player("computer", Colors.WHITE);
        Player human = new Player("ana@example.com", Colors.BLACK);
        Game game = new Game(202, computer, human);

        addPiece(game, 'K', Colors.WHITE, 'E', 1);
        addPiece(game, 'R', Colors.WHITE, 'C', 2);
        addPiece(game, 'K', Colors.BLACK, 'E', 8);
        addPiece(game, 'R', Colors.BLACK, 'F', 8);

        List<Move> moves = new ArrayList<>();
        moves.add(new Move("WHITE", "C1", "C2"));
        moves.add(new Move("BLACK", "F8", "F7"));
        moves.add(new Move("WHITE", "C1", "C2"));
        moves.add(new Move("BLACK", "F8", "F7"));
        moves.add(new Move("WHITE", "C1", "C2"));

        Move auto = new Move(Colors.BLACK, new Position('F', 8), new Position('F', 7), null);
        return finalizeScenario(game, "BLACK", moves, auto);
    }

    public static Scenario draw3() {
        Player computer = new Player("computer", Colors.WHITE);
        Player human = new Player("ana@example.com", Colors.BLACK);
        Game game = new Game(203, computer, human);

        addPiece(game, 'K', Colors.WHITE, 'E', 1);
        addPiece(game, 'R', Colors.WHITE, 'H', 2);
        addPiece(game, 'K', Colors.BLACK, 'E', 8);
        addPiece(game, 'R', Colors.BLACK, 'A', 8);

        List<Move> moves = new ArrayList<>();
        moves.add(new Move("WHITE", "H1", "H2"));
        moves.add(new Move("BLACK", "A8", "A7"));
        moves.add(new Move("WHITE", "H1", "H2"));
        moves.add(new Move("BLACK", "A8", "A7"));
        moves.add(new Move("WHITE", "H1", "H2"));

        Move auto = new Move(Colors.BLACK, new Position('A', 8), new Position('A', 7), null);
        return finalizeScenario(game, "BLACK", moves, auto);
    }

    // ========= STALEMATE (Test-Stealmate.txt) =========
    public static Scenario stalemate1() {
        Player computer = new Player("computer", Colors.WHITE);
        Player human = new Player("ana@example.com", Colors.BLACK);
        Game game = new Game(401, computer, human);

        addPiece(game, 'K', Colors.WHITE, 'A', 1);
        addPiece(game, 'K', Colors.BLACK, 'C', 2);
        addPiece(game, 'R', Colors.BLACK, 'B', 3);

        List<Move> moves = new ArrayList<>();
        Move auto = new Move(Colors.BLACK, new Position('B', 3), new Position('B', 2), null);
        return finalizeScenario(game, "BLACK", moves, auto);
    }

    public static Scenario stalemate2() {
        Player computer = new Player("computer", Colors.WHITE);
        Player human = new Player("user@example.com", Colors.BLACK);
        Game game = new Game(402, computer, human);

        addPiece(game, 'K', Colors.WHITE, 'H', 1);
        addPiece(game, 'K', Colors.BLACK, 'F', 2);
        addPiece(game, 'R', Colors.BLACK, 'G', 3);

        List<Move> moves = new ArrayList<>();
        Move auto = new Move(Colors.BLACK, new Position('G', 3), new Position('G', 2), null);
        return finalizeScenario(game, "BLACK", moves, auto);
    }

    public static Scenario stalemate3() {
        Player computer = new Player("computer", Colors.WHITE);
        Player human = new Player("user@example.com", Colors.BLACK);
        Game game = new Game(403, computer, human);

        addPiece(game, 'K', Colors.WHITE, 'H', 8);
        addPiece(game, 'K', Colors.BLACK, 'F', 7);
        addPiece(game, 'R', Colors.BLACK, 'G', 6);

        List<Move> moves = new ArrayList<>();
        Move auto = new Move(Colors.BLACK, new Position('G', 6), new Position('G', 7), null);
        return finalizeScenario(game, "BLACK", moves, auto);
    }

    // ========= WIN CHECKMATE (Test-WinCheckmate.txt) =========
    public static Scenario winCheckmate1() {
        Player human = new Player("ana@example.com", Colors.WHITE);
        Player computer = new Player("computer", Colors.BLACK);
        Game game = new Game(7, human, computer);

        addPiece(game, 'K', Colors.BLACK, 'G', 8);
        addPiece(game, 'P', Colors.BLACK, 'F', 7);
        addPiece(game, 'P', Colors.BLACK, 'G', 7);
        addPiece(game, 'P', Colors.BLACK, 'H', 7);
        addPiece(game, 'P', Colors.BLACK, 'A', 6);
        addPiece(game, 'B', Colors.BLACK, 'F', 4);
        addPiece(game, 'R', Colors.BLACK, 'B', 2);

        addPiece(game, 'P', Colors.WHITE, 'A', 5);
        addPiece(game, 'P', Colors.WHITE, 'H', 3);
        addPiece(game, 'P', Colors.WHITE, 'F', 2);
        addPiece(game, 'P', Colors.WHITE, 'G', 2);
        addPiece(game, 'R', Colors.WHITE, 'D', 1);
        addPiece(game, 'K', Colors.WHITE, 'G', 1);

        List<Move> moves = new ArrayList<>();
        Move auto = new Move(Colors.WHITE, new Position('D', 1), new Position('D', 8), null);
        return finalizeScenario(game, "WHITE", moves, auto);
    }

    public static Scenario winCheckmate2() {
        Player human = new Player("ana@example.com", Colors.WHITE);
        Player computer = new Player("computer", Colors.BLACK);
        Game game = new Game(8, human, computer);

        addPiece(game, 'R', Colors.BLACK, 'A', 8);
        addPiece(game, 'B', Colors.BLACK, 'C', 8);
        addPiece(game, 'Q', Colors.BLACK, 'D', 8);
        addPiece(game, 'K', Colors.BLACK, 'E', 8);
        addPiece(game, 'B', Colors.BLACK, 'F', 8);
        addPiece(game, 'R', Colors.BLACK, 'H', 8);

        addPiece(game, 'P', Colors.BLACK, 'A', 7);
        addPiece(game, 'P', Colors.BLACK, 'B', 7);
        addPiece(game, 'P', Colors.BLACK, 'C', 7);
        addPiece(game, 'P', Colors.BLACK, 'D', 7);
        addPiece(game, 'P', Colors.BLACK, 'F', 7);
        addPiece(game, 'P', Colors.BLACK, 'G', 7);
        addPiece(game, 'P', Colors.BLACK, 'H', 7);

        addPiece(game, 'N', Colors.BLACK, 'C', 6);
        addPiece(game, 'N', Colors.BLACK, 'F', 6);

        addPiece(game, 'Q', Colors.WHITE, 'D', 5);
        addPiece(game, 'B', Colors.WHITE, 'C', 4);
        addPiece(game, 'P', Colors.WHITE, 'E', 4);

        addPiece(game, 'P', Colors.WHITE, 'A', 2);
        addPiece(game, 'B', Colors.WHITE, 'B', 2);
        addPiece(game, 'P', Colors.WHITE, 'F', 2);
        addPiece(game, 'P', Colors.WHITE, 'G', 2);
        addPiece(game, 'P', Colors.WHITE, 'H', 2);

        addPiece(game, 'R', Colors.WHITE, 'A', 1);
        addPiece(game, 'N', Colors.WHITE, 'B', 1);
        addPiece(game, 'K', Colors.WHITE, 'E', 1);
        addPiece(game, 'N', Colors.WHITE, 'G', 1);
        addPiece(game, 'R', Colors.WHITE, 'H', 1);

        List<Move> moves = new ArrayList<>();
        Move auto = new Move(Colors.WHITE, new Position('D', 5), new Position('E', 5), null);
        return finalizeScenario(game, "WHITE", moves, auto);
    }

    public static Scenario winCheckmate3() {
        Player human = new Player("ana@example.com", Colors.WHITE);
        Player computer = new Player("computer", Colors.BLACK);
        Game game = new Game(10, human, computer);

        addPiece(game, 'R', Colors.BLACK, 'A', 8);
        addPiece(game, 'B', Colors.BLACK, 'C', 8);
        addPiece(game, 'R', Colors.BLACK, 'F', 8);
        addPiece(game, 'K', Colors.BLACK, 'G', 8);

        addPiece(game, 'P', Colors.BLACK, 'A', 7);
        addPiece(game, 'P', Colors.BLACK, 'B', 7);
        addPiece(game, 'P', Colors.BLACK, 'F', 7);
        addPiece(game, 'P', Colors.BLACK, 'G', 7);

        addPiece(game, 'N', Colors.BLACK, 'C', 6);
        addPiece(game, 'B', Colors.BLACK, 'D', 6);
        addPiece(game, 'P', Colors.BLACK, 'E', 6);
        addPiece(game, 'Q', Colors.BLACK, 'F', 6);
        addPiece(game, 'P', Colors.BLACK, 'H', 6);

        addPiece(game, 'P', Colors.BLACK, 'C', 5);
        addPiece(game, 'P', Colors.BLACK, 'D', 5);

        addPiece(game, 'N', Colors.WHITE, 'G', 5);

        addPiece(game, 'P', Colors.WHITE, 'D', 4);
        addPiece(game, 'P', Colors.WHITE, 'H', 4);

        addPiece(game, 'P', Colors.WHITE, 'C', 3);
        addPiece(game, 'P', Colors.WHITE, 'E', 3);

        addPiece(game, 'P', Colors.WHITE, 'A', 2);
        addPiece(game, 'P', Colors.WHITE, 'B', 2);
        addPiece(game, 'Q', Colors.WHITE, 'C', 2);
        addPiece(game, 'P', Colors.WHITE, 'F', 2);
        addPiece(game, 'P', Colors.WHITE, 'G', 2);

        addPiece(game, 'R', Colors.WHITE, 'A', 1);
        addPiece(game, 'N', Colors.WHITE, 'B', 1);
        addPiece(game, 'K', Colors.WHITE, 'E', 1);
        addPiece(game, 'B', Colors.WHITE, 'F', 1);
        addPiece(game, 'R', Colors.WHITE, 'H', 1);

        List<Move> moves = new ArrayList<>();
        Move auto = new Move(Colors.WHITE, new Position('C', 2), new Position('H', 7), null);
        return finalizeScenario(game, "WHITE", moves, auto);
    }

    // ========= LOSE CHECKMATE (Test-LoseCheckmate.txt) =========
    public static Scenario loseCheckmate1() {
        Player computer = new Player("computer", Colors.WHITE);
        Player human = new Player("ana@example.com", Colors.BLACK);
        Game game = new Game(701, computer, human);

        addPiece(game, 'K', Colors.BLACK, 'A', 8);
        addPiece(game, 'K', Colors.WHITE, 'C', 6);
        addPiece(game, 'Q', Colors.WHITE, 'B', 7);

        List<Move> moves = new ArrayList<>();
        return finalizeScenario(game, "BLACK", moves, null);
    }

    public static Scenario loseCheckmate2() {
        Player computer = new Player("computer", Colors.WHITE);
        Player human = new Player("ana@example.com", Colors.BLACK);
        Game game = new Game(702, computer, human);

        addPiece(game, 'K', Colors.BLACK, 'H', 8);
        addPiece(game, 'K', Colors.WHITE, 'F', 7);
        addPiece(game, 'Q', Colors.WHITE, 'G', 7);

        List<Move> moves = new ArrayList<>();
        return finalizeScenario(game, "BLACK", moves, null);
    }

    public static Scenario loseCheckmate3() {
        Player computer = new Player("computer", Colors.WHITE);
        Player human = new Player("ana@example.com", Colors.BLACK);
        Game game = new Game(703, computer, human);

        addPiece(game, 'K', Colors.BLACK, 'A', 1);
        addPiece(game, 'K', Colors.WHITE, 'C', 3);
        addPiece(game, 'Q', Colors.WHITE, 'B', 2);

        List<Move> moves = new ArrayList<>();
        return finalizeScenario(game, "BLACK", moves, null);
    }
}
