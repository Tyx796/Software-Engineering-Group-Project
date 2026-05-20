package com.bupt.tarecruit.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class OrganiserApplicationFiltersContractTest {
    @Test
    void organiserApplicationListExposesFilterControls() throws Exception {
        String content = Files.readString(Path.of("src/main/webapp/jsp/organiser/job_applications.jsp"));

        assertContains(content, "name=\"status\"");
        assertContains(content, "name=\"keyword\"");
        assertContains(content, "name=\"sort\"");
        assertContains(content, "No applications match the current filters.");
    }

    private void assertContains(final String content, final String expectedSnippet) {
        assertTrue(content.contains(expectedSnippet),
                () -> "organiser/job_applications.jsp should contain: " + expectedSnippet);
    }
}
