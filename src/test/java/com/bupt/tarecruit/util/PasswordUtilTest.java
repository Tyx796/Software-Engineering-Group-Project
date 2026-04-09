package com.bupt.tarecruit.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.junit.jupiter.api.Test;

class PasswordUtilTest {
    @Test
    void hashUsesVersionedSaltedFormatAndMatchesRawPassword() {
        String storedHash = PasswordUtil.hash("password123");

        assertTrue(storedHash.startsWith("v2$"));
        assertTrue(PasswordUtil.matches("password123", storedHash));
        assertFalse(PasswordUtil.matches("wrong-password", storedHash));
        assertFalse(PasswordUtil.needsRehash(storedHash));
    }

    @Test
    void samePasswordProducesDifferentHashesBecauseOfSalt() {
        String first = PasswordUtil.hash("password123");
        String second = PasswordUtil.hash("password123");

        assertNotEquals(first, second);
    }

    @Test
    void legacyHashesStillVerifyAndRequireRehash() throws Exception {
        String legacyHash = legacySha256("password123");

        assertTrue(PasswordUtil.matches("password123", legacyHash));
        assertTrue(PasswordUtil.needsRehash(legacyHash));
    }

    @Test
    void malformedVersionedHashFailsClosed() {
        assertFalse(PasswordUtil.matches("password123", "v2$not-hex$hash"));
    }

    private String legacySha256(final String rawPassword) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hash = messageDigest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        for (byte current : hash) {
            builder.append(String.format("%02x", current));
        }
        return builder.toString();
    }
}
