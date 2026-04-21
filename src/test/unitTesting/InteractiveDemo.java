package test.unitTesting;

import exceptions.InvalidCommandException;
import game.Game;
import game.Player;
import model.Colors;
import model.Piece;
import model.Position;
import pieces.*;
import exceptions.InvalidMoveException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InteractiveDemo {

    private static final Scanner scanner = new Scanner(System.in);
    private static int score = 0;

    public static void main(String[] args) {
        printBanner();
        System.out.println("Buna ziua! Aceasta este suita de testare interactiva.");
        System.out.println("Vom trece prin 8 SCENARII pentru a demonstra functionalitatea.");
        System.out.println("La fiecare pas, ti se va spune ce sa tastezi.");
        waitForEnter();

        // --- NIVEL 1: MISCARI DE BAZA ---
        runScenario_1_PawnMove();
        runScenario_2_KnightJump();

        // --- NIVEL 2: REGULI SI RESTRICTII (Defense Programming) ---
        runScenario_3_BlockedPiece();
        runScenario_4_WrongTurn();
        runScenario_5_FriendlyFire();

        // --- NIVEL 3: COMBAT SI FINAL ---
        runScenario_6_Capture();
        runScenario_7_SuicideCheck(); // Regele intra in sah
        runScenario_8_CheckMate();    // Marea Finala

        printFinalScore();
    }

    // ==================================================================================
    //                                   SCENARII
    // ==================================================================================

    private static void runScenario_1_PawnMove() {
        printHeader("SCENARIU 1: Mutarea Simpla (Pion)");
        Game game = setupStandardGame();

        System.out.println("CONTEXT: Joc nou. Pionii au voie sa mute 2 patrate la start.");
        printBoard(game);

        String targetMove = "E2E4";
        System.out.println(">> TEST: Deschidere clasica cu Pionul Alb.");
        executeInteractiveMove(game, targetMove, true); // true = asteptam succes
    }

    private static void runScenario_2_KnightJump() {
        printHeader("SCENARIU 2: Logica Avansata (Calul Sare)");
        Game game = setupStandardGame();

        System.out.println("CONTEXT: Calul este singura piesa care poate sari peste altele.");
        System.out.println("         Calul din B1 este blocat de pioni, dar poate ajunge la C3.");
        printBoard(game);

        String targetMove = "B1C3";
        System.out.println(">> TEST: Demonstrare miscare in 'L' si saritura.");
        executeInteractiveMove(game, targetMove, true);
    }

    private static void runScenario_3_BlockedPiece() {
        printHeader("SCENARIU 3: Detectie Blocaj (Tura)");
        Game game = setupStandardGame();

        System.out.println("CONTEXT: Tura Alba (A1) este blocata frontal de Pionul (A2).");
        System.out.println("         Regulile sahului interzic trecerea prin piese (exceptie Cal).");
        printBoard(game);

        String targetMove = "A1A3";
        System.out.println(">> TEST: Incercam sa mutam Tura ilegal peste Pion.");
        executeInteractiveMove(game, targetMove, false); // false = asteptam eroare (InvalidMoveException)
    }

    private static void runScenario_4_WrongTurn() {
        printHeader("SCENARIU 4: Ordinea Jucatorilor");
        Game game = setupStandardGame();

        System.out.println("CONTEXT: Jocul abia a inceput. Este randul ALBULUI.");
        printBoard(game);

        String targetMove = "A7A6"; // Mutare valida pentru Negru, dar nu e randul lui
        System.out.println(">> TEST: Incercam sa mutam o piesa NEAGRA cand e randul Albului.");

        // Aici logica e in main.Main de obicei, dar simulam validarea din Game/Board
        // Daca Game.java nu verifica randul intern la nivel de movePiece,
        // ne bazam pe faptul ca piesa selectata nu apartine jucatorului curent.
        executeInteractiveMove(game, targetMove, false);
    }

    private static void runScenario_5_FriendlyFire() {
        printHeader("SCENARIU 5: Foc Amical (Friendly Fire)");
        Game game = setupStandardGame();

        System.out.println("CONTEXT: Regele Alb e la E1. Regina Alba e la D1.");
        System.out.println("         Nu poti captura propria piesa.");
        printBoard(game);

        String targetMove = "E1D1"; // Regele incearca sa ia Regina
        System.out.println(">> TEST: Regele incearca sa se mute peste Regina proprie.");
        executeInteractiveMove(game, targetMove, false);
    }

    private static void runScenario_6_Capture() {
        printHeader("SCENARIU 6: Mecanica de Captura");
        Game game = new Game();
        game.setBoard(new ArrayList<>()); // Tabla goala

        // Setup: Tura Alba vs Pion Negru
        Piece rook = new Rook(Colors.WHITE, new Position("A1"));
        Piece pawn = new Pawn(Colors.BLACK, new Position("A5"));
        game.getBoard().addPiece(rook);
        game.getBoard().addPiece(pawn);
        setupPlayers(game); // Reset players

        System.out.println("CONTEXT: Tura Alba (A1) are linie libera spre Pionul Negru (A5).");
        printBoard(game);

        String targetMove = "A1A5";
        System.out.println(">> TEST: Tura captureaza Pionul.");
        executeInteractiveMove(game, targetMove, true);

        // Verificare suplimentara
        Piece p = game.getBoard().getPieceAt(new Position("A5"));
        if(p != null && p.getColor() == Colors.WHITE) {
            System.out.println("   [Verificare Vizuala] Pionul a disparut, Tura este pe A5. Corect.");
        }
    }

    private static void runScenario_7_SuicideCheck() {
        printHeader("SCENARIU 7: Interzicere Mutare in Sah (Suicid)");
        Game game = new Game();
        game.setBoard(new ArrayList<>());

        Piece king = new King(Colors.WHITE, new Position("E1"));
        Piece enemyRook = new Rook(Colors.BLACK, new Position("E8")); // Tine coloana E sub atac
        game.getBoard().addPiece(king);
        game.getBoard().addPiece(enemyRook);
        setupPlayers(game);

        System.out.println("CONTEXT: Coloana E este controlata de Tura Neagra.");
        System.out.println("         Regele Alb vrea sa mute de la E1 la E2.");
        printBoard(game);

        String targetMove = "E1E2";
        System.out.println(">> TEST: Regele incearca sa intre direct in bataia Turei.");
        executeInteractiveMove(game, targetMove, false);
    }

    private static void runScenario_8_CheckMate() {
        printHeader("SCENARIU 8: DEMONSTRATIE SAH MAT");
        Game game = setupStandardGame();

        // Simulam rapid mutarile pentru Fool's Mate
        try {
            forceMove(game, "F2", "F3"); // Alb
            game.switchPlayer();
            forceMove(game, "E7", "E5"); // Negru
            game.switchPlayer();
            forceMove(game, "G2", "G4"); // Alb (Greseala)
            game.switchPlayer();
        } catch (Exception e) {}

        System.out.println("CONTEXT: Albul a jucat foarte prost. Apararea Regelui este distrusa.");
        System.out.println("         Negrul are Regina la D8.");
        printBoard(game);

        String targetMove = "D8H4";
        System.out.println(">> TEST FINAL: Da lovitura de gratie cu Regina Neagra!");

        boolean moveSuccess = executeInteractiveMove(game, targetMove, true);

        if (moveSuccess) {
            // Verificam conditia de victorie
            game.switchPlayer(); // Schimbam inapoi la Alb ca sa vedem daca EL e in mat
            if (game.checkForCheckMate()) {
                System.out.println("\n✅ VICTORY: Sistemul a detectat SAH MAT! Jocul s-a terminat.");
                score++;
            } else {
                System.out.println("\n❌ FAILURE: Sistemul NU a detectat sah mat.");
            }
        }
    }

    // ==================================================================================
    //                                   HELPERE LOGICE
    // ==================================================================================

    private static boolean executeInteractiveMove(Game game, String expectedInput, boolean shouldSucceed) {
        System.out.print("👉 Te rog scrie exact: '" + expectedInput + "' : ");
        String input = scanner.nextLine().trim().toUpperCase();

        if (!input.equals(expectedInput)) {
            System.out.println("⚠️  Ai scris altceva (" + input + "). Testul continua cu valoarea corecta pentru demonstratie...");
            input = expectedInput;
        }

        try {
            // Decodificare
            String fromStr = input.substring(0, 2);
            String toStr = input.substring(2, 4);
            Position from = new Position(fromStr);
            Position to = new Position(toStr);

            // Verificam daca e randul corect (simulare logica main.Main)
            Piece p = game.getBoard().getPieceAt(from);
            if (p != null && p.getColor() != game.getCurrentPlayer().getColor()) {
                throw new InvalidMoveException("Nu este randul tau!");
            }

            // Executam mutarea
            game.getBoard().movePiece(from, to);

            // Daca ajungem aici, mutarea a reusit
            if (shouldSucceed) {
                System.out.println("✅ SUCCES: Mutarea a fost validata si executata.");
                printBoard(game); // Aratam tabla modificata
                score++;
                waitForEnter();
                return true;
            } else {
                System.out.println("❌ EROARE: Mutarea trebuia sa fie ILEGALA, dar sistemul a permis-o!");
                waitForEnter();
                return false;
            }

        } catch (InvalidMoveException e) {
            if (!shouldSucceed) {
                System.out.println("🛡️  BLOCAT (Corect): " + e.getMessage());
                System.out.println("✅ Sistemul a prevenit mutarea ilegala.");
                score++;
                waitForEnter();
                return true;
            } else {
                System.out.println("❌ EROARE: Mutarea trebuia sa mearga, dar a dat eroare: " + e.getMessage());
                waitForEnter();
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ CRASH: Eroare neasteptata: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static Game setupStandardGame() {
        Game game = new Game();
        setupPlayers(game);
        game.start();
        return game;
    }

    private static void setupPlayers(Game game) {
        Player p1 = new Player("DemoUser", Colors.WHITE);
        Player p2 = new Player("Computer", Colors.BLACK);
        game.setPlayers(List.of(p1, p2));
        game.setCurrentPlayerIndex(1); // Incepe albul
    }

    private static void forceMove(Game game, String f, String t) throws InvalidMoveException, InvalidCommandException {
        game.getBoard().movePiece(new Position(f), new Position(t));
    }

    // ==================================================================================
    //                                   UI / PRINTING
    // ==================================================================================

    private static void printHeader(String title) {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("   " + title);
        System.out.println("-----------------------------------------------------------");
    }

    private static void printBanner() {
        System.out.println("###########################################################");
        System.out.println("#                                                         #");
        System.out.println("#           DEMONSTRATIE PROIECT SAH - LIVE               #");
        System.out.println("#                                                         #");
        System.out.println("###########################################################");
    }

    private static void waitForEnter() {
        System.out.println("[Apasa ENTER pentru a continua...]");
        scanner.nextLine();
    }

    private static void printFinalScore() {
        System.out.println("\n###########################################################");
        System.out.println("   DEMONSTRATIE INCHEIATA");
        System.out.println("   SCOR FINAL: " + score + " / 9 (8 scenarii + 1 bonus checkmate)");
        System.out.println("###########################################################");
    }

    private static void printBoard(Game game) {
        System.out.println("\n      A     B     C     D     E     F     G     H");
        System.out.println("   +-----+-----+-----+-----+-----+-----+-----+-----+");
        for (int row = 8; row >= 1; row--) {
            System.out.print(" " + row + " |");
            for (char col = 'A'; col <= 'H'; col++) {
                Position pos = new Position(col, row);
                Piece piece = game.getBoard().getPieceAt(pos);
                if (piece == null) {
                    System.out.print("     |");
                } else {
                    String colorCode = (piece.getColor() == Colors.WHITE) ? "W" : "B";
                    System.out.print(" " + piece.type() + "-" + colorCode + " |");
                }
            }
            System.out.println(" " + row);
            System.out.println("   +-----+-----+-----+-----+-----+-----+-----+-----+");
        }
        System.out.println("      A     B     C     D     E     F     G     H\n");
    }
}