package game;

import model.ChessPair;
import model.Colors;
import model.Position;
import model.Piece;
import observers.GameObserver;
import strategies.EndGameCondition;
import strategies.ScoringStrategy;
import strategies.StandardScoringStrategy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Game {
    // Observer Pattern
    private List<GameObserver> observers = new ArrayList<>();

    private int id;
    private Board board;
    private Player player1;
    private Player player2;
    private List<Move> moves;
    private int currentPlayerIndex;
    // Pentru sah mat
    private Player checkMatedPlayer;

    // Tema 2
    private EndGameCondition endGameCondition;
    private int endGameBonusPenalty;

    public Game(int id, Player player1, Player player2) {
        this.id = id;
        this.board = new Board();
        this.player1 = player1;
        this.player2 = player2;
        this.moves = new LinkedList<>();
        this.currentPlayerIndex = 1;

        endGameCondition = null;
        endGameBonusPenalty = 0;
    }

    // Observer Pattern
    public void addObserver(GameObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(GameObserver observer) {
        this.observers.remove(observer);
    }

    public void notifyMoveMade(Move move) {
        for (GameObserver obs : observers) {
            obs.onMoveMade(move);
        }
    }

    public void notifyPieceCaptured(Piece piece) {
        for (GameObserver obs : observers) {
            obs.onPieceCaptured(piece);
        }
    }

    public void notifyPlayerSwitch(Player currentPlayer) {
        for (GameObserver obs : observers) {
            obs.onPlayerSwitch(currentPlayer);
        }
    }

    // Pentru JsonReaderUtil
    public Game() {
        this.board = new Board();
        this.moves = new LinkedList<>();
        this.currentPlayerIndex = 1;
    }

    public void start() {
        board.initialize();
        moves.clear();

        if (player1.getColor() == Colors.WHITE) {
            this.currentPlayerIndex = 1;
        }
        else {
            this.currentPlayerIndex = 2;
        }

        player1.initializeOwnedPieces(board);
        player2.initializeOwnedPieces(board);
    }

    public void resume() {
        player1.initializeOwnedPieces(board);
        player2.initializeOwnedPieces(board);
        player1.recalculatePointsFromMoves(moves);
        player2.recalculatePointsFromMoves(moves);
        System.out.println("Joc reluat. Este randul lui: " + getCurrentPlayer().getName());
    }

    public void switchPlayer() {
        if (currentPlayerIndex == 1) {
            currentPlayerIndex = 2;
        }
        else {
            currentPlayerIndex = 1;
        }
        notifyPlayerSwitch(getCurrentPlayer());
    }

    public boolean checkForCheckMate() {
        Player currentPlayer = this.getCurrentPlayer();

        Position kingPosition = board.getKingPosition(currentPlayer.getColor());
        if (kingPosition == null) {
            return false;
        }

        if (!board.isKingAttacked(kingPosition, currentPlayer.getColor())) {
            return false;
        }

        if (hasAnyValidMove(currentPlayer)) {
            return false;
        }
        // Sah-Mat
        this.checkMatedPlayer = currentPlayer;
        return true;
    }

    public boolean checkForStaleMate() {
        Player currentPlayer = this.getCurrentPlayer();
        if (currentPlayer == null) {
            return false;
        }

        Position kingPosition = board.getKingPosition(currentPlayer.getColor());
        if (kingPosition == null) {
            return false;
        }

        if (board.isKingAttacked(kingPosition, currentPlayer.getColor())) {
            return false;
        }
        return !hasAnyValidMove(currentPlayer);
    } 

    private boolean hasAnyValidMove(Player player) {
        if (player == null) {
            return false;
        }
        for (ChessPair<Position, Piece> pair : player.getOwnedPieces()) {
            Piece piece = pair.getValue();
            Position from = pair.getKey();
            List<Position> candidates = piece.getPossibleMoves(board);

            for (Position to : candidates) {
                if (board.isValidMove(from, to)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkDrawByRepetition() {
        int n = moves.size();
        if (n < 6) {
            return false;
        }

        Move lastCurrent = moves.get(n - 1);
        Move prevCurrent1 = moves.get(n - 3);
        Move prevCurrent2 = moves.get(n - 5);

        if (!sameFinalPosition(lastCurrent, prevCurrent1)) {
            return false;
        }
        if (!sameFinalPosition(lastCurrent, prevCurrent2)) {
            return false;
        }

        Move lastOpponent = moves.get(n - 2);
        Move prevOpponent1 = moves.get(n - 4);
        Move prevOpponent2 = moves.get(n - 6);

        if (!sameFinalPosition(lastOpponent, prevOpponent1)) {
            return false;
        }
        if (!sameFinalPosition(lastOpponent, prevOpponent2)) {
            return false;
        }

        return true;
    }

    
    public boolean sameFinalPosition(Move m1, Move m2) {
        if (m1.getPlayerColor() != m2.getPlayerColor()) {
            return false;
        }
        return m1.getFrom().equals(m2.getFrom()) && m1.getTo().equals(m2.getTo());
    }

    // Am schimbat antetul METODEI
    public void addMove(Player player, Position from, Position to, Piece capturedPiece) {
        Move move = new Move(player.getColor(), from, to, capturedPiece);
        this.addMove(move);
    }

    public void addMove(Move move) {
        moves.add(move);
        notifyMoveMade(move);
        if (move.getCapturedPiece() != null) {
            notifyPieceCaptured(move.getCapturedPiece());
            Player mover = (player1 != null && player1.getColor() == move.getPlayerColor()) ? player1 : player2;
            Player opponent = (mover == player1) ? player2 : player1;
            if (opponent != null) {
                opponent.removeOwnedPieceAt(move.getTo());
            }
        }
    }

    // Getters
    public int getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public Player getOpponent() {
        if (currentPlayerIndex == 1) {
            return player2;
        }
        else {
            return player1;
        }
    }

    public List<Move> getMoves() {
        return moves;
    }

    public Player getCurrentPlayer() {
        if (currentPlayerIndex == 1) {
            return player1;
        }
        else {
            return player2;
        }
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public Player getCheckMatedPlayer() {
        return checkMatedPlayer;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setBoard(List<Piece> pieces) {
        this.board = new Board();
        if (pieces != null) {
            for (Piece piece : pieces) {
                this.board.addPiece(piece);
            }
        }
    }

    public void setPlayers(List <Player> players) {
        this.player1 = players.get(0);
        this.player2 = players.get(1);
    }

    public void setCurrentPlayerColor(String colorString) {
        Colors color;
        if ("WHITE".equals(colorString)) {
            color = Colors.WHITE;
        }
        else {
            color = Colors.BLACK;
        }
        if (player1 != null && player1.getColor() == color) {
            this.currentPlayerIndex = 1;
        } else {
            this.currentPlayerIndex = 2;
        }
    }

    public void setMoves(List<Move> movesList) {
        this.moves.clear();
        if (movesList != null) {
            this.moves.addAll(movesList);
        }
    }

    // Doar de test
    public void setCurrentPlayerIndex(int index) {
        this.currentPlayerIndex = index;
    }

    // Tema 2
    public EndGameCondition evaluateEndGameCondition(String reason) {
        EndGameCondition condition = null;

        if ("RESIGN".equals(reason)) {
            condition = EndGameCondition.RESIGN_OWN;
        } else if (checkForCheckMate()) {
            Player loser = this.checkMatedPlayer;

            Player human = this.getHumanPlayer();
            condition = loser.equals(human) ? EndGameCondition.LOSE_CHECKMATE : EndGameCondition.WIN_CHECKMATE;

        } else if (checkDrawByRepetition()) {
            condition = EndGameCondition.RESIGN_OPPONENT;
        } else if (checkForStaleMate()) {
            condition = EndGameCondition.DRAW;
        }

        this.endGameCondition = condition;

        if (condition != null) {
            // Strategy Pattern
            ScoringStrategy strategy = new StandardScoringStrategy();
            this.endGameBonusPenalty = strategy.getEndGamePoints(condition);
        } else {
            this.endGameBonusPenalty = 0;
        }

        return condition;
    }


    public Player getHumanPlayer() {
        if ("computer".equals(this.player1.getName())) {
            return this.player2;
        }
        return this.player1;
    }

    public EndGameCondition getEndGameCondition() {
        return endGameCondition;
    }

    public int getEndGameBonusPenalty() {
        return endGameBonusPenalty;
    }
}
