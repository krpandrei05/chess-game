package model;

import game.Board;
import strategies.MoveStrategy;

import java.util.List;
import java.util.Objects;

public abstract class Piece implements ChessPiece {
    // Strategy Pattern
    private MoveStrategy moveStrategy;

    private final Colors color;
    private Position position;

    // Constructor
    public Piece(Colors color, Position position){
        // Culoarea poate fi setata doar o data (in constructor)
        this.color = color;
        this.position = position;
    }

    // Strategy Pattern
    protected void setMoveStrategy(MoveStrategy moveStrategy) {
        this.moveStrategy = moveStrategy;
    }

    public List<Position> getPossibleMoves(Board board) {
        return this.moveStrategy.getPossibleMoves(board, this.position);
    }

    // Getters
    public Colors getColor(){
        return this.color;
    }

    public Position getPosition(){
        return this.position;
    }

    // Setters
    public void setPosition(Position position){
        this.position = position;
    }

    @Override
    public boolean equals(Object o){
        if (this == o){
            return true;
        }
        if ((o instanceof Piece) == false){
            return false;
        }
        Piece piece = (Piece) o;
        return this.color == piece.color && Objects.equals(this.position, piece.position);
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.color, this.position);
    }

    @Override
    public String toString() {
        return position.getX() + "" + position.getY();
    }
}
