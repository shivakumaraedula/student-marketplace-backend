package com.marketplace.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        String hash = enc.encode("Password@123");
        System.out.println("BCrypt hash for 'Password@123': " + hash);
    }
}
