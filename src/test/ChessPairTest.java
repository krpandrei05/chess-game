package test;

import model.ChessPair;
import model.Position;

import java.util.TreeSet;

public class ChessPairTest {
    public static void main(String[] args) {
        TestUtils.printTestStart("ChessPair ordering");

        ChessPair<Position, String> p1 = new ChessPair<>(new Position('A', 1), "one");
        ChessPair<Position, String> p2 = new ChessPair<>(new Position('A', 2), "two");
        ChessPair<Position, String> p3 = new ChessPair<>(new Position('B', 1), "three");

        TreeSet<ChessPair<Position, String>> set = new TreeSet<>();
        set.add(p2);
        set.add(p3);
        set.add(p1);

        ChessPair<Position, String> first = set.first();
        TestUtils.assertEquals(new Position('A', 1), first.getKey(), "first element is A1");

        TestUtils.printTestPassed("ChessPair ordering");

        TestUtils.printTestStart("ChessPair toString");
        String repr = p1.toString();
        TestUtils.assertNotNull(repr, "toString not null");
        TestUtils.printTestPassed("ChessPair toString");
    }
}
