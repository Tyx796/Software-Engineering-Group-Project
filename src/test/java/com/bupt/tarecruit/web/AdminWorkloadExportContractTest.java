package com.bupt.tarecruit.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class AdminWorkloadExportContractTest {
    @Test
    void exportServletSetsCsvResponseHeadersAndWorkloadPageLinksToIt() throws Exception {
        String servletContent = Files.readString(Path.of("src/main/java/com/bupt/tarecruit/servlet/AdminWorkloadExportServlet.java"));
        String jspContent = Files.readString(Path.of("src/main/webapp/jsp/admin/workloads.jsp"));

        assertContains(servletContent, "text/csv;charset=UTF-8");
        assertContains(servletContent, "attachment; filename=");
        assertContains(jspContent, "/admin/workloads/export");
        assertContains(jspContent, "Export to CSV");
    }

    private void assertContains(final String content, final String expectedSnippet) {
        assertTrue(content.contains(expectedSnippet),
                () -> "Expected snippet not found: " + expectedSnippet);
    }
}
