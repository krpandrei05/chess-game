package utils;

import game.User;

import java.util.List;

public final class UserUtils {
    private UserUtils() {
    }

    // Am nevoie de email in main.Main
    public static String getEmail(String name, List<User> users) {
        // Robot
        if ("computer".equals(name)) {
            return "computer";
        }

        for (User user : users) {
            String email = user.getEmail();
            String nameFromEmail = email.split("@")[0];
            if (name.equals(nameFromEmail)) {
                return email;
            }
        }

        return name + "@example.com";
    }

    // Am nevoie de nume (alias) in main.Main
    public static String getName(String email) {
        String name = email.split("@")[0];
        return name;
    }
}
