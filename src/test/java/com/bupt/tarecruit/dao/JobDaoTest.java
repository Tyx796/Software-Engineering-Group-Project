package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.Job;
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
        job.setHoursPerWeek(10);
        job.setDeadline(LocalDate.now().plusDays(7));
        dao.save(job);

        assertTrue(dao.findById(job.getId()).isPresent());
        assertEquals("Test TA", dao.findById(job.getId()).get().getTitle());
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
}
