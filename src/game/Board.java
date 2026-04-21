package game;

import exceptions.InvalidMoveException;
import exceptions.InvalidCommandException;
import model.ChessPair;
import model.Colors;
import model.Piece;
import model.Position;
import pieces.*;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class Board {
    private final TreeSet<ChessPair<Position, Piece>> pieces;

    public Board() {
        this.pieces = new TreeSet<>();
    }

    // 1. initialize
    public void initialize() {
        this.pieces.clear();

        // Cod vechi (Fara Factory Pattern)
//        // Piesele ALBE
//        this.addPiece(new Rook(Colors.WHITE, new Position('A', 1)));
//        this.addPiece(new Knight(Colors.WHITE, new Position('B', 1)));
//        this.addPiece(new Bishop(Colors.WHITE, new Position('C', 1)));
//        this.addPiece(new Queen(Colors.WHITE, new Position('D', 1)));
//        this.addPiece(new King(Colors.WHITE, new Position('E', 1)));
//        this.addPiece(new Bishop(Colors.WHITE, new Position('F', 1)));
//        this.addPiece(new Knight(Colors.WHITE, new Position('G', 1)));
//        this.addPiece(new Rook(Colors.WHITE, new Position('H', 1)));
//        char i;
//        for (i = 'A'; i <= 'H'; i++) {
//            this.addPiece(new Pawn(Colors.WHITE, new Position(i, 2)));
//        }
//
//        // Piese NEGRE
//        this.addPiece(new Rook(Colors.BLACK, new Position('A', 8)));
//        this.addPiece(new Knight(Colors.BLACK, new Position('B', 8)));
//        this.addPiece(new Bishop(Colors.BLACK, new Position('C', 8)));
//        this.addPiece(new Queen(Colors.BLACK, new Position('D', 8)));
//        this.addPiece(new King(Colors.BLACK, new Position('E', 8)));
//        this.addPiece(new Bishop(Colors.BLACK, new Position('F', 8)));
//        this.addPiece(new Knight(Colors.BLACK, new Position('G', 8)));
//        this.addPiece(new Rook(Colors.BLACK, new Position('H', 8)));
//
//        for (i = 'A'; i <= 'H'; i++) {
//            this.addPiece(new Pawn(Colors.BLACK, new Position(i, 7)));
//        }

        // Factory Pattern
        try {
            // Piesele ALBE
            this.addPiece(PieceFactory.createPiece('R', Colors.WHITE, new Position('A', 1)));
            this.addPiece(PieceFactory.createPiece('N', Colors.WHITE, new Position('B', 1)));
            this.addPiece(PieceFactory.createPiece('B', Colors.WHITE, new Position('C', 1)));
            this.addPiece(PieceFactory.createPiece('Q', Colors.WHITE, new Position('D', 1)));
            this.addPiece(PieceFactory.createPiece('K', Colors.WHITE, new Position('E', 1)));
            this.addPiece(PieceFactory.createPiece('B', Colors.WHITE, new Position('F', 1)));
            this.addPiece(PieceFactory.createPiece('N', Colors.WHITE, new Position('G', 1)));
            this.addPiece(PieceFactory.createPiece('R', Colors.WHITE, new Position('H', 1)));

            char i;
            for (i = 'A'; i <= 'H'; i++) {
                this.addPiece(PieceFactory.createPiece('P', Colors.WHITE, new Position(i, 2)));
            }

            // Piese NEGRE
            this.addPiece(PieceFactory.createPiece('R', Colors.BLACK, new Position('A', 8)));
            this.addPiece(PieceFactory.createPiece('N', Colors.BLACK, new Position('B', 8)));
            this.addPiece(PieceFactory.createPiece('B', Colors.BLACK, new Position('C', 8)));
            this.addPiece(PieceFactory.createPiece('Q', Colors.BLACK, new Position('D', 8)));
            this.addPiece(PieceFactory.createPiece('K', Colors.BLACK, new Position('E', 8)));
            this.addPiece(PieceFactory.createPiece('B', Colors.BLACK, new Position('F', 8)));
            this.addPiece(PieceFactory.createPiece('N', Colors.BLACK, new Position('G', 8)));
            this.addPiece(PieceFactory.createPiece('R', Colors.BLACK, new Position('H', 8)));

            for (i = 'A'; i <= 'H'; i++) {
                this.addPiece(PieceFactory.createPiece('P', Colors.BLACK, new Position(i, 7)));
            }

        } catch (InvalidCommandException e) {
            System.out.println("Eroare critica la initializarea tablei: " + e.getMessage());
            // Vad eroarea exacta
            e.printStackTrace();
        }
    }

    // METODA ajutatoare pentru 1.initialize
    public void addPiece(Piece piece) {
        pieces.add(new ChessPair<>(piece.getPosition(), piece));
    }

    // 2. movePiece
    public Piece movePiece(Position from, Position to) throws InvalidMoveException, InvalidCommandException {
        return this.movePiece(from, to, 'Q');
    }

    public Piece movePiece(Position from, Position to, char promotionPawnChar) throws InvalidMoveException, InvalidCommandException {
        if (!isValidMove(from, to)) {
            Piece pieceToMove = this.getPieceAt(from);

            if (pieceToMove == null) {
                throw new InvalidMoveException("Nu exista nicio piesa la pozitia de start -> " + from);
            }

            List<Position> possibleMoves = pieceToMove.getPossibleMoves(this);
            if (!possibleMoves.contains(to)) {
                throw new InvalidMoveException("Mutare invalida! Piesa de pe " + pieceToMove.toString() + " nu se poate deplasa la "
                        + to + " (obstacol sau miscare nepermisa).");
            }
            if (resultsInCheck(pieceToMove, to)) {
                throw new InvalidMoveException("Mutare ilegala! Regele intra in sah.");
            }
            throw new InvalidMoveException("Mutare invalida!");
        }

        // Mutarea piesei daca nu e eroare
        Piece pieceToMove = getPieceAt(from);
        Piece capturedPiece = getPieceAt(to);

        removePieceAt(from);
        if (capturedPiece != null) {
            removePieceAt(to);
        }

        pieceToMove.setPosition(to);

        // Pionul este promovat!
        if (pieceToMove instanceof Pawn) {
            int y = pieceToMove.getPosition().getY();
            // Daca a ajuns la finalul tablei de sah
            if ((pieceToMove.getColor() == Colors.WHITE && y == 8)
                    || ((pieceToMove.getColor() == Colors.BLACK && y == 1))) {
                // Cod vechi
//                Piece promotedPiece;
//                switch (Character.toUpperCase(promotionPawnChar)) {
//                    case 'R':
//                        promotedPiece = new Rook(pieceToMove.getColor(), to);
//                        break;
//                    case 'B':
//                        promotedPiece = new Bishop(pieceToMove.getColor(), to);
//                        break;
//                    case 'N':
//                        promotedPiece = new Knight(pieceToMove.getColor(), to);
//                        break;
//                    case 'Q':
//                    default:
//                        promotedPiece = new Queen(pieceToMove.getColor(), to);
//                        break;
//                }

                // Factory Pattern
                Piece promotedPiece = PieceFactory.createPiece(promotionPawnChar, pieceToMove.getColor(), to);

                this.addPiece(promotedPiece);
                return capturedPiece;
            }
        }

        this.addPiece(pieceToMove);
        return capturedPiece;
    }

    // 3. getPieceAt
    // Returneaza piesa care se afla la positia trimisa ca parametru
    public Piece getPieceAt(Position position){
        for(ChessPair<Position, Piece> pair : pieces) {
            if (pair.getKey().equals(position)) {
                return pair.getValue();
            }
        }
        // Nu am gasit nimic
        return null;
    }

    // 4. isValidMove
    public boolean isValidMove(Position from, Position to) {
        Piece pieceToMove = this.getPieceAt(from);

        // Exista piesa? -> EXCEPTIE
        if (pieceToMove == null) {
            return false;
        }

        List<Position> possibleMoves = pieceToMove.getPossibleMoves(this);
        if (!possibleMoves.contains(to)) {
            return false;
        }

        if (resultsInCheck(pieceToMove, to)) {
            return false;
        }
        return true;
    }


    // METODE AJUTATOARE

    // METODA ajutatoare pentru isValidMove
    // Simuleaza o mutare si verifica daca imi las propriul Rege in sah
    public boolean resultsInCheck(Piece pieceToMove, Position to) {
        Position from = pieceToMove.getPosition();
        // Culoarea piesei pe care o mut
        Colors movingColor = pieceToMove.getColor();

        Position kingPosition;
        if (pieceToMove instanceof King) {
            kingPosition = to;
        }
        else {
            kingPosition = getKingPosition(movingColor);
        }

        // Salvam starea initiala a piesei pe care vrem sa o atacam (to)
        Piece capturedPiece = getPieceAt(to);
        this.removePieceAt(from);

        if (capturedPiece != null) {
            removePieceAt(to);
        }

        pieceToMove.setPosition(to);
        // Creez mutarea temporara (de test)
        ChessPair<Position, Piece> tempMove = new ChessPair<>(to, pieceToMove);
        pieces.add(tempMove);
        boolean isChecked = isKingAttacked(kingPosition, movingColor);
        pieces.remove(tempMove);
        pieceToMove.setPosition(from);
        pieces.add(new ChessPair<>(from, pieceToMove));
        
        if (capturedPiece != null) {
            pieces.add(new ChessPair<>(to, capturedPiece));
        }
        
        return isChecked;
    }

    // Metoda ajutatoare resultsInCheck
    public Position getKingPosition(Colors color) {
        for (ChessPair<Position, Piece> pair : pieces) {
            Piece currentPiece = pair.getValue();
            if (currentPiece instanceof King && currentPiece.getColor() == color) {
                return pair.getKey();
            }
        }
        return null;
    }

    public boolean isKingAttacked(Position kingPosition, Colors kingColor) {
        if (kingPosition == null) {
            return false;
        }

        Colors enemyColor = Colors.GRAY;
        if (kingColor == Colors.WHITE) {
            enemyColor = Colors.BLACK;
        }
        else if (kingColor == Colors.BLACK) {
            enemyColor = Colors.WHITE;
        }

        for (ChessPair<Position, Piece> pair : pieces) {
            Piece piece = pair.getValue();
            if (piece.getColor() == enemyColor) {
                if (piece.checkForCheck(this, kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void removePieceAt(Position position) {
        Iterator<ChessPair<Position, Piece>> it = pieces.iterator();
        while (it.hasNext()) {
            ChessPair<Position, Piece> pair = it.next();
            if (pair.getKey().equals(position)) {
                it.remove();
                break;
            }
        }
    }
}
