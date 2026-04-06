package com.bupt.tarecruit.dao.impl;

import com.bupt.tarecruit.dao.ApplicantDao;
import com.bupt.tarecruit.model.Applicant;
import com.bupt.tarecruit.util.FileStorageUtil;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplicantDaoImpl implements ApplicantDao {
    private final Path applicantsFile;

    public ApplicantDaoImpl() {
        this(FileStorageUtil.dataDirectory().resolve("applicants.json"));
    }

    public ApplicantDaoImpl(final Path applicantsFile) {
        this.applicantsFile = applicantsFile;
    }

    @Override
    public List<Applicant> findAll() {
        return FileStorageUtil.readList(applicantsFile, new TypeToken<>() { });
    }

    @Override
    public Optional<Applicant> findByUserId(final String userId) {
        return findAll().stream().filter(applicant -> applicant.getUserId().equals(userId)).findFirst();
    }

    @Override
    public void save(final Applicant applicant) {
        List<Applicant> applicants = new ArrayList<>(findAll());
        applicants.removeIf(existing -> existing.getUserId().equals(applicant.getUserId()));
        applicants.add(applicant);
        saveAll(applicants);
    }

    @Override
    public void saveAll(final List<Applicant> applicants) {
        FileStorageUtil.writeList(applicantsFile, applicants);
    }
}
