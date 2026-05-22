package com.bupt.tarecruit.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class LocalDeploymentScriptContractTest {
    @Test
    void localRunScriptExposesBuildDeployAndStartFlow() throws Exception {
        Path scriptPath = Path.of("run-local.ps1");

        assertTrue(Files.exists(scriptPath), "run-local.ps1 should exist at the repository root.");

        String content = Files.readString(scriptPath);
        assertContains(content, "setenv.bat");
        assertContains(content, "startup.bat");
        assertContains(content, "shutdown.bat");
        assertContains(content, "TARECRUIT_DATA_DIR");
        assertContains(content, "http://localhost");
        assertContains(content, "mvn clean package");
        assertContains(content, "Test-PathSafe");
        assertContains(content, "UnauthorizedAccessException");
        assertContains(content, "Get-TomcatServiceName");
        assertContains(content, "Stop-Service");
        assertContains(content, "Start-Service");
        assertContains(content, "Administrator");
    }

    private void assertContains(final String content, final String expectedSnippet) {
        assertTrue(content.contains(expectedSnippet),
                () -> "run-local.ps1 should contain: " + expectedSnippet);
    }
}
