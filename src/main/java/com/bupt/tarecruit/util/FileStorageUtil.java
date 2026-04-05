package com.bupt.tarecruit.util;

import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class FileStorageUtil {
    private FileStorageUtil() {
    }

    public static <T> List<T> readList(final Path path, final TypeToken<List<T>> typeToken) {
        return JsonStorage.readList(path, typeToken);
    }

    public static void writeList(final Path path, final List<?> items) {
        JsonStorage.writeList(path, items);
    }

    public static Path dataDirectory() {
        return AppPaths.dataDirectory();
    }

    public static Path cvDirectory() {
        return AppPaths.cvDirectory();
    }

    public static void ensureDirectory(final Path path) {
        AppPaths.ensureDirectory(path);
    }

    public static void saveFile(final InputStream inputStream, final Path target) {
        try {
            Files.createDirectories(target.getParent());
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to save file: " + target, exception);
        }
    }

    public static boolean fileExists(final Path path) {
        return Files.exists(path);
    }
}
