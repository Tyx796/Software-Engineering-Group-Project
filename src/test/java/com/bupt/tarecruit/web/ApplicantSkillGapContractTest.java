package com.bupt.tarecruit.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ApplicantSkillGapContractTest {
    @Test
    void applicantJobDetailExposesMatchedAndMissingSkills() throws Exception {
        String content = Files.readString(Path.of("src/main/webapp/jsp/applicant/job_detail.jsp"));

        assertContains(content, "skillMatch.matchedSkills");
        assertContains(content, "skillMatch.missingSkills");
        assertContains(content, "Complete your profile to see skill match insights before applying.");
        assertContains(content, "You can still apply even if some required skills are missing.");
    }

    private void assertContains(final String content, final String expectedSnippet) {
        assertTrue(content.contains(expectedSnippet),
                () -> "applicant/job_detail.jsp should contain: " + expectedSnippet);
    }
}
