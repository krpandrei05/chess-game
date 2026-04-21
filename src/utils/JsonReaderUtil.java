package utils;

import exceptions.InvalidCommandException;
import model.Piece;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import pieces.*;
import model.Colors;
import model.Position;
import game.*;

/**
 * Utility class for reading JSON documents using JSON.simple ("simple-json").
 *
 * ###### IMPORTANT: This is just an example of how to read JSON documents using the library.
 * Your classes might differ slightly, so don’t hesitate to update this class as needed.
 *
 * Expected structures:
 * - accounts.json: an array of objects with fields: email (String), password (String), points (Number), games (array of numbers)
 * - games.json: an array of objects with fields matching the JSON provided:
 *   id (Number), players (array of {email, color}), currentPlayerColor (String),
 *   board (array of {type, color, position}), moves (array of {playerColor, from, to})
 */
public final class JsonReaderUtil {

    private JsonReaderUtil() {
    }

    /**
     * Reads the accounts from the given JSON file path.
     *
     * @param path path to accounts.json
     * @return list of Account objects (empty list if file not found or array empty)
     * @throws IOException    if I/O fails
     * @throws ParseException if JSON is invalid
     */
    public static List<User> readAccounts(Path path) throws IOException, ParseException {
        if (path == null || !Files.exists(path)) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            Object root = parser.parse(reader);
            JSONArray arr = asArray(root);
            List<User> result = new ArrayList<>();

            if (arr == null) {
                return result;
            }

            for (Object item : arr) {
                JSONObject obj = asObject(item);
                if (obj == null) {
                    continue;
                }

                User acc = new User();
                acc.setEmail(asString(obj.get("email")));
                acc.setPassword(asString(obj.get("password")));
                acc.setPoints(asInt(obj.get("points"), 0));

                List<Integer> gameIds = new ArrayList<>();
                JSONArray games = asArray(obj.get("games"));
                if (games != null) {
                    for (Object gid : games) {
                        gameIds.add(asInt(gid, 0));
                    }
                }
                acc.setGameIds(gameIds);
                result.add(acc);
            }
            return result;
        }
    }

    /**
     * Reads the games from the given JSON file path and returns them as a map by id.
     * The structure strictly follows games.json as provided (no title/genre).
     *
     * @param path path to games.json
     * @return map id -> Game (empty if file missing or array empty)
     * @throws IOException    if I/O fails
     * @throws ParseException if JSON is invalid
     */
    public static Map<Integer, Game> readGamesAsMap(Path path) throws IOException, ParseException {
        Map<Integer, Game> map = new HashMap<>();
        if (path == null || !Files.exists(path)) {
            return map;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            Object root = parser.parse(reader);
            JSONArray arr = asArray(root);
            if (arr == null) return map;
            for (Object item : arr) {
                JSONObject obj = asObject(item);
                if (obj == null) {
                    continue;
                }
                int id = asInt(obj.get("id"), -1);
                if (id < 0) {
                    continue;
                }// skip invalid
                Game g = new Game();
                g.setId(id);

                // players array
                JSONArray playersArr = asArray(obj.get("players"));
                if (playersArr != null) {
                    List<Player> players = new ArrayList<>();
                    for (Object pItem : playersArr) {
                        JSONObject pObj = asObject(pItem);
                        if (pObj == null) {
                            continue;
                        }
                        String email = asString(pObj.get("email"));
                        // MODIFICAT
                        String name = email; // computer, computer
                        if (name != null && name.contains("@")) {
                            name = email.split("@")[0];
                        }
                        // MODIFICAT
                        String color = asString(pObj.get("color"));
                        // MODIFICAT
                        Colors playerColor = Colors.GRAY;
                        if ("WHITE".equals(color)) {
                            playerColor = Colors.WHITE;
                        }
                        else if ("BLACK".equals(color)) {
                            playerColor = Colors.BLACK;
                        }
                        else {
                            playerColor = Colors.GRAY;
                        }

                        players.add(new Player(name, playerColor));
                        // MODIFICAT
                    }
                    g.setPlayers(players);
                }

                // currentPlayerColor
                g.setCurrentPlayerColor(asString(obj.get("currentPlayerColor")));

                // board array
                JSONArray boardArr = asArray(obj.get("board"));
                if (boardArr != null) {
                    List<Piece> board = new ArrayList<>();
                    for (Object bItem : boardArr) {
                        JSONObject bObj = asObject(bItem);
                        if (bObj == null) {
                            continue;
                        }

                        String type = asString(bObj.get("type"));
                        String colorStr = asString(bObj.get("color"));
                        String positionStr = asString(bObj.get("position"));

                        Colors color = colorStr.equals("WHITE") ? Colors.WHITE : Colors.BLACK;
                        Position position = new Position(positionStr);

                        Piece piece = null;
                        if (type != null && type.length() > 0) {
                            // Cod vechi
//                            char pieceType = type.charAt(0);
//                            switch (pieceType) {
//                                case 'K':
//                                    piece = new King(color, position);
//                                    break;
//                                case 'Q':
//                                    piece = new Queen(color, position);
//                                    break;
//                                case 'R':
//                                    piece = new Rook(color, position);
//                                    break;
//                                case 'B':
//                                    piece = new Bishop(color, position);
//                                    break;
//                                case 'N':
//                                    piece = new Knight(color, position);
//                                    break;
//                                case 'P':
//                                    piece = new Pawn(color, position);
//                                    break;
//                            }
                            // Factory Pattern
                            try {
                                piece = PieceFactory.createPiece(type.charAt(0), color, position);
                            } catch (InvalidCommandException e) {
                                System.out.println("Piesa ignorata (tip invalid in JSON): " + type);
                            }
                        }

                        if (piece != null) {
                            board.add(piece);
                        }
                    }
                    g.setBoard(board);
                }

                // Parse optional moves array
                JSONArray movesArr = asArray(obj.get("moves"));
                if (movesArr != null) {
                    List<Move> moves = new ArrayList<>();
                    for (Object mItem : movesArr) {
                        JSONObject mObj = asObject(mItem);
                        if (mObj == null) {
                            continue;
                        }
                        // MODIFICAT
                        String playerColorStr = asString(mObj.get("playerColor"));
                        String fromStr = asString(mObj.get("from"));
                        String toStr = asString(mObj.get("to"));

                        Colors playerColor = Colors.valueOf(playerColorStr);
                        Position from = new Position(fromStr);
                        Position to = new Position(toStr);

                        Piece capturedPiece = null;
                        JSONObject capturedObj = asObject(mObj.get("captured"));
                        if (capturedObj != null) {
                            char type = asString(capturedObj.get("type")).charAt(0);
                            Colors color = Colors.valueOf(asString(capturedObj.get("color")));
                            // Cod vechi
//                            capturedPiece = buildPiece(type, color);

                            // Factory Pattern
                            try {
                                // position == null, deoarece e capturata si nu conteaza pozitia in istoric
                                capturedPiece = PieceFactory.createPiece(type, color, null);
                            } catch (InvalidCommandException e) {
                                System.out.println("Piesa capturata ignorata (tip invalid): " + type);
                            }
                        }
                        moves.add(new Move(playerColor, from, to, capturedPiece));
                    }
                    g.setMoves(moves);
                    // MODIFICAT
                }
                map.put(id, g);
            }
        }
        return map;
    }

    public static void saveAccounts(List<User> users, String path) {
        JSONArray usersArr = new JSONArray();

        for (User user : users) {
            Map<String, Object> userObj = new LinkedHashMap<>();
            userObj.put("email", user.getEmail());
            userObj.put("password", user.getPassword());
            userObj.put("points", user.getPoints());

            JSONArray gameIdsArr = new JSONArray();
            if (user.getActiveGames() != null) {
                for (Game game : user.getActiveGames()) {
                    gameIdsArr.add(game.getId());
                }
            }
            userObj.put("games", gameIdsArr);
            usersArr.add(userObj);
        }

        try (FileWriter file = new FileWriter(path)) {
            file.write(formatJSON(usersArr.toJSONString()));
        } catch (IOException e) {
            System.out.println("Eroare la scrierea in accounts.json: " + e.getMessage());
        }
    }

    public static void saveGames(Map<Integer, Game> games, List<User> users, String path) {
        JSONArray gamesArr = new JSONArray();
        List<Game> sortedGames = new ArrayList<>(games.values());

        // Sortam jocurile dupa ID
        Collections.sort(sortedGames, new Comparator<Game>() {
            @Override
            public int compare(Game g1, Game g2) {
                return Integer.compare(g1.getId(), g2.getId());
            }
        });

        for (Game game : sortedGames) {
            Map<String, Object> gameObj = new LinkedHashMap<>();
            gameObj.put("id", game.getId());

            // Players
            JSONArray playersArr = new JSONArray();

            // Player 1
            Map<String, Object> player1Obj = new LinkedHashMap<>();
            String emailPlayer1 = UserUtils.getEmail(game.getPlayer1().getName(), users);
            player1Obj.put("email", emailPlayer1);
            player1Obj.put("color", game.getPlayer1().getColor().toString());
            playersArr.add(player1Obj);

            // Player 2
            Map<String, Object> player2Obj = new LinkedHashMap<>();
            String emailPlayer2 = UserUtils.getEmail(game.getPlayer2().getName(), users);
            player2Obj.put("email", emailPlayer2);
            player2Obj.put("color", game.getPlayer2().getColor().toString());
            playersArr.add(player2Obj);

            gameObj.put("players", playersArr);
            gameObj.put("currentPlayerColor", game.getCurrentPlayer().getColor().toString());

            // Board
            gameObj.put("board", getBoardAsJson(game.getBoard()));

            // Moves
            gameObj.put("moves", getMovesAsJson(game.getMoves()));

            gamesArr.add(gameObj);
        }

        try (FileWriter file = new FileWriter(path)) {
            file.write(formatJSON(gamesArr.toJSONString()));
        } catch (IOException e) {
            System.out.println("Eroare la scrierea in games.json: " + e.getMessage());
        }
    }

    // Metoda privata pentru a extrage JSON-ul tablei (pentru a nu aglomera saveGames)
    private static JSONArray getBoardAsJson(Board board) {
        JSONArray boardArr = new JSONArray();
        JSONArray whitePieces = new JSONArray();
        JSONArray blackPieces = new JSONArray();

        char x;
        int y;
        for (x = 'A'; x <= 'H'; x++) {
            for (y = 1; y <= 8; y++) {
                Position position = new Position(x, y);
                Piece piece = board.getPieceAt(position);

                if (piece != null) {
                    Map<String, Object> pieceObj = new LinkedHashMap<>();
                    pieceObj.put("type", String.valueOf(piece.type()));
                    pieceObj.put("color", piece.getColor().toString());
                    pieceObj.put("position", x + "" + y);

                    if (piece.getColor() == Colors.WHITE) {
                        whitePieces.add(pieceObj);
                    } else {
                        blackPieces.add(pieceObj);
                    }
                }
            }
        }
        boardArr.addAll(whitePieces);
        boardArr.addAll(blackPieces);
        return boardArr;
    }

    // Metoda privata pentru a extrage JSON-ul mutarilor
    private static JSONArray getMovesAsJson(List<Move> moves) {
        JSONArray movesArr = new JSONArray();
        for (Move move : moves) {
            Map<String, Object> moveObj = new LinkedHashMap<>();
            moveObj.put("playerColor", move.getPlayerColor().toString());
            moveObj.put("from", move.getFrom().toString());
            moveObj.put("to", move.getTo().toString());

            if (move.getCapturedPiece() != null) {
                Map<String, Object> capturedObj = new LinkedHashMap<>();
                capturedObj.put("type", String.valueOf(move.getCapturedPiece().type()));
                capturedObj.put("color", move.getCapturedPiece().getColor().toString());
                moveObj.put("captured", capturedObj);
            }
            movesArr.add(moveObj);
        }
        return movesArr;
    }

    // METODA AJUTATOARE PENTRU INFRUMUSETAREA JSON-urilor
    public static String formatJSON(String jsonString) {
        StringBuilder prettyJSON = new StringBuilder();
        int indentLevel = 0;
        boolean inQuote = false;

        for (char charFromStr : jsonString.toCharArray()) {
            switch (charFromStr) {
                case '"':
                    inQuote = !inQuote;
                    prettyJSON.append(charFromStr);
                    break;
                case ' ':
                    // Ignor spatiile din afara ghilimelelor pentru a curata formatarea veche
                    if (inQuote) {
                        prettyJSON.append(charFromStr);
                    }
                    break;
                case '{':
                case '[':
                    prettyJSON.append(charFromStr);
                    if (!inQuote) {
                        prettyJSON.append("\n");
                        indentLevel++;
                        addIndentation(prettyJSON, indentLevel);
                    }
                    break;
                case '}':
                case ']':
                    if (!inQuote) {
                        prettyJSON.append("\n");
                        indentLevel--;
                        addIndentation(prettyJSON, indentLevel);
                    }
                    prettyJSON.append(charFromStr);
                    break;
                case ',':
                    prettyJSON.append(charFromStr);
                    if (!inQuote) {
                        prettyJSON.append("\n");
                        addIndentation(prettyJSON, indentLevel);
                    }
                    break;
                case ':':
                    prettyJSON.append(charFromStr);
                    if (!inQuote) {
                        prettyJSON.append(" ");
                    }
                    break;
                default:
                    prettyJSON.append(charFromStr);
            }
        }
        return prettyJSON.toString();
    }

    private static void addIndentation(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
    }

    // METODADA STATICA ajutatoare -> Nu mai e nevoie -> Factory Pattern
//    private static Piece buildPiece(char type, Colors color) {
//        if (type == 'K') return new King(color, null);
//        if (type == 'Q') return new Queen(color, null);
//        if (type == 'R') return new Rook(color, null);
//        if (type == 'B') return new Bishop(color, null);
//        if (type == 'N') return new Knight(color, null);
//        if (type == 'P') return new Pawn(color, null);
//        return null;
//    }

    // -------- helper converters --------

    private static JSONArray asArray(Object o) {
        return (o instanceof JSONArray) ? (JSONArray) o : null;
    }

    private static JSONObject asObject(Object o) {
        return (o instanceof JSONObject) ? (JSONObject) o : null;
    }

    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static int asInt(Object o, int def) {
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return o != null ? Integer.parseInt(String.valueOf(o)) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static long asLong(Object o, long def) {
        if (o instanceof Number) return ((Number) o).longValue();
        try {
            return o != null ? Long.parseLong(String.valueOf(o)) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
