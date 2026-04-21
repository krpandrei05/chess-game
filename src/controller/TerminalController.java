package controller;

import game.Move;
import main.Main;
import exceptions.GameFlowControlException;
import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import game.Game;
import game.Player;
import model.Colors;
import model.Piece;
import model.Position;
import observers.CheckObserver;
import observers.LoggerObserver;
import observers.ScoreObserver;
import pieces.Pawn;
import strategies.EndGameCondition;
import strategies.ScoringStrategy;
import strategies.StandardScoringStrategy;
import utils.UserUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class TerminalController {
    // Observer Pattern
    private final Set<Integer> observedGameIds;
    private final Map<Integer, CheckObserver> checkByGameId = new HashMap<>();
    private final Map<Integer, LoggerObserver> loggerByGameId = new HashMap<>();
    private final Map<Integer, ScoreObserver> scoreByGameId = new HashMap<>();

    private Main mainApp;
    // Pentru reutilizare (private)
    private Scanner scanner;

    public TerminalController(Main mainApp) {
        this.mainApp = mainApp;
        this.scanner = new Scanner(System.in);
        this.observedGameIds = new HashSet<>();
    }

    private void attachObserversIfNeeded(Game game) {
        if (observedGameIds.contains(game.getId())) {
            return;
        }

        LoggerObserver loggerObserver = new LoggerObserver();
        ScoreObserver scoreObserver = new ScoreObserver(game.getPlayer1(), game.getPlayer2());
        CheckObserver checkObserver = new CheckObserver(game);

        loggerByGameId.put(game.getId(), loggerObserver);
        scoreByGameId.put(game.getId(), scoreObserver);
        checkByGameId.put(game.getId(), checkObserver);

        game.addObserver(loggerObserver);
        game.addObserver(scoreObserver);
        game.addObserver(checkObserver);
        
        observedGameIds.add(game.getId());
}


    public void start() {
        boolean appRunning = true;
        while (appRunning) {
            // PASUL 1: AUTENTIFICARE
            while (mainApp.getCurrentUser() == null) {
                if (!handleAuthentication()) {
                    appRunning = false;
                    break;
                }
            }

            if (!appRunning) {
                System.out.println("Inchidere aplicatie...");
                break;
            }

            // PASUL 2: MENIUL PRINCIPAL
            boolean loggedIn = true;
            while (loggedIn) {
                System.out.println("\n=== MENIU PRINCIPAL ===");
                System.out.println("Utilizator curent: " + UserUtils.getName(mainApp.getCurrentUser().getEmail()));
                System.out.println("1. Incepe joc nou (Player vs Computer)");
                System.out.println("2. Vizualizare jocuri in progres");
                // Tema 2
                System.out.println("3. Informatii cont");
                System.out.println("4. Delogare");
                System.out.print("Alege o optiune: ");

                String cmd = scanner.nextLine().trim();
                try {
                    switch (cmd) {
                        case "1":
                            setupNewGame();
                            break;
                        case "2":
                            handleActiveGames();
                            break;
                        // Tema 2
                        case "3":
                            System.out.println("\n=== INFORMATII CONT ===");
                            System.out.println("Email: " + mainApp.getCurrentUser().getEmail());
                            System.out.println("Puncte totale: " + mainApp.getCurrentUser().getPoints());
                            System.out.println("Jocuri active: " + mainApp.getCurrentUser().getActiveGames().size());
                            break;
                        case "4":
                            boolean shouldExitApp = handleLogout();
                            loggedIn = false;
                            if (shouldExitApp) {
                                appRunning = false;
                            }
                            break;
                        default:
                            throw new InvalidCommandException("Optiune invalida -> " + cmd + "! Ai de ales 4 variante (1/2/3/4)!");
                    }
                } catch (InvalidCommandException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }


    // --- METODE PRIVATE (UI LOGIC) ---
    private boolean handleAuthentication() {
        boolean loginSuccessful = false;

        while (!loginSuccessful) {
            System.out.println("\n--- AUTENTIFICARE ---");
            System.out.println("1. Login | 2. Creeaza cont nou | 3. Inchide aplicatia");
            System.out.print("Alege o optiune: ");

            String choice = scanner.nextLine().trim();
            String email, password;
            try {
                switch (choice) {
                    case "1":
                        System.out.print("Email: ");
                        email = scanner.nextLine().trim();
                        System.out.print("Parola: ");
                        password = scanner.nextLine().trim();
                        // Apelam metoda obligatorie din main.Main
                        if (mainApp.login(email, password) != null) {
                            System.out.println("Autentificare reusita!");
                            loginSuccessful = true;
                        } else {
                            throw new InvalidCommandException("Email-ul sau parola sunt gresite! Incearca din nou!");
                        }
                        break;
                    case "2":
                        System.out.print("Email nou: ");
                        email = scanner.nextLine().trim();
                        System.out.print("Parola noua: ");
                        password = scanner.nextLine().trim();
                        // Apelam metoda obligatorie din main.Main
                        if (mainApp.newAccount(email, password) != null) {
                            System.out.println("Contul a fost creat si autentificat cu SUCCES!");
                            loginSuccessful = true;
                        } else {
                            throw new InvalidCommandException("Eroare: Email-ul exista deja!");
                        }
                        break;
                    case "3":
                        System.out.println("La revedere! PA PA!");
                        return false;
                    default:
                        throw new InvalidCommandException("Optiune invalida -> " + choice + "! Ai de ales 3 variante (1/2/3)!");
                }
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
            }
        }
        return true;
    }

    private void setupNewGame() {
        boolean setupComplete = false;
        String alias = "";
        Colors userColor = Colors.GRAY;

        while (!setupComplete) {
            System.out.println("\n--- JOC NOU (Player vs Computer) ---");
            System.out.println("0. Inapoi la meniul principal");
            System.out.print("Introdu alias-ul tau (sau 0): ");
            String input = scanner.nextLine().trim();

            try {
                if ("0".equals(input)) {
                    return;
                }
                if (input.isEmpty()) {
                    throw new InvalidCommandException("Alias-ul nu poate fi gol");
                }
                alias = input;

                boolean colorSet = false;
                while (!colorSet) {
                    System.out.print("Alege culoare (WHITE/BLACK): ");
                    String colorInput = scanner.nextLine().trim().toUpperCase();

                    try {
                        if ("WHITE".equals(colorInput) || "BLACK".equals(colorInput)) {
                            userColor = Colors.valueOf(colorInput);
                            colorSet = true;
                        } else {
                            throw new InvalidCommandException("Culoare invalida! Alege WHITE sau BLACK!");
                        }
                    } catch (InvalidCommandException e) {
                        System.out.println(e.getMessage());
                    }
                }
                setupComplete = true;
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
            }
        }

        Colors computerColor = (userColor == Colors.WHITE) ? Colors.BLACK : Colors.WHITE;

        Player human = new Player(alias, userColor);
        Player computer = new Player("computer", computerColor);

        int id;
        if (mainApp.getGames().isEmpty()) {
            id = 1;
        } else {
            id = Collections.max(mainApp.getGames().keySet()) + 1;
        }
        Game game = new Game(id, human, computer);
        game.start();

        // Actualizam starea in main.Main
        mainApp.getGames().put(id, game);
        mainApp.getCurrentUser().addGame(game);

        System.out.println("Joc nou creat cu ID-ul: " + id + ". Incepem jocul!");

        try {
            playGame(game);
        } catch (GameFlowControlException e) {
            System.out.println("Jocul a fost incheiat de catre utilizator si a fost salvat/finalizat.");
        }
    }

    private void handleActiveGames() {
        boolean stayingInMenu = true;
        while (stayingInMenu) {
            List<Game> activeGames = mainApp.getCurrentUser().getActiveGames();
            if (activeGames == null || activeGames.isEmpty()) {
                System.out.println("Nu ai jocuri in desfasurare.");
                return;
            }

            System.out.println("\n--- JOCURILE IN PROGRES ---");
            for (Game game : activeGames) {
                System.out.println("ID: " + game.getId() + " | VS: Computer | Mutari: " + game.getMoves().size());
            }

            System.out.println("0. Inapoi la meniul principal");
            System.out.print("Introdu ID-ul jocului pentru optiuni (sau 0): ");

            int id;
            try {
                String input = scanner.nextLine().trim();
                if ("0".equals(input)) {
                    return;
                }

                id = Integer.parseInt(input);
                Game selectedGame = mainApp.getGames().get(id);

                if (selectedGame == null || !mainApp.getCurrentUser().getActiveGames().contains(selectedGame)) {
                    throw new InvalidCommandException("ID-ul '" + id + "' este invalid sau jocul nu iti apartine!");
                }

                System.out.println("\nActiuni pentru jocul " + id + ":");
                System.out.println("1. Vizualizare detalii | 2. Continua jocul | 3. Sterge jocul");
                System.out.print("> ");
                String cmd = scanner.nextLine().trim();

                switch (cmd) {
                    case "1":
                        printBoard(selectedGame);
                        System.out.println("Istoric mutari: " + selectedGame.getMoves());
                        break;
                    case "2":
                        playGame(selectedGame);
                        break;
                    case "3":
                        mainApp.getCurrentUser().removeGame(selectedGame);
                        mainApp.getGames().remove(id);
                        cleanupGameObservers(selectedGame);
                        mainApp.write();
                        System.out.println("Jocul cu ID-ul: '" + id + "' a fost sters!");
                        break;
                    default:
                        throw new InvalidCommandException("Optiune invalida -> " + cmd + "! Ai de ales 3 variante (1/2/3)!");
                }
            } catch (NumberFormatException e) {
                System.out.println("EROARE: Te rog introdu un numar.");
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
            } catch (GameFlowControlException e) {
                stayingInMenu = false;
            }
        }
    }

    private boolean handleLogout() {
        mainApp.logout();
        System.out.println("\nAi fost delogat!");

        while (true) {
            System.out.println("--- DELOGARE ---");
            System.out.println("1. Re-autentificare | 2. Inchide aplicatia");
            System.out.print("Alege: ");
            String cmd = scanner.nextLine().trim();

            try {
                if ("1".equals(cmd)) {
                    return false;
                } else if ("2".equals(cmd)) {
                    System.out.println("La revedere! PaPa!");
                    return true;
                } else {
                    throw new InvalidCommandException("Optiune invalida -> " + cmd + "! Ai de ales 2 variante (1/2)!");
                }
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void playGame(Game game) {
        // Observer Pattern
        attachObserversIfNeeded(game);

        Player player1 = game.getPlayer1();
        Player player2 = game.getPlayer2();

        Player human, computer;
        if ("computer".equals(player1.getName())) {
            computer = player1;
            human = player2;
        } else {
            computer = player2;
            human = player1;
        }

        game.resume();

        CheckObserver checkObserver = checkByGameId.get(game.getId());
        if (checkObserver != null) {
            checkObserver.onPlayerSwitch(game.getCurrentPlayer());
            if (checkObserver.isCheck()) {
                System.out.println("Sah! " + game.getCurrentPlayer().getName() + " este in sah.");
                if (checkObserver.isCheckMate() || game.checkForStaleMate()) {
                    handleGameOver(game, null);
                }
            }
        }
        while (true) {
            System.out.println("\n--- JOC ACTIV (ID: " + game.getId() + ") ---");
            printBoard(game);

            try {
                if (game.checkForCheckMate() || game.checkDrawByRepetition() || game.checkForStaleMate()) {
                    handleGameOver(game, null);
                }

                Player currentPlayer = game.getCurrentPlayer();
                System.out.println("Mutarea curenta: " + currentPlayer.getName() + " (" + currentPlayer.getColor() + ")");
                Move executedMove = null;

                if (currentPlayer.equals(human)) {
                    executedMove = handleHumanMove(game);
                } else if (currentPlayer.equals(computer)) {
                    System.out.println("Computer-ul se gandeste...");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    executedMove = computer.makeRandomMove(game.getBoard());
                    if (executedMove == null) {
                        handleGameOver(game, null);
                        continue;
                    }
                    System.out.println("Computer-ul a mutat: " + executedMove.getFrom() + "-" + executedMove.getTo());
                }

                if (executedMove != null) {
                    game.addMove(executedMove);
                    game.switchPlayer();
                    if (checkObserver != null && checkObserver.isCheck()) {
                        System.out.println("Sah! " + game.getCurrentPlayer().getName() + " este in sah.");
                        if (checkObserver.isCheckMate()) {
                            handleGameOver(game, null);
                        }
                    }
                }
            } catch (InvalidCommandException | InvalidMoveException e) {
                System.out.println(e.getMessage());
            } catch (GameFlowControlException e) {
                String message = e.getMessage();
                if ("RESIGN".equals(message)) {
                    try {
                        handleGameOver(game, "RESIGN");
                    } catch (GameFlowControlException ex) {
                        return;
                    }
                } else if ("QUIT".equals(message)) {
                    mainApp.write();
                    return;
                } else if ("GAME_OVER".equals(message)) {
                    return;
                }
            }
        }
    }

    private Move handleHumanMove(Game game) throws InvalidCommandException, GameFlowControlException {
        Player human = game.getHumanPlayer();

        while (true) {
            System.out.print("[" + human.getName() + "] Introdu mutarea (ex: E2E4 sau E2-E4) sau comanda (QUIT/RESIGN): ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("QUIT")) {
                System.out.println("Salvare inainte de iesire...");
                throw new GameFlowControlException("QUIT");
            } else if (input.equals("RESIGN")) {
                System.out.println(human.getName() + " a renuntat! Jocul se termina!");
                throw new GameFlowControlException("RESIGN");
            }

            if (input.matches("^[A-H][1-8]$")) {
                // Logica de vizualizare mutari posibile
                Position from = new Position(input.charAt(0), Character.getNumericValue(input.charAt(1)));
                Piece piece = game.getBoard().getPieceAt(from);
                if (piece == null) throw new InvalidCommandException("Nu exista nicio piesa la pozitia " + from);
                if (piece.getColor() != human.getColor()) throw new InvalidCommandException("Piesa de la " + from + " nu iti apartine");

                List<Position> possibleMoves = piece.getPossibleMoves(game.getBoard());
                List<Position> validMoves = new ArrayList<>();
                for (Position to : possibleMoves) {
                    if (game.getBoard().isValidMove(from, to)) validMoves.add(to);
                }
                System.out.println(validMoves.isEmpty() ? "Piesa selectata nu are mutari valide." : "Mutari posibile pentru " + from + ": " + validMoves);
                continue;
            }

            if (input.matches("^[A-H][1-8]-[A-H][1-8]$") || input.matches("^[A-H][1-8][A-H][1-8]$")) {
                try {
                    String fromStr, toStr;
                    if (input.contains("-")) {
                        fromStr = input.substring(0, 2);
                        toStr = input.substring(3, 5);
                    } else {
                        fromStr = input.substring(0, 2);
                        toStr = input.substring(2, 4);
                    }

                    Position from = new Position(fromStr.charAt(0), Character.getNumericValue(fromStr.charAt(1)));
                    Position to = new Position(toStr.charAt(0), Character.getNumericValue(toStr.charAt(1)));

                    Piece pieceToMove = game.getBoard().getPieceAt(from);
                    if (pieceToMove == null) throw new InvalidMoveException("Nu este nicio piesa la pozitia " + from);

                    char promotionPawnChar = 'Q';
                    if (pieceToMove instanceof Pawn) {
                        Colors playerColor = pieceToMove.getColor();
                        int targetRank = to.getY();
                        if ((playerColor == Colors.WHITE && targetRank == 8) || (playerColor == Colors.BLACK && targetRank == 1)) {
                            promotionPawnChar = getPromotionPieceFromUser();
                        }
                    }

                    Piece capturedPiece = game.getBoard().getPieceAt(to);
                    human.makeMove(from, to, game.getBoard(), promotionPawnChar);
                    return new Move(human.getColor(), from, to, capturedPiece);
                } catch (InvalidMoveException e) {
                    System.out.println("Mutare invalida: " + e.getMessage());
                    System.out.println("Incearca din nou!");
                }
            } else {
                throw new InvalidCommandException("Format gresit! Foloseste formatul 'E2E4', 'E2-E4', 'B2', 'QUIT' sau 'RESIGN'.");
            }
        }
        return null;
    }

    private char getPromotionPieceFromUser() throws InvalidCommandException {
        System.out.println("Pionul va fi promovat! Alegeti piesa! (Q - Regina, R - Tura, B - Nebun, N - Cal)");
        String choice = scanner.nextLine().trim().toUpperCase();
        if (choice.length() == 1 && "QRBN".contains(choice)) {
            return choice.charAt(0);
        } else {
            throw new InvalidCommandException("Optiune invalida! Alege intre variantele: Q, R, B sau N!");
        }
    }

//    private Player getHumanPlayer(Game game) {
//        if ("computer".equals(game.getPlayer1().getName())) return game.getPlayer2();
//        return game.getPlayer1();
//    }

    private void cleanupGameObservers(Game game) {
        if (game == null) {
            return;
        }
        int id = game.getId();
        observedGameIds.remove(id);

        LoggerObserver logger = loggerByGameId.remove(id);
        ScoreObserver score = scoreByGameId.remove(id);
        CheckObserver check = checkByGameId.remove(id);

        if (logger != null) {
            game.removeObserver(logger);
        }
        if (score != null) {
            game.removeObserver(score);
        }
        if (check != null) {
            game.removeObserver(check);
        }
    }

    private void handleGameOver(Game game, String reason) throws GameFlowControlException {
        EndGameCondition condition = game.evaluateEndGameCondition(reason);
        if (condition == null) {
            return;
        }
        String baseMessage = "";

        if (condition == EndGameCondition.RESIGN_OWN) {
            baseMessage = "AI RENUNTAT!";
        }
        else if (condition == EndGameCondition.LOSE_CHECKMATE) {
            baseMessage = "SAH-MAT! Ai pierdut!";
        }
        else if (condition == EndGameCondition.WIN_CHECKMATE) {
            baseMessage = "FELICITARI! Ai castigat prin SAH-MAT!";
        }
        else if (condition == EndGameCondition.RESIGN_OPPONENT) {
            baseMessage = "EGALITATE (Repetitie x3) -> Computer-ul a RENUNTAT! Ai castigat!";
        }
        else if (condition == EndGameCondition.DRAW) {
            baseMessage = "EGALITATE (stalemate)!";
        }

        int pointsInGame = game.getHumanPlayer().getPoints();
        int bonusPenalty = game.getEndGameBonusPenalty();
        int newTotal = mainApp.getCurrentUser().getPoints() + pointsInGame + bonusPenalty;
        mainApp.getCurrentUser().setPoints(newTotal);

        String fullMessage = baseMessage + " (" + (bonusPenalty >= 0 ? "+" : "") + bonusPenalty + " puncte)";
        
        System.out.println("\n==============================");
        System.out.println("        JOC INCHEIAT          ");
        System.out.println("==============================");
        System.out.println(fullMessage);
        System.out.println("Puncte din capturi: " + pointsInGame);
        System.out.println("Bonus/Penalizare:   " + bonusPenalty);
        System.out.println("------------------------------");
        System.out.println("TOTAL PUNCTE CONT:  " + newTotal);
        System.out.println("==============================\n");

        mainApp.getCurrentUser().removeGame(game);
        mainApp.getGames().remove(game.getId());
        cleanupGameObservers(game);
        mainApp.write();
        throw new GameFlowControlException("GAME_OVER");
    }

    private void printBoard(Game game) {
        // Am copiat exact logica ta de afisare
        game.Board board = game.getBoard();
        System.out.println("\n      A     B     C     D     E     F     G     H");
        String separator = "   +-----+-----+-----+-----+-----+-----+-----+-----+";
        System.out.println(separator);

        char x;
        int y;
        for (y = 8; y >= 1; y--) {
            System.out.print(" " + y + " |");
            for (x = 'A'; x <= 'H'; x++) {
                Position position = new Position(x, y);
                Piece piece = board.getPieceAt(position);

                String content = "";
                if (piece != null) {
                    String pieceString = String.valueOf(piece.type());
                    String colorString = (piece.getColor() == Colors.WHITE) ? "W" : "B";
                    content = pieceString + "-" + colorString;
                }
                if (content.isEmpty()) {
                    System.out.print("     |");
                } else {
                    System.out.printf(" %-3s |", content);
                }
            }
            System.out.println(" " + y);
            System.out.println(separator);
        }
        System.out.println("      A     B     C     D     E     F     G     H\n");
    }
}