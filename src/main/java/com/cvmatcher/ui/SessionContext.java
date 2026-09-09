package com.cvmatcher.ui;

import com.cvmatcher.model.User;

/** Holds the currently logged-in user for the duration of the app session. */
public final class SessionContext {

    private static User currentUser;

    private SessionContext() {}

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
