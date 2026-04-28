package com.bupt.tarecruit.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.time.Instant;
import java.time.LocalDate;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class JsonStorage {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Instant.class, (com.google.gson.JsonSerializer<Instant>) (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
            .registerTypeAdapter(Instant.class, (com.google.gson.JsonDeserializer<Instant>) (json, typeOfT, context) -> Instant.parse(json.getAsString()))
            .registerTypeAdapter(LocalDate.class, (com.google.gson.JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class, (com.google.gson.JsonDeserializer<LocalDate>) (json, typeOfT, context) -> LocalDate.parse(json.getAsString()))
            .setPrettyPrinting()
            .create();

    private JsonStorage() {
    }

    public static synchronized <T> List<T> readList(final Path path, final TypeToken<List<T>> typeToken) {
        Type type = typeToken.getType();
        try {
            if (Files.notExists(path)) {
                writeList(path, new ArrayList<>());
                return new ArrayList<>();
            }
            try (Reader reader = Files.newBufferedReader(path)) {
                List<T> items = GSON.fromJson(reader, type);
                return items == null ? new ArrayList<>() : new ArrayList<>(items);
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to read " + path, exception);
        }
    }

    public static synchronized void writeList(final Path path, final List<?> items) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(items, writer);
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to write " + path, exception);
        }
    }

    public static synchronized <T> T readObject(
            final Path path,
            final Class<T> type,
            final Supplier<T> defaultSupplier) {
        try {
            if (Files.notExists(path)) {
                T defaultValue = defaultSupplier.get();
                writeObject(path, defaultValue);
                return defaultValue;
            }
            try (Reader reader = Files.newBufferedReader(path)) {
                T item = GSON.fromJson(reader, type);
                if (item != null) {
                    return item;
                }
            }
            T defaultValue = defaultSupplier.get();
            writeObject(path, defaultValue);
            return defaultValue;
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to read " + path, exception);
        }
    }

    public static synchronized void writeObject(final Path path, final Object item) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(item, writer);
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to write " + path, exception);
        }
    }
}
