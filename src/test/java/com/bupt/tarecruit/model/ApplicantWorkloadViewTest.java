package com.bupt.tarecruit.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicantWorkloadViewTest {
    @Test
    void workloadWarningUsesStrictGreaterThanThreshold() {
        ApplicantWorkloadView view = new ApplicantWorkloadView();
        view.setWorkloadThreshold(20);
        view.setTotalHoursPerWeek(20);

        assertFalse(view.isOverloaded());
        assertEquals("Within threshold", view.getWorkloadStatusLabel());
    }

    @Test
    void workloadWarningMessageIncludesThresholdAndCurrentHours() {
        ApplicantWorkloadView view = new ApplicantWorkloadView();
        view.setWorkloadThreshold(20);
        view.setTotalHoursPerWeek(24);

        assertTrue(view.isOverloaded());
        assertEquals("Overloaded", view.getWorkloadStatusLabel());
        assertEquals("24 hours/week exceeds the 20-hour threshold.", view.getWorkloadAlertMessage());
    }
}
