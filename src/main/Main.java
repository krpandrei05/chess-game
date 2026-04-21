package main;

import controller.GuiController;
import controller.TerminalController;
import game.*;
import utils.JsonReaderUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    // Singleton Pattern
    private static Main instance;

    private List<User> users;
    private Map<Integer, Game> games;
    private User currentUser;

    private static final String PATH_ACCOUNTS = "input/accounts.json";
    private static final String PATH_GAMES = "input/games.json";

    // Constructor Singleton Pattern
    private Main() {
        this.users = new ArrayList<>();
        this.games = new HashMap<>();
        this.currentUser = null;
    }

    public void read() {
        try {
            Path accountsPath = Paths.get(PATH_ACCOUNTS);
            Path gamesPath = Paths.get(PATH_GAMES);

            this.users = JsonReaderUtil.readAccounts(accountsPath);
            this.games = JsonReaderUtil.readGamesAsMap(gamesPath);

            if (this.users != null && this.games != null) {
                for (User user : this.users) {
                    List<Integer> ids = user.getGameIds();
                    if (ids != null) {
                        for (Integer id : ids) {
                            // Caut jocul
                            Game foundGame = this.games.get(id);
                            if (foundGame != null) {
                                user.addGame(foundGame);
                            }
                        }
                    }
                }
            }
            System.out.println("Datele au fost incarcate cu SUCCES!");
        } catch (Exception e) {
            System.out.println("Nu s-au putut citi datele din json-uri!");
            // Fac asta ca sa nu moara aplicatia
            this.users = new ArrayList<>();
            this.games = new HashMap<>();
        }
    }

    public void write() {
        JsonReaderUtil.saveAccounts(this.users, PATH_ACCOUNTS);
        JsonReaderUtil.saveGames(this.games, this.users, PATH_GAMES);
    }


    public User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                this.currentUser = user;
                return user;
            }
        }
        return null;
    }

    public User newAccount(String email, String password) {
        // Daca exista deja contul
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return null;
            }
        }

        User newUser = new User(email, password);
        users.add(newUser);
        this.currentUser = newUser;
        return newUser;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Alege modul:");
            System.out.println("1) Terminal");
            System.out.println("2) Interfata Grafica");
            System.out.println("0) Exit");
            System.out.print("> ");

            String option = scanner.nextLine().trim();

            if ("1".equals(option)) {
                TerminalController terminalController = new TerminalController(this);
                terminalController.start();
                break;
            }
            else if ("2".equals(option)) {
                GuiController guiController = new GuiController(this);
                guiController.start();
                break;
            }
            else if ("0".equals(option)) {
                System.out.println("Aplicatia se inchide...");
                return; // iese din run()
            }
            else {
                System.out.println("Optiune invalida! Te rog introdu 0, 1 sau 2.");
            }
        }
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getUsers() {
        return users;
    }

    public Map<Integer, Game> getGames() {
        return games;
    }

    // Metoda statica -> Singleton Pattern
    public static Main getInstance() {
        if (instance == null) {
            // Lazy initialization
            instance = new Main();
        }
        return instance;
    }

    public static void main(String[] args) {
        // Clasic -> Main app = new Main();

        // Singleton Pattern
        Main app = getInstance();
        System.out.println("Se citesc datele din fisiere...");
        app.read();
        app.run();
    }
}
