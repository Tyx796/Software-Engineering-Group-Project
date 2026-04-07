package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class JobServiceTest {
    @Test
    void availableJobsExcludeExpiredOnes() throws Exception {
        Path file = Files.createTempFile("jobs", ".json");
        JobService service = new JobService(file);
        service.createJob("org-1", "Fresh", "CS", "desc", "Java", 10, LocalDate.now().plusDays(3));
        service.createJob("org-1", "Later", "CS", "desc", "SQL", 8, LocalDate.now().plusDays(10));

        assertEquals(2, service.getAvailableJobs().size());
        assertEquals("Fresh", service.getAvailableJobs().get(0).getTitle());
    }
}
