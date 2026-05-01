package com.bupt.tarecruit.dao.impl;

import com.bupt.tarecruit.dao.ApplicantLimitPolicyDao;
import com.bupt.tarecruit.model.ApplicantLimitPolicy;
import com.bupt.tarecruit.util.FileStorageUtil;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplicantLimitPolicyDaoImpl implements ApplicantLimitPolicyDao {
    private final Path policiesFile;

    public ApplicantLimitPolicyDaoImpl() {
        this(FileStorageUtil.dataDirectory().resolve("applicant_limit_policies.json"));
    }

    public ApplicantLimitPolicyDaoImpl(final Path policiesFile) {
        this.policiesFile = policiesFile;
    }

    @Override
    public List<ApplicantLimitPolicy> findAll() {
        return FileStorageUtil.readList(policiesFile, new TypeToken<>() { });
    }

    @Override
    public Optional<ApplicantLimitPolicy> findByUserId(final String userId) {
        return findAll().stream().filter(policy -> policy.getUserId().equals(userId)).findFirst();
    }

    @Override
    public void save(final ApplicantLimitPolicy policy) {
        List<ApplicantLimitPolicy> policies = new ArrayList<>(findAll());
        policies.removeIf(existing -> existing.getUserId().equals(policy.getUserId()));
        policies.add(policy);
        saveAll(policies);
    }

    @Override
    public void saveAll(final List<ApplicantLimitPolicy> policies) {
        FileStorageUtil.writeList(policiesFile, policies);
    }

    @Override
    public void deleteByUserId(final String userId) {
        List<ApplicantLimitPolicy> policies = new ArrayList<>(findAll());
        policies.removeIf(existing -> existing.getUserId().equals(userId));
        saveAll(policies);
    }
}