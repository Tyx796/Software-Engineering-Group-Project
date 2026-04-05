package com.bupt.tarecruit.dao.impl;

import com.bupt.tarecruit.dao.ApplicationDao;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.util.FileStorageUtil;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApplicationDaoImpl implements ApplicationDao {
    private final Path applicationsFile;

    public ApplicationDaoImpl() {
        this(FileStorageUtil.dataDirectory().resolve("applications.json"));
    }

    public ApplicationDaoImpl(final Path applicationsFile) {
        this.applicationsFile = applicationsFile;
    }

    @Override
    public List<Application> findAll() {
        return FileStorageUtil.readList(applicationsFile, new TypeToken<>() { });
    }

    @Override
    public Optional<Application> findById(final String id) {
        return findAll().stream().filter(app -> app.getId().equals(id)).findFirst();
    }

    @Override
    public List<Application> findByApplicantId(final String applicantUserId) {
        return findAll().stream().filter(app -> app.getApplicantUserId().equals(applicantUserId)).toList();
    }

    @Override
    public List<Application> findByJobId(final String jobId) {
        return findAll().stream().filter(app -> app.getJobId().equals(jobId)).toList();
    }

    @Override
    public void save(final Application application) {
        List<Application> applications = new ArrayList<>(findAll());
        applications.removeIf(existing -> existing.getId().equals(application.getId()));
        applications.add(application);
        saveAll(applications);
    }

    @Override
    public void saveAll(final List<Application> applications) {
        FileStorageUtil.writeList(applicationsFile, applications);
    }
}
