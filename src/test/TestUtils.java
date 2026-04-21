package test;

import java.util.Objects;

public class TestUtils {
    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("ASSERT FAILED: " + message);
        }
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new RuntimeException("ASSERT FAILED: " + message);
        }
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new RuntimeException("ASSERT FAILED: " + message +
                    " expected=" + expected + " actual=" + actual);
        }
    }

    public static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new RuntimeException("ASSERT FAILED: " + message);
        }
    }

    public static void assertNull(Object obj, String message) {
        if (obj != null) {
            throw new RuntimeException("ASSERT FAILED: " + message);
        }
    }

    public static void printTestPassed(String testName) {
        System.out.println("[OK] " + testName);
    }

    public static void printTestStart(String testName) {
        System.out.println("Running test: " + testName);
    }
}
