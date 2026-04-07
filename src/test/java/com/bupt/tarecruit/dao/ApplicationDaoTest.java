package com.bupt.tarecruit.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationStatus;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ApplicationDaoTest {
    @Test
    void saveAndQueryApplicationByIdApplicantAndJob() throws Exception {
        Path file = Files.createTempFile("applications", ".json");
        ApplicationDao dao = new ApplicationDaoImpl(file);

        Application application = new Application();
        application.setId("APP-1");
        application.setApplicantUserId("user-1");
        application.setJobId("job-1");
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(Instant.now());

        dao.save(application);

        assertTrue(dao.findById("APP-1").isPresent());
        assertEquals(1, dao.findByApplicantId("user-1").size());
        assertEquals(1, dao.findByJobId("job-1").size());
        assertEquals(ApplicationStatus.PENDING, dao.findById("APP-1").orElseThrow().getStatus());
    }

    @Test
    void saveUpdatesExistingApplication() throws Exception {
        Path file = Files.createTempFile("applications", ".json");
        ApplicationDao dao = new ApplicationDaoImpl(file);

        Application application = new Application();
        application.setId("APP-1");
        application.setApplicantUserId("user-1");
        application.setJobId("job-1");
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(Instant.now());
        dao.save(application);

        application.setStatus(ApplicationStatus.REVIEWING);
        dao.save(application);

        assertEquals(1, dao.findAll().size());
        assertEquals(ApplicationStatus.REVIEWING, dao.findById("APP-1").orElseThrow().getStatus());
    }
}
