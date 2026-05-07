package com.bupt.tarecruit.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ApplicantRecommendationsContractTest {
    @Test
    void applicantJobListExposesRecommendedJobsSection() throws Exception {
        String content = Files.readString(Path.of("src/main/webapp/jsp/applicant/job_list.jsp"));

        assertContains(content, "Recommended for you");
        assertContains(content, "recommendedJobs");
        assertContains(content, "match");
    }

    private void assertContains(final String content, final String expectedSnippet) {
        assertTrue(content.contains(expectedSnippet),
                () -> "applicant/job_list.jsp should contain: " + expectedSnippet);
    }
}
