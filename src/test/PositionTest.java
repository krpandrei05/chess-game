package test;

import model.Position;

public class PositionTest {
    public static void main(String[] args) {
        TestUtils.printTestStart("Position equals");
        Position p1 = new Position('A', 1);
        Position p2 = new Position('A', 1);
        Position p3 = new Position('A', 2);

        TestUtils.assertTrue(p1.equals(p2), "A1 equals A1");
        TestUtils.assertFalse(p1.equals(p3), "A1 not equals A2");
        TestUtils.printTestPassed("Position equals");

        TestUtils.printTestStart("Position toString");
        TestUtils.assertEquals("A1", p1.toString(), "toString A1");
        TestUtils.assertEquals("A2", p3.toString(), "toString A2");
        TestUtils.printTestPassed("Position toString");
    }
}
