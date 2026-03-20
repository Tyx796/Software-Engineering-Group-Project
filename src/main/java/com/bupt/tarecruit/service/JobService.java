package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.util.AppPaths;
import com.bupt.tarecruit.util.JsonStorage;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JobService {
    private final Path jobsFile;

    public JobService() {
        this(AppPaths.dataDirectory().resolve("jobs.json"));
    }

    public JobService(final Path jobsFile) {
        this.jobsFile = jobsFile;
    }

    public Job createJob(final String organiserUserId, final String title, final String department, final String description,
            final String requirementsText, final int hoursPerWeek, final LocalDate deadline) {
        validate(title, department, description, requirementsText, hoursPerWeek, deadline);
        List<Job> jobs = getAllJobs();
        Job job = Job.create(organiserUserId);
        job.setTitle(title.trim());
        job.setDepartment(department.trim());
        job.setDescription(description.trim());
        job.setRequirements(parseRequirements(requirementsText));
        job.setHoursPerWeek(hoursPerWeek);
        job.setDeadline(deadline);
        jobs.add(job);
        JsonStorage.writeList(jobsFile, jobs);
        return job;
    }

    public List<Job> getAvailableJobs() {
        return getAllJobs().stream()
                .filter(job -> "OPEN".equals(job.getStatus()))
                .filter(job -> !job.getDeadline().isBefore(LocalDate.now()))
                .sorted((left, right) -> left.getDeadline().compareTo(right.getDeadline()))
                .collect(Collectors.toList());
    }

    public List<Job> getJobsByOrganiser(final String organiserUserId) {
        return getAllJobs().stream().filter(job -> job.getOrganiserUserId().equals(organiserUserId)).toList();
    }

    public Optional<Job> findById(final String jobId) {
        return getAllJobs().stream().filter(job -> job.getId().equals(jobId)).findFirst();
    }

    public List<Job> getAllJobs() {
        return JsonStorage.readList(jobsFile, new TypeToken<>() { });
    }

    private List<String> parseRequirements(final String requirementsText) {
        return Arrays.stream(requirementsText.split("\\r?\\n|,"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();
    }

    private void validate(final String title, final String department, final String description,
            final String requirementsText, final int hoursPerWeek, final LocalDate deadline) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Job title is required.");
        }
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("Department is required.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Job description is required.");
        }
        if (requirementsText == null || requirementsText.isBlank()) {
            throw new IllegalArgumentException("At least one requirement is required.");
        }
        if (hoursPerWeek <= 0) {
            throw new IllegalArgumentException("Hours per week must be greater than zero.");
        }
        if (deadline == null || deadline.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline must be today or later.");
        }
    }
}
