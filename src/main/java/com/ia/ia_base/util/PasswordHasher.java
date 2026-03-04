package com.ia.ia_base.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String password, String passwordHash) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return BCrypt.checkpw(password, passwordHash);
    }
}
