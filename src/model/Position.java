package model;

import java.util.Objects;

public class Position implements Comparable<Position>{
    private char x;
    private int y;

    // Constructor
    public Position(char x, int y){
        this.x = x;
        this.y = y;
    }

    // Pentru JsonReaderUtil
    public Position(String positionString) {
        // '1' -> 1
        this(positionString.charAt(0), positionString.charAt(1) - '0');
    }

    // Getters
    public char getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    // Setters
    public void setX(char x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    @Override
    public boolean equals(Object o){
        // Acelasi obiect in memorie
        if (this == o){
            return true;
        }
        if ((o instanceof Position) == false){
            return false;
        }

        // Obiecte cu adrese diferite dar poate aceleasi campuri
        Position position = (Position) o;
        return this.x == position.x && this.y == position.y;
    }

    // Contract equals <-> hashCode
    @Override
    public int hashCode(){
        return Objects.hash(this.x, this.y);
    }

    @Override
    public String toString(){
        return x + "" + y;
    }

    // Datorita interfetei Comparable
    @Override
    public int compareTo(Position position){
        if (this.y != position.y){
            return Integer.compare(this.y, position.y);
        }
        return Character.compare(this.x, position.x);
    }
}
