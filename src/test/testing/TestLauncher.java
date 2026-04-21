package test.testing;

import controller.GuiController;
import controller.TerminalController;
import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import game.Game;
import game.Move;
import game.Player;
import main.Main;
import model.Piece;
import observers.CheckObserver;
import observers.GuiObserver;
import observers.LoggerObserver;
import observers.ScoreObserver;
import test.testing.ManualScenarioLoader;
import ui.GamePanel;
import ui.MainFrame;
import ui.MenuPanel;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

public class TestLauncher {
    public static void main(String[] args) {
        Main app = Main.getInstance();
        System.out.println("Se citesc datele din fisiere...");
        app.read();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== TEST LAUNCHER ===");
            System.out.println("1. Terminal (flux complet)");
            System.out.println("2. GUI normal (login)");
            System.out.println("3. GUI scenarii predefinite (login: email-ana@example.com, pass-parolaAna)");
            System.out.println("0. Iesire");
            System.out.print("> ");

            String option = scanner.nextLine().trim();

            if ("1".equals(option)) {
                TerminalController terminal = new TerminalController(app);
                terminal.start();
                return;
            } else if ("2".equals(option)) {
                GuiController gui = new GuiController(app);
                gui.start();
                return;
            } else if ("3".equals(option)) {
                if (!ensureUser(app, scanner)) {
                    continue;
                }
                ManualScenarioLoader.Scenario scenario = selectScenario(scanner);
                if (scenario == null) {
                    continue;
                }

                int autoDelay = askAutoDelay(scanner, scenario.getAutoMove() != null);

                launchScenarioGui(app, scenario, autoDelay);
                return;
            } else if ("0".equals(option)) {
                System.out.println("Iesire.");
                return;
            } else {
                System.out.println("Optiune invalida.");
            }
        }
    }

    private static boolean ensureUser(Main app, Scanner scanner) {
        if (app.getCurrentUser() != null) {
            return true;
        }

        while (true) {
            System.out.println("\n=== SELECTARE USER PENTRU TEST ===");
            System.out.println("1. Login existent (email: ana@example.com, pass: parolaAna");
            System.out.println("2. Creeaza cont test");
            System.out.println("0. Inapoi");
            System.out.print("> ");
            String opt = scanner.nextLine().trim();

            if ("1".equals(opt)) {
                System.out.print("Email: ");
                String email = scanner.nextLine().trim();
                System.out.print("Parola: ");
                String pass = scanner.nextLine().trim();

                if (app.login(email, pass) != null) {
                    return true;
                }
                System.out.println("Login esuat. Incearca din nou.");
            } else if ("2".equals(opt)) {
                System.out.print("Email nou: ");
                String email = scanner.nextLine().trim();
                System.out.print("Parola noua: ");
                String pass = scanner.nextLine().trim();

                if (app.newAccount(email, pass) != null) {
                    System.out.println("Cont creat si autentificat.");
                    return true;
                }
                System.out.println("Contul exista deja.");
            } else if ("0".equals(opt)) {
                return false;
            } else {
                System.out.println("Optiune invalida.");
            }
        }
    }

    private static ManualScenarioLoader.Scenario selectScenario(Scanner scanner) {
        System.out.println("\n=== SCENARII PREDEFINITE ===");
        System.out.println(" 1. Draw 1 (repetition)");
        System.out.println(" 2. Draw 2 (repetition)");
        System.out.println(" 3. Draw 3 (repetition)");
        System.out.println(" 4. Stalemate 1");
        System.out.println(" 5. Stalemate 2");
        System.out.println(" 6. Stalemate 3");
        System.out.println(" 7. Win Checkmate 1");
        System.out.println(" 8. Win Checkmate 2");
        System.out.println(" 9. Win Checkmate 3");
        System.out.println("10. Lose Checkmate 1");
        System.out.println("11. Lose Checkmate 2");
        System.out.println("12. Lose Checkmate 3");
        System.out.println(" 0. Inapoi");
        System.out.print("> ");

        String opt = scanner.nextLine().trim();

        switch (opt) {
            case "1": return ManualScenarioLoader.draw1();
            case "2": return ManualScenarioLoader.draw2();
            case "3": return ManualScenarioLoader.draw3();
            case "4": return ManualScenarioLoader.stalemate1();
            case "5": return ManualScenarioLoader.stalemate2();
            case "6": return ManualScenarioLoader.stalemate3();
            case "7": return ManualScenarioLoader.winCheckmate1();
            case "8": return ManualScenarioLoader.winCheckmate2();
            case "9": return ManualScenarioLoader.winCheckmate3();
            case "10": return ManualScenarioLoader.loseCheckmate1();
            case "11": return ManualScenarioLoader.loseCheckmate2();
            case "12": return ManualScenarioLoader.loseCheckmate3();
            case "0":
                return null;
            default:
                System.out.println("Optiune invalida.");
                return null;
        }
    }

    private static int askAutoDelay(Scanner scanner, boolean hasAutoMove) {
        if (!hasAutoMove) {
            return 0;
        }

        System.out.print("Vrei mutare automata dupa 3 secunde? (da/nu): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        if ("da".equals(answer) || "d".equals(answer) || "yes".equals(answer)) {
            return 3000;
        }
        return 0;
    }

    private static void launchScenarioGui(final Main app, final ManualScenarioLoader.Scenario scenario, final int autoDelayMs) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Game game = scenario.getGame();

                app.getGames().put(game.getId(), game);
                if (app.getCurrentUser() != null && !app.getCurrentUser().getActiveGames().contains(game)) {
                    app.getCurrentUser().addGame(game);
                }

                LoggerObserver logger = new LoggerObserver();
                logger.loadFromMoves(game.getMoves());

                ScoreObserver score = new ScoreObserver(game.getPlayer1(), game.getPlayer2());
                CheckObserver check = new CheckObserver(game);

                game.addObserver(logger);
                game.addObserver(score);
                game.addObserver(check);

                MainFrame frame = new MainFrame();
                MenuPanel menu = new MenuPanel(app, frame);

                GamePanel panel = new GamePanel(app, frame, game, menu, check, false);
                GuiObserver guiObserver = new GuiObserver(game, panel, logger);
                game.addObserver(guiObserver);

                panel.loadFromGame(logger);

                frame.setMenuPanel(menu);
                frame.setGamePanel(panel);
                frame.showGame();
                frame.setVisible(true);

                if (autoDelayMs > 0 && scenario.getAutoMove() != null) {
                    Timer t = new Timer(autoDelayMs, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                Move auto = scenario.getAutoMove();
                                Player human = game.getHumanPlayer();
                                if (human == null) {
                                    return;
                                }
                                Piece captured = game.getBoard().getPieceAt(auto.getTo());
                                human.makeMove(auto.getFrom(), auto.getTo(), game.getBoard());
                                game.addMove(human, auto.getFrom(), auto.getTo(), captured);
                                game.switchPlayer();
                                panel.loadFromGame(logger);
                            } catch (InvalidMoveException | InvalidCommandException ex) {
                                JOptionPane.showMessageDialog(panel, "Mutarea automata a esuat: " + ex.getMessage(),"Eroare", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                    t.setRepeats(false);
                    t.start();
                }
            }
        });
    }
}

