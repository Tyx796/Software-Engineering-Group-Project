package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.model.Application;
import java.util.List;
import java.util.Optional;

public interface ApplicationDao {
    List<Application> findAll();
    Optional<Application> findById(String id);
    List<Application> findByApplicantId(String applicantUserId);
    List<Application> findByJobId(String jobId);
    void save(Application application);
    void saveAll(List<Application> applications);
}
