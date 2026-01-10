package com.leon.rest_api;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGeneratorTest {

    @Test
    void generateBcryptPassword() {
        // 1. You can control the "strength" (4 to 31). Default is 10.
        // Higher = slower hashing = more secure against brute force.
//        int strength = 12;
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(strength);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "1234"; //testing purpose

        // 2. BCrypt automatically generates a random Salt internally every time
        String hash1 = encoder.encode(rawPassword);
        String hash2 = encoder.encode(rawPassword);

        System.out.println("Hash 1: " + hash1);

        // 3. Even though the password is the same, the hashes are DIFFERENT.
        // This proves the salt is working and unique per execution.
        if (hash1.equals(hash2)) {
            throw new RuntimeException("Security Error: Hashes should not be equal! Salt is missing.");
        }
    }
}