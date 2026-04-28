package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.Job;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobDaoTest {
    @Test
    void saveAndFindById() throws Exception {
        Path file = Files.createTempFile("jobs", ".json");
        JobDao dao = new JobDaoImpl(file);

        Job job = Job.create("org-1");
        job.setTitle("Test TA");
        job.setDepartment("CS");
        job.setDescription("Testing");
        job.setAssistantQuota(3);
        job.setHoursPerWeek(10);
        job.setDeadline(LocalDate.now().plusDays(7));
        dao.save(job);

        assertTrue(dao.findById(job.getId()).isPresent());
        assertEquals("Test TA", dao.findById(job.getId()).get().getTitle());
        assertEquals(3, dao.findById(job.getId()).get().getAssistantQuota());
    }

    @Test
    void findByOrganiserIdFiltersCorrectly() throws Exception {
        Path file = Files.createTempFile("jobs", ".json");
        JobDao dao = new JobDaoImpl(file);

        Job job1 = Job.create("org-1");
        job1.setTitle("Job 1");
        job1.setDeadline(LocalDate.now().plusDays(7));
        dao.save(job1);

        Job job2 = Job.create("org-2");
        job2.setTitle("Job 2");
        job2.setDeadline(LocalDate.now().plusDays(7));
        dao.save(job2);

        assertEquals(1, dao.findByOrganiserId("org-1").size());
        assertEquals(1, dao.findByOrganiserId("org-2").size());
    }

    @Test
    void legacyJobWithoutAssistantQuotaUsesCompatibilityDefault() throws Exception {
        Path file = Files.createTempFile("jobs-legacy", ".json");
        Files.writeString(file, """
                [
                  {
                    "id": "J-legacy",
                    "organiserUserId": "org-1",
                    "title": "Legacy Job",
                    "department": "CS",
                    "description": "Old record",
                    "requirements": ["Java"],
                    "hoursPerWeek": 10,
                    "deadline": "2099-01-01",
                    "status": "OPEN",
                    "createdAt": "2026-01-01T00:00:00Z"
                  }
                ]
                """, StandardCharsets.UTF_8);
        JobDao dao = new JobDaoImpl(file);

        Job job = dao.findById("J-legacy").orElseThrow();

        assertEquals(1, job.getAssistantQuota());
        assertEquals("Legacy Job", job.getTitle());
    }
}
