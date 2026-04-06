package com.bupt.tarecruit.dao.impl;

import com.bupt.tarecruit.dao.JobDao;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.util.FileStorageUtil;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JobDaoImpl implements JobDao {
    private final Path jobsFile;

    public JobDaoImpl() {
        this(FileStorageUtil.dataDirectory().resolve("jobs.json"));
    }

    public JobDaoImpl(final Path jobsFile) {
        this.jobsFile = jobsFile;
    }

    @Override
    public List<Job> findAll() {
        return FileStorageUtil.readList(jobsFile, new TypeToken<>() { });
    }

    @Override
    public Optional<Job> findById(final String id) {
        return findAll().stream().filter(job -> job.getId().equals(id)).findFirst();
    }

    @Override
    public List<Job> findByOrganiserId(final String organiserUserId) {
        return findAll().stream().filter(job -> job.getOrganiserUserId().equals(organiserUserId)).toList();
    }

    @Override
    public void save(final Job job) {
        List<Job> jobs = new ArrayList<>(findAll());
        jobs.removeIf(existing -> existing.getId().equals(job.getId()));
        jobs.add(job);
        saveAll(jobs);
    }

    @Override
    public void saveAll(final List<Job> jobs) {
        FileStorageUtil.writeList(jobsFile, jobs);
    }
}
