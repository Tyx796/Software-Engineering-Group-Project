package com.bupt.tarecruit.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FormValidationContractTest {
    @Test
    void authenticationFormsExposeBootstrapValidationFeedback() throws Exception {
        assertFormHasValidation("src/main/webapp/jsp/login.jsp",
                "needs-validation",
                "type=\"email\"",
                "id=\"email\"",
                "Please enter your email address.",
                "Please enter your password.");

        assertFormHasValidation("src/main/webapp/jsp/register.jsp",
                "needs-validation",
                "type=\"email\"",
                "minlength=\"6\"",
                "Please choose a role.",
                "Please enter a username.",
                "Please enter a valid email address.",
                "Password must be at least 6 characters.",
                "Please confirm your password.");
    }

    @Test
    void applicantFormsExposeValidationFeedback() throws Exception {
        assertFormHasValidation("src/main/webapp/jsp/applicant/profile.jsp",
                "needs-validation",
                "pattern=\"[+0-9()\\-\\s]{6,20}\"",
                "Please enter your full name.",
                "Please enter a valid phone number.",
                "Please enter your student ID.",
                "Please enter your programme.");

        assertFormHasValidation("src/main/webapp/jsp/applicant/upload_cv.jsp",
                "needs-validation",
                "type=\"file\"",
                "accept=\".pdf,.doc,.docx\"",
                "Please choose a CV file in PDF, DOC, or DOCX format.");
    }

    @Test
    void organiserJobFormExposesValidationFeedback() throws Exception {
        assertFormHasValidation("src/main/webapp/jsp/organiser/job_form.jsp",
                "needs-validation",
                "type=\"number\"",
                "type=\"date\"",
                "Please enter the job title.",
                "Please enter the department.",
                "Please enter the job description.",
                "Please enter at least one requirement.",
                "Hours per week must be greater than zero.",
                "Assistant quota must be greater than zero.",
                "Please choose a deadline.");
    }

    @Test
    void adminFormsExposeValidationFeedback() throws Exception {
        assertFormHasValidation("src/main/webapp/jsp/admin/settings.jsp",
                "needs-validation",
                "type=\"number\"",
                "Default applicant application limit must be greater than zero.");

        assertFormHasValidation("src/main/webapp/jsp/admin/users.jsp",
                "needs-validation",
                "type=\"number\"",
                "Override must be zero or greater.");

        assertFormHasValidation("src/main/webapp/jsp/admin/workloads.jsp",
                "needs-validation",
                "type=\"number\"",
                "Workload threshold must be greater than zero.");
    }

    private void assertFormHasValidation(final String relativePath, final String... expectedSnippets)
            throws IOException {
        String content = Files.readString(Path.of(relativePath));
        for (String expectedSnippet : expectedSnippets) {
            assertTrue(content.contains(expectedSnippet),
                    () -> relativePath + " should contain: " + expectedSnippet);
        }
        assertTrue(content.contains("invalid-feedback"),
                () -> relativePath + " should expose visible validation feedback.");
    }
}
