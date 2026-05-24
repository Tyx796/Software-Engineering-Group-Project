package com.bupt.tarecruit.web;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class FrontendModernizationContractTest {
    @Test
    void sharedShellExposesModernWorkspaceStructure() throws Exception {
        String headerContent = read("src/main/webapp/jsp/common/header.jsp");
        String navigationContent = read("src/main/webapp/jsp/common/navigation.jsp");

        assertContains(headerContent, "pageSection");
        assertContains(headerContent, "app-shell");
        assertContains(headerContent, "shell-body");
        assertContains(navigationContent, "workspace-nav");
        assertContains(navigationContent, "workspace-nav-link");
        assertContains(navigationContent, "pageSection == ");
    }

    @Test
    void loginPagePresentsSplitSigninWorkspace() throws Exception {
        String content = read("src/main/webapp/jsp/login.jsp");
        String authVisualContent = read("src/main/webapp/jsp/common/auth_visual.jsp");
        String stylesContent = read("src/main/webapp/css/styles.css");

        assertContains(content, "pageLayout");
        assertContains(content, "auth-grid");
        assertContains(content, "auth-hero");
        assertContains(content, "auth-panel");
        assertContains(content, "Sign in");
        assertContains(authVisualContent, "auth-hero-art");
        assertContains(authVisualContent, "auth-hero-art-media");
        assertContains(stylesContent, ".auth-hero-art");
        assertContains(stylesContent, ".auth-hero-art-media");
    }

    @Test
    void applicantJobsPageExposesDashboardSections() throws Exception {
        String content = read("src/main/webapp/jsp/applicant/job_list.jsp");

        assertContains(content, "pageSection");
        assertContains(content, "jobs-toolbar");
        assertContains(content, "jobs-highlight-grid");
        assertContains(content, "job-grid");
        assertContains(content, "job-surface");
    }

    @Test
    void organiserAndAdminPagesExposeWorkspaceTablesAndMetrics() throws Exception {
        String organiserContent = read("src/main/webapp/jsp/organiser/job_list.jsp");
        String adminContent = read("src/main/webapp/jsp/admin/home.jsp");

        assertContains(organiserContent, "pageSection");
        assertContains(organiserContent, "workspace-card");
        assertContains(organiserContent, "workspace-table");
        assertContains(adminContent, "pageSection");
        assertContains(adminContent, "dashboard-grid");
        assertContains(adminContent, "metric-card");
        assertContains(adminContent, "insight-panel");
    }

    private String read(final String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }

    private void assertContains(final String content, final String expectedSnippet) {
        assertTrue(content.contains(expectedSnippet),
                () -> "Expected snippet not found: " + expectedSnippet);
    }
}
