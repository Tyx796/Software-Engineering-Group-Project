package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.dao.impl.ApplicantLimitPolicyDaoImpl;
import com.bupt.tarecruit.model.ApplicantLimitPolicy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicantLimitPolicyDaoTest {
    @Test
    void saveAndFindByUserId() throws Exception {
        Path file = Files.createTempFile("applicant-limit-policies", ".json");
        ApplicantLimitPolicyDao dao = new ApplicantLimitPolicyDaoImpl(file);

        ApplicantLimitPolicy policy = new ApplicantLimitPolicy();
        policy.setUserId("user-1");
        policy.setApplicationLimitOverride(5);
        policy.setUpdatedAt(Instant.now());
        dao.save(policy);

        assertTrue(dao.findByUserId("user-1").isPresent());
        assertEquals(5, dao.findByUserId("user-1").orElseThrow().getApplicationLimitOverride());
    }

    @Test
    void saveUpdatesExistingPolicy() throws Exception {
        Path file = Files.createTempFile("applicant-limit-policies", ".json");
        ApplicantLimitPolicyDao dao = new ApplicantLimitPolicyDaoImpl(file);

        ApplicantLimitPolicy policy = new ApplicantLimitPolicy();
        policy.setUserId("user-1");
        policy.setApplicationLimitOverride(4);
        policy.setUpdatedAt(Instant.now());
        dao.save(policy);

        policy.setApplicationLimitOverride(6);
        dao.save(policy);

        assertEquals(1, dao.findAll().size());
        assertEquals(6, dao.findByUserId("user-1").orElseThrow().getApplicationLimitOverride());
    }
}
