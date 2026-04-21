package test.unitTesting;

import exceptions.InvalidCommandException;
import game.Game;
import game.Player;
import model.Colors;
import model.Position;
import model.Piece;
import pieces.*;
import exceptions.InvalidMoveException;

import java.util.List;
import java.util.ArrayList;

public class GameTest {

    // Contoare pentru raportul final
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=== PORNIRE SUITA DE TESTE AUTOMATE ===\n");

        // 1. Testare Initializare
        testBoardInitialization();

        // 2. Testare Logica de Miscare (Pion & Cal)
        testPieceMovementLogic();

        // 3. Testare Mutari Ilegale (Blocaje si reguli)
        testIllegalMoves();

        // 4. Testare Captura
        testCaptureLogic();

        // 5. Testare Sah-Mat (Fool's Mate)
        testCheckMate();

        System.out.println("\n=======================================");
        System.out.println("RAPORT FINAL:");
        System.out.println("Teste Trecute: " + testsPassed);
        System.out.println("Teste Picate:  " + testsFailed);
        System.out.println("=======================================");
    }

    /**
     * SCENARIU 1: Verificam daca piesele sunt puse unde trebuie la inceput.
     */
    private static void testBoardInitialization() {
        System.out.println("Running: testBoardInitialization...");
        Game game = new Game();
        // Setam jucatori dummy ca sa nu crape start()
        Player p1 = new Player("Tester1", Colors.WHITE);
        Player p2 = new Player("Tester2", Colors.BLACK);
        game.setPlayers(List.of(p1, p2));
        game.start();

        Piece whiteKing = game.getBoard().getPieceAt(new Position('E', 1));
        boolean cond1 = (whiteKing instanceof King) && (whiteKing.getColor() == Colors.WHITE);
        assertTest(cond1, "Regele Alb este la E1");

        Piece blackKing = game.getBoard().getPieceAt(new Position('E', 8));
        boolean cond2 = (blackKing instanceof King) && (blackKing.getColor() == Colors.BLACK);
        assertTest(cond2, "Regele Negru este la E8");

        Piece pawn = game.getBoard().getPieceAt(new Position('C', 2));
        boolean cond3 = (pawn instanceof Pawn);
        assertTest(cond3, "Pion Alb la C2");
    }

    /**
     * SCENARIU 2: Verificam logica specifica a pieselor (Pion si Cal).
     */
    private static void testPieceMovementLogic() {
        System.out.println("\nRunning: testPieceMovementLogic...");
        Game game = new Game();

        // --- FIX: Setam jucatorii inainte de start() ---
        Player p1 = new Player("T1", Colors.WHITE);
        Player p2 = new Player("T2", Colors.BLACK);
        game.setPlayers(List.of(p1, p2));
        // ----------------------------------------------

        game.start();

        Piece pawnE2 = game.getBoard().getPieceAt(new Position("E2"));
        List<Position> moves = pawnE2.getPossibleMoves(game.getBoard());

        boolean hasE3 = containsPosition(moves, "E3");
        boolean hasE4 = containsPosition(moves, "E4");
        assertTest(hasE3 && hasE4, "Pionul de start are optiunile corecte");

        Piece knightB1 = game.getBoard().getPieceAt(new Position("B1"));
        List<Position> kMoves = knightB1.getPossibleMoves(game.getBoard());

        boolean hasA3 = containsPosition(kMoves, "A3");
        boolean hasC3 = containsPosition(kMoves, "C3");
        assertTest(hasA3 && hasC3, "Calul poate sari peste piese (L-shape)");
    }

    /**
     * SCENARIU 3: Testam exceptiile de logica (mutari interzise).
     */
    private static void testIllegalMoves() {
        System.out.println("\nRunning: testIllegalMoves...");
        Game game = new Game();

        // --- FIX: Setam jucatorii inainte de start() ---
        Player p1 = new Player("T1", Colors.WHITE);
        Player p2 = new Player("T2", Colors.BLACK);
        game.setPlayers(List.of(p1, p2));
        // ----------------------------------------------

        game.start();

        Piece rookA1 = game.getBoard().getPieceAt(new Position("A1"));
        List<Position> rookMoves = rookA1.getPossibleMoves(game.getBoard());
        boolean canJumpPawn = containsPosition(rookMoves, "A3");
        assertTest(!canJumpPawn, "Tura NU poate sari peste propriul pion");

        boolean validBack = game.getBoard().isValidMove(new Position("A2"), new Position("A1"));
        assertTest(!validBack, "Pionul nu poate merge inapoi sau peste piese proprii");
    }

    /**
     * SCENARIU 4: Testam Captura.
     */
    private static void testCaptureLogic() {
        System.out.println("\nRunning: testCaptureLogic...");
        Game game = new Game();
        // Aici NU apelam start(), ci construim tabla manual, deci nu ne trebuie players neaparat
        // dar e bine sa fie definit board-ul
        game.setBoard(new ArrayList<>());

        Piece whiteRook = new Rook(Colors.WHITE, new Position("A1"));
        Piece blackPawn = new Pawn(Colors.BLACK, new Position("A5"));

        game.getBoard().addPiece(whiteRook);
        game.getBoard().addPiece(blackPawn);

        List<Position> moves = whiteRook.getPossibleMoves(game.getBoard());
        boolean canCapture = containsPosition(moves, "A5");
        assertTest(canCapture, "Tura vede inamicul si il poate captura");

        try {
            game.getBoard().movePiece(new Position("A1"), new Position("A5"));

            Piece pieceAtA5 = game.getBoard().getPieceAt(new Position("A5"));
            assertTest(pieceAtA5.getColor() == Colors.WHITE, "Piesa de la A5 este acum Alba (Tura)");
            assertTest(pieceAtA5 instanceof Rook, "Tipul piesei este Tura");

        } catch (InvalidMoveException e) {
            assertTest(false, "Eroare neasteptata la captura: " + e.getMessage());
        } catch (InvalidCommandException e) {
            assertTest(false, "Eroare neasteptata la captura: " + e.getMessage());
        }
    }

    /**
     * SCENARIU 5: Testam Sah-Mat (Matul Prostului / Fool's Mate).
     */
    private static void testCheckMate() {
        System.out.println("\nRunning: testCheckMate (Fool's Mate)...");
        Game game = new Game();
        Player p1 = new Player("P1", Colors.WHITE);
        Player p2 = new Player("P2", Colors.BLACK);
        game.setPlayers(List.of(p1, p2));
        game.start();

        try {
            // 1. Alb muta F2 -> F3
            game.getBoard().movePiece(new Position("F2"), new Position("F3"));
            game.switchPlayer();

            // 2. Negru muta E7 -> E5
            game.getBoard().movePiece(new Position("E7"), new Position("E5"));
            game.switchPlayer();

            // 3. Alb muta G2 -> G4
            game.getBoard().movePiece(new Position("G2"), new Position("G4"));
            game.switchPlayer();

            // 4. Negru muta Regina D8 -> H4 (SAH MAT!)
            game.getBoard().movePiece(new Position("D8"), new Position("H4"));

            // Verificam matul
            game.switchPlayer();
            boolean isCheckMate = game.checkForCheckMate();
            assertTest(isCheckMate, "Detectie Sah-Mat (Fool's Mate) functioneaza");

        } catch (InvalidMoveException | InvalidCommandException e) {
            assertTest(false, "Eroare neasteptata in timpul jocului de test: " + e.getMessage());
        }
    }

    // --- Helper Methods ---

    private static void assertTest(boolean condition, String message) {
        if (condition) {
            System.out.println("[PASS] " + message);
            testsPassed++;
        } else {
            System.out.println("[FAIL] " + message);
            testsFailed++;
        }
    }

    private static boolean containsPosition(List<Position> positions, String posStr) {
        if (positions == null) return false;
        for (Position p : positions) {
            if (p.toString().equals(posStr)) {
                return true;
            }
        }
        return false;
    }
}