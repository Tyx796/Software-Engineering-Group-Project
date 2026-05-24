package com.bupt.tarecruit.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class AdminWorkloadWarningContractTest {
    @Test
    void adminWorkloadViewsUseSharedStatusAndWarningText() throws Exception {
        String workloadsContent = Files.readString(Path.of("src/main/webapp/jsp/admin/workloads.jsp"));
        String homeContent = Files.readString(Path.of("src/main/webapp/jsp/admin/home.jsp"));

        assertContains(workloadsContent, "view.workloadStatusLabel");
        assertContains(workloadsContent, "view.workloadAlertMessage");
        assertContains(homeContent, "view.workloadAlertMessage");
    }

    private void assertContains(final String content, final String expectedSnippet) {
        assertTrue(content.contains(expectedSnippet),
                () -> "Expected snippet not found: " + expectedSnippet);
    }
}
