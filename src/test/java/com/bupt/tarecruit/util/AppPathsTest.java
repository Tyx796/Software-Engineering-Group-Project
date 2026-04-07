package com.bupt.tarecruit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppPathsTest {
    @TempDir
    Path tempDir;

    @Test
    void systemPropertyTakesPrecedenceOverEnvironmentVariable() {
        Path propertyPath = tempDir.resolve("property-data");
        Path envPath = tempDir.resolve("env-data");

        Path resolved = AppPaths.resolveDataDirectory(
                propertyPath.toString(),
                envPath.toString(),
                tempDir);

        assertEquals(propertyPath.toAbsolutePath().normalize(), resolved);
    }

    @Test
    void environmentVariableIsUsedWhenSystemPropertyMissing() {
        Path envPath = tempDir.resolve("env-data");

        Path resolved = AppPaths.resolveDataDirectory(
                null,
                envPath.toString(),
                tempDir);

        assertEquals(envPath.toAbsolutePath().normalize(), resolved);
    }

    @Test
    void projectDataDirectoryIsResolvedFromProjectRoot() throws Exception {
        Path projectRoot = tempDir.resolve("project");
        Files.createDirectories(projectRoot);
        Files.createFile(projectRoot.resolve("pom.xml"));

        Path resolved = AppPaths.resolveDataDirectory(null, null, projectRoot);

        assertEquals(projectRoot.resolve("data").toAbsolutePath().normalize(), resolved);
    }

    @Test
    void projectDataDirectoryIsResolvedFromNestedWorkingDirectory() throws Exception {
        Path projectRoot = tempDir.resolve("project");
        Path nestedDirectory = projectRoot.resolve("src").resolve("main").resolve("java");
        Files.createDirectories(nestedDirectory);
        Files.createFile(projectRoot.resolve("pom.xml"));

        Path resolved = AppPaths.resolveDataDirectory(null, null, nestedDirectory);

        assertEquals(projectRoot.resolve("data").toAbsolutePath().normalize(), resolved);
    }

    @Test
    void missingConfigurationAndProjectRootThrowsClearError() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> AppPaths.resolveDataDirectory(null, null, tempDir));

        assertTrue(exception.getMessage().contains("Unable to resolve the application data directory."));
    }
}
