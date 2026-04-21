package game;

import exceptions.InvalidCommandException;
import exceptions.InvalidMoveException;
import model.ChessPair;
import model.Colors;
import model.Position;
import model.Piece;
import strategies.ScoringStrategy;
import strategies.StandardScoringStrategy;

import java.util.*;

public class Player {
    // Strategy Pattern
    private ScoringStrategy scoringStrategy;

    private String name;
    private Colors color;
    private List<Piece> capturedPieces;
    private TreeSet<ChessPair<Position, Piece>> ownedPieces;
    // Puncte joc curent
    private int points;


    public Player(String name, Colors color) {
        this.name = name;
        this.color = color;
        this.capturedPieces = new ArrayList<>();
        this.ownedPieces = new TreeSet<>();
        this.points = 0;

        // Strategy Pattern
        this.scoringStrategy = new StandardScoringStrategy();
    }

    // 1. makeMove
    public void makeMove(Position from, Position to, Board board) throws InvalidMoveException, InvalidCommandException {
        this.makeMove(from, to, board, 'Q');
    }

    public void makeMove(Position from, Position to, Board board, char promotionPawnChar) throws InvalidMoveException, InvalidCommandException {
        Piece pieceToMove = board.getPieceAt(from);
        if (pieceToMove == null || pieceToMove.getColor() != this.color) {
            throw new InvalidMoveException("Nu poti muta o piesa care nu iti apartine!");
        }

        Piece capturedPiece = board.movePiece(from, to, promotionPawnChar);

        // Se executa doar daca movePiece nu a dat eroare
        if (capturedPiece != null) {
            this.capturedPieces.add(capturedPiece);
            // Observer Pattern e folosit
            // this.points += calculatePoints(capturedPiece);
        }

        this.updateOwnedPieces(from, to, board);
    }


    // Mutarea robotului
    public Move makeRandomMove (Board board) throws InvalidMoveException, InvalidCommandException{
        List<Move> allValidMoves = new ArrayList<>();

        for (ChessPair<Position, Piece> pair : this.getOwnedPieces()) {
            Piece piece = pair.getValue();
            Position from = pair.getKey();

            List<Position> candidates = piece.getPossibleMoves(board);
            for (Position to : candidates) {
                if (board.isValidMove(from, to)) {
                    // Poate sa fie si null
                    Piece capturedPiece = board.getPieceAt(to);
                    allValidMoves.add(new Move(this.color, from, to, capturedPiece));
                }
            }
        }
        // Daca e lista e goala, inseamna ca e sah mat
        if (allValidMoves.isEmpty()) {
            return null;
        }
        Random random = new Random();
        Move selectedMove = allValidMoves.get(random.nextInt(allValidMoves.size()));

        // Executa mutarea
        // Daca e pion, simplu devine Regina
        this.makeMove(selectedMove.getFrom(), selectedMove.getTo(), board);

        return selectedMove;
    }

    // Metoda ajutatoare pentru 1. makeMove
    private void updateOwnedPieces(Position from, Position to, Board board) {
        Iterator<ChessPair<Position, Piece>> it = ownedPieces.iterator();
        while(it.hasNext()) {
            ChessPair<Position, Piece> pair = it.next();
            if (pair.getKey().equals(from)) {
                it.remove();
            }
        }

        Piece movedPiece = board.getPieceAt(to);
        if (movedPiece != null) {
            ownedPieces.add(new ChessPair<>(to, movedPiece));
        }

        Iterator<ChessPair<Position, Piece>> cleanIt = ownedPieces.iterator();
        while(cleanIt.hasNext()) {
            ChessPair<Position, Piece> pair = cleanIt.next();
            Position position = pair.getKey();
            Piece piece = board.getPieceAt(position);

            // Daca la acea pozitie nu mai e nimic (a fost capturat de adversar)
            if (piece == null || piece.getColor() != this.color) {
                cleanIt.remove();
            }
        }
    }

    public void removeOwnedPieceAt(Position position) {
        if (position == null) {
            return;
        }
        Iterator<ChessPair<Position, Piece>> it = ownedPieces.iterator();
        while (it.hasNext()) {
            ChessPair<Position, Piece> pair = it.next();
            if (pair.getKey().equals(position)) {
                it.remove();
                break;
            }
        }
    }
    // Cod vechi -> Strategy Pattern
//    // Metoda ajutatoare pentru 1. makeMove
//    private int calculatePoints(Piece piece) {
//        switch (piece.type()) {
//            case 'Q':
//                return 90;
//            case 'R':
//                return 50;
//            case 'B':
//                return 30;
//            case 'N':
//                return 30;
//            case 'P':
//                return 10;
//            default:
//                return 0;
//        }
//    }

    // Strategy Pattern
    private int calculatePoints(Piece piece) {
        return scoringStrategy.getCapturePoints(piece);
    }

    public void recalculatePointsFromMoves(List<Move> moves) {
        this.points = 0;
        this.capturedPieces.clear();

        for (Move move : moves) {
            if (move.getCapturedPiece() != null && move.getPlayerColor() == this.color) {
                Piece captured = move.getCapturedPiece();
                this.capturedPieces.add(captured);
                this.points += calculatePoints(captured);
            }
        }
    }

    // 2. getCapturedPieces
    public List<Piece> getCapturedPieces(){
        return capturedPieces;
    }

    // 3. getOwnedPieces
    public List<ChessPair<Position, Piece>> getOwnedPieces() {
        return new ArrayList<>(ownedPieces);
    }

    // 4. getPoints
    public int getPoints() {
        return this.points;
    }

    // 5. setPoints
    public void setPoints(int points) {
        this.points = points;
    }

    // Metoda Obligatorie apelata de Game la start() si resume()
    public void initializeOwnedPieces(Board board) {
        ownedPieces.clear();
        char x;
        int y;
        for (x = 'A'; x <= 'H'; x++) {
            for (y = 1; y <= 8; y++) {
                Position position = new Position(x, y);
                Piece piece = board.getPieceAt(position);
                if (piece != null && piece.getColor() == this.color) {
                    this.ownedPieces.add(new ChessPair<>(position, piece));
                }
            }
        }
    }

    // Getters
    public String getName() {
        return this.name;
    }

    public Colors getColor() {
        return this.color;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setColor(Colors color) {
        this.color = color;
    }
}
