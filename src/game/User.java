package game;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String password;
    private List<Game> games;
    // Puncte toate jocurile
    private int points;
    // Pentru JsonReaderUtil
    private List<Integer> gameIds;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.games = new ArrayList<>();
        this.gameIds = new ArrayList<>();
        this.points = 0;
    }

    public User() {
        this.games = new ArrayList<>();
    }

    public void addGame(Game game) {
        this.games.add(game);
    }

    public void removeGame(Game game) {
        this.games.remove(game);
    }

    public List<Game> getActiveGames() {
        return games;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    // Gettes
    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public List<Integer> getGameIds() {
        return this.gameIds;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGameIds(List<Integer> gameIds) {
        this.gameIds = gameIds;
    }
}
