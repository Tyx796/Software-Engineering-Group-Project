package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.dao.impl.CvDaoImpl;
import com.bupt.tarecruit.model.CV;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CvDaoTest {
    @Test
    void saveAndFindByUserId() throws Exception {
        Path file = Files.createTempFile("cvs", ".json");
        CvDao dao = new CvDaoImpl(file);

        CV cv = new CV();
        cv.setUserId("user-1");
        cv.setFileName("cv.pdf");
        cv.setFileType("pdf");
        cv.setUploadedAt(Instant.now());
        dao.save(cv);

        Optional<CV> found = dao.findByUserId("user-1");
        assertTrue(found.isPresent());
        assertEquals("cv.pdf", found.get().getFileName());
        assertEquals("pdf", found.get().getFileType());
    }

    @Test
    void saveReplacesExistingCv() throws Exception {
        Path file = Files.createTempFile("cvs", ".json");
        CvDao dao = new CvDaoImpl(file);

        CV first = new CV();
        first.setUserId("user-1");
        first.setFileName("cv.pdf");
        first.setFileType("pdf");
        first.setUploadedAt(Instant.now());
        dao.save(first);

        CV second = new CV();
        second.setUserId("user-1");
        second.setFileName("cv.docx");
        second.setFileType("docx");
        second.setUploadedAt(Instant.now());
        dao.save(second);

        Optional<CV> found = dao.findByUserId("user-1");
        assertTrue(found.isPresent());
        assertEquals("cv.docx", found.get().getFileName());
    }

    @Test
    void findByUserIdReturnsEmptyWhenNotFound() throws Exception {
        Path file = Files.createTempFile("cvs", ".json");
        CvDao dao = new CvDaoImpl(file);

        assertTrue(dao.findByUserId("nonexistent").isEmpty());
    }
}
