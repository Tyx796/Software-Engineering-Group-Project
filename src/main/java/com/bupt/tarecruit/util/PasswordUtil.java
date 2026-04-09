package com.bupt.tarecruit.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class PasswordUtil {
    private static final String VERSION_PREFIX = "v2";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hash(final String rawPassword) {
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        return VERSION_PREFIX + "$" + HexFormat.of().formatHex(salt) + "$" + hashWithSalt(rawPassword, salt);
    }

    public static boolean matches(final String rawPassword, final String storedHash) {
        if (storedHash == null || storedHash.isBlank()) {
            return false;
        }
        if (isLegacyHash(storedHash)) {
            return MessageDigest.isEqual(
                    legacyHash(rawPassword).getBytes(StandardCharsets.UTF_8),
                    storedHash.getBytes(StandardCharsets.UTF_8));
        }
        String[] parts = storedHash.split("\\$");
        if (parts.length != 3 || !VERSION_PREFIX.equals(parts[0])) {
            return false;
        }
        try {
            byte[] salt = HexFormat.of().parseHex(parts[1]);
            String candidate = hashWithSalt(rawPassword, salt);
            return MessageDigest.isEqual(
                    candidate.getBytes(StandardCharsets.UTF_8),
                    parts[2].getBytes(StandardCharsets.UTF_8));
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    public static boolean needsRehash(final String storedHash) {
        return isLegacyHash(storedHash);
    }

    private static boolean isLegacyHash(final String storedHash) {
        return !storedHash.startsWith(VERSION_PREFIX + "$");
    }

    private static String hashWithSalt(final String rawPassword, final byte[] salt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(salt);
            byte[] hash = messageDigest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte current : hash) {
                builder.append(String.format("%02x", current));
            }
            return builder.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to hash password", exception);
        }
    }

    private static String legacyHash(final String rawPassword) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte current : hash) {
                builder.append(String.format("%02x", current));
            }
            return builder.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to hash password", exception);
        }
    }
}
