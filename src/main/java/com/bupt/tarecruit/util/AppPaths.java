package com.bupt.tarecruit.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppPaths {
    private static final String DATA_DIR_PROPERTY = "tarecruit.data.dir";
    private static final String DATA_DIR_ENV = "TARECRUIT_DATA_DIR";

    private AppPaths() {
    }

    public static Path dataDirectory() {
        Path workingDirectory = Paths.get(System.getProperty("user.dir", "."));
        Path path = resolveDataDirectory(
                System.getProperty(DATA_DIR_PROPERTY),
                System.getenv(DATA_DIR_ENV),
                workingDirectory);
        ensureDirectory(path);
        return path;
    }

    public static Path cvDirectory() {
        Path path = dataDirectory().resolve("cv");
        ensureDirectory(path);
        return path;
    }

    public static void ensureDirectory(final Path path) {
        try {
            Files.createDirectories(path);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to create directory: " + path, exception);
        }
    }

    static Path resolveDataDirectory(
            final String systemPropertyValue,
            final String environmentValue,
            final Path workingDirectory) {
        String configured = firstNonBlank(systemPropertyValue, environmentValue);
        if (configured != null) {
            return Paths.get(configured).toAbsolutePath().normalize();
        }
        return resolveProjectDataDirectory(workingDirectory);
    }

    static Path resolveProjectDataDirectory(final Path workingDirectory) {
        Path current = workingDirectory.toAbsolutePath().normalize();
        while (current != null) {
            if (Files.exists(current.resolve("pom.xml"))) {
                return current.resolve("data").toAbsolutePath().normalize();
            }
            current = current.getParent();
        }
        throw new IllegalStateException(
                "Unable to resolve the application data directory. Set "
                        + DATA_DIR_PROPERTY + " or " + DATA_DIR_ENV
                        + ", or start the application from the project directory.");
    }

    private static String firstNonBlank(final String first, final String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}
