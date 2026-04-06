package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.model.Applicant;
import java.util.List;
import java.util.Optional;

public interface ApplicantDao {
    List<Applicant> findAll();
    Optional<Applicant> findByUserId(String userId);
    void save(Applicant applicant);
    void saveAll(List<Applicant> applicants);
}
