package com.bupt.tarecruit.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppPaths {
    private AppPaths() {
    }

    public static Path dataDirectory() {
        String configured = System.getProperty("tarecruit.data.dir");
        if (configured == null || configured.isBlank()) {
            configured = System.getenv("TARECRUIT_DATA_DIR");
        }
        Path path;
        if (configured != null && !configured.isBlank()) {
            path = Paths.get(configured);
        } else {
            path = Paths.get(System.getProperty("user.home"), ".tarecruit", "data");
        }
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
}
