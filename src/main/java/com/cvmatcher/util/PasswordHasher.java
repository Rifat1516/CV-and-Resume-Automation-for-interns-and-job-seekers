package com.cvmatcher.util;

import org.mindrot.jbcrypt.BCrypt;

/** Wraps BCrypt so no other class ever handles raw hashing calls directly. */
public final class PasswordHasher {

    private PasswordHasher() {}

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean verify(String plainPassword, String hashed) {
        return BCrypt.checkpw(plainPassword, hashed);
    }
}
