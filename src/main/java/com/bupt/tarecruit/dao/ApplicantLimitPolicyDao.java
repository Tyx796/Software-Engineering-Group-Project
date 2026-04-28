package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.model.ApplicantLimitPolicy;
import java.util.List;
import java.util.Optional;

public interface ApplicantLimitPolicyDao {
    List<ApplicantLimitPolicy> findAll();
    Optional<ApplicantLimitPolicy> findByUserId(String userId);
    void save(ApplicantLimitPolicy policy);
    void saveAll(List<ApplicantLimitPolicy> policies);
}
