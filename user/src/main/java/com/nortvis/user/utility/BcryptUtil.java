package com.nortvis.user.utility;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BcryptUtil {
    private static final int SALT_ROUNDS = 12; // You can adjust the salt factor as needed

    public  String hashPassword(String plainTextPassword) {
        String salt = BCrypt.gensalt(SALT_ROUNDS);
        return BCrypt.hashpw(plainTextPassword, salt);
    }

        public boolean isPasswordValid(String inputPassword, String hashedPassword) {

            return BCrypt.checkpw(inputPassword, hashedPassword);
        }
}