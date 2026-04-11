package com.bupt.tarecruit.util;

import com.bupt.tarecruit.model.ApplicationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationStatusViewTest {
    @Test
    void badgeClassMappingCoversAllStatuses() {
        assertEquals("app-status-pending", ApplicationStatusView.badgeClassFor(ApplicationStatus.PENDING));
        assertEquals("app-status-reviewing", ApplicationStatusView.badgeClassFor(ApplicationStatus.REVIEWING));
        assertEquals("app-status-accepted", ApplicationStatusView.badgeClassFor(ApplicationStatus.ACCEPTED));
        assertEquals("app-status-rejected", ApplicationStatusView.badgeClassFor(ApplicationStatus.REJECTED));
        assertEquals("app-status-withdrawn", ApplicationStatusView.badgeClassFor(ApplicationStatus.WITHDRAWN));
        assertEquals("app-status-cancelled", ApplicationStatusView.badgeClassFor(ApplicationStatus.CANCELLED));
    }

    @Test
    void summaryMappingCoversAllStatuses() {
        assertEquals("Waiting for organiser review.", ApplicationStatusView.summaryFor(ApplicationStatus.PENDING));
        assertEquals("An organiser is reviewing your application.", ApplicationStatusView.summaryFor(ApplicationStatus.REVIEWING));
        assertEquals("Your application has been accepted.", ApplicationStatusView.summaryFor(ApplicationStatus.ACCEPTED));
        assertEquals("Your application has been rejected.", ApplicationStatusView.summaryFor(ApplicationStatus.REJECTED));
        assertEquals("You withdrew this application.", ApplicationStatusView.summaryFor(ApplicationStatus.WITHDRAWN));
        assertEquals(
                "This application was cancelled because the job was cancelled.",
                ApplicationStatusView.summaryFor(ApplicationStatus.CANCELLED));
    }

    @Test
    void nullStatusFallsBackToSafeDefaults() {
        assertEquals("app-status-pending", ApplicationStatusView.badgeClassFor(null));
        assertEquals("Application status is currently unavailable.", ApplicationStatusView.summaryFor(null));
    }
}