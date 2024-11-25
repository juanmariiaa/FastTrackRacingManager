package org.juanmariiaa.others;

import org.juanmariiaa.model.domain.User;

public class SingletonUserSession {
    private static User currentUser = null;

    public static void login(int id, String username) {
        currentUser = new User(id, "", username);
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLogged() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
