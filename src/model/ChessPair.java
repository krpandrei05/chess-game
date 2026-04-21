package model;

public class ChessPair<K extends Comparable<K>, V> implements Comparable<ChessPair<K, V>> {
    private K key;
    private V value;

    // Constructor
    public ChessPair(K key, V value){
        this.key = key;
        this.value = value;
    }

    // Getters
    public K getKey(){
        return this.key;
    }

    public V getValue(){
        return this.value;
    }

    // Setters
    public void setKey(K key){
        this.key = key;
    }

    public void setValue(V value){
        this.value = value;
    }

    // Garantie ca in enunt K == Position
    @Override
    public int compareTo(ChessPair<K, V> pair){
        return this.key.compareTo(pair.key);
    }

    @Override
    public String toString(){
        return key.toString() + " -> " + value.toString();
    }
}
