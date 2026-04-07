package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.dao.impl.ApplicantDaoImpl;
import com.bupt.tarecruit.model.Applicant;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicantDaoTest {
    @Test
    void saveAndFindByUserId() throws Exception {
        Path file = Files.createTempFile("applicants", ".json");
        ApplicantDao dao = new ApplicantDaoImpl(file);

        Applicant applicant = new Applicant();
        applicant.setUserId("user-1");
        applicant.setFullName("Alice Chen");
        applicant.setPhone("+86 138000");
        applicant.setStudentId("20260001");
        applicant.setProgramme("CS");
        applicant.setSkills(List.of("Java", "Teaching"));
        applicant.setPreferredWorkingDays(List.of("Monday", "Wednesday"));
        applicant.setUpdatedAt(Instant.now());
        dao.save(applicant);

        assertTrue(dao.findByUserId("user-1").isPresent());
        assertEquals("Alice Chen", dao.findByUserId("user-1").get().getFullName());
        assertEquals(List.of("Java", "Teaching"), dao.findByUserId("user-1").get().getSkills());
        assertEquals(List.of("Monday", "Wednesday"), dao.findByUserId("user-1").get().getPreferredWorkingDays());
    }

    @Test
    void saveUpdatesExistingRecord() throws Exception {
        Path file = Files.createTempFile("applicants", ".json");
        ApplicantDao dao = new ApplicantDaoImpl(file);

        Applicant applicant = new Applicant();
        applicant.setUserId("user-1");
        applicant.setFullName("Alice");
        applicant.setUpdatedAt(Instant.now());
        dao.save(applicant);

        applicant.setFullName("Alice Updated");
        dao.save(applicant);

        assertEquals(1, dao.findAll().size());
        assertEquals("Alice Updated", dao.findByUserId("user-1").get().getFullName());
    }
}
