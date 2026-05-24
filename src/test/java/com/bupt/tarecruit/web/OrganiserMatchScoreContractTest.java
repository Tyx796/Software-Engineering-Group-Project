package com.bupt.tarecruit.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class OrganiserMatchScoreContractTest {
    @Test
    void organiserApplicationListExposesMatchScoreColumn() throws Exception {
        String content = Files.readString(Path.of("src/main/webapp/jsp/organiser/job_applications.jsp"));

        assertContains(content, "<th>Match Score</th>");
        assertContains(content, "view.skillMatch.matchScore");
        assertContains(content, "% match");
    }

    private void assertContains(final String content, final String expectedSnippet) throws IOException {
        assertTrue(content.contains(expectedSnippet),
                () -> "organiser/job_applications.jsp should contain: " + expectedSnippet);
    }
}
