package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.model.Job;
import java.util.List;
import java.util.Optional;

public interface JobDao {
    List<Job> findAll();
    Optional<Job> findById(String id);
    List<Job> findByOrganiserId(String organiserUserId);
    void save(Job job);
    void saveAll(List<Job> jobs);
}
