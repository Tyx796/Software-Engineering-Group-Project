package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.JobDao;
import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.util.DataValidator;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class JobService {
    private final JobDao jobDao;

    public JobService() {
        this(new JobDaoImpl());
    }

    public JobService(final Path jobsFile) {
        this(new JobDaoImpl(jobsFile));
    }

    public JobService(final JobDao jobDao) {
        this.jobDao = jobDao;
    }

    public Job createJob(final String organiserUserId, final String title, final String department,
            final String description, final String requirementsText, final int hoursPerWeek, final LocalDate deadline) {
        validateJobData(title, department, description, requirementsText, hoursPerWeek, deadline);
        Job job = Job.create(organiserUserId);
        job.setTitle(title.trim());
        job.setDepartment(department.trim());
        job.setDescription(description.trim());
        job.setRequirements(parseRequirements(requirementsText));
        setWorkloadAndDeadline(job, hoursPerWeek, deadline);
        jobDao.save(job);
        return job;
    }

    public Job saveJob(final Job job) {
        jobDao.save(job);
        return job;
    }

    public List<Job> getAvailableJobs() {
        return sortJobs(filterExpiredJobs(getAllJobs()));
    }

    public List<Job> searchAvailableJobs(final String keyword) {
        List<Job> availableJobs = getAvailableJobs();
        if (keyword == null || keyword.isBlank()) {
            return availableJobs;
        }
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        return availableJobs.stream()
                .filter(job -> matchesKeyword(job, normalizedKeyword))
                .collect(Collectors.toList());
    }

    public List<Job> filterExpiredJobs(final List<Job> jobs) {
        return jobs.stream()
                .filter(job -> "OPEN".equals(job.getStatus()))
                .filter(job -> !job.getDeadline().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public List<Job> sortJobs(final List<Job> jobs) {
        return jobs.stream()
                .sorted(Comparator.comparing(Job::getDeadline))
                .collect(Collectors.toList());
    }

    public Optional<Job> getJobDetails(final String jobId) {
        return findById(jobId);
    }

    public boolean validateJobExists(final String jobId) {
        return jobDao.findById(jobId).isPresent();
    }

    public void setRequirements(final Job job, final List<String> requirements) {
        job.setRequirements(requirements);
    }

    public void setWorkloadAndDeadline(final Job job, final int hoursPerWeek, final LocalDate deadline) {
        job.setHoursPerWeek(hoursPerWeek);
        job.setDeadline(deadline);
    }

    public List<Job> getJobsByOrganiser(final String organiserUserId) {
        return jobDao.findByOrganiserId(organiserUserId);
    }

    public Optional<Job> findById(final String jobId) {
        return jobDao.findById(jobId);
    }

    public List<Job> getAllJobs() {
        return jobDao.findAll();
    }

    public void validateJobData(final String title, final String department, final String description,
            final String requirementsText, final int hoursPerWeek, final LocalDate deadline) {
        DataValidator.validateRequired(title, "Job title");
        DataValidator.validateRequired(department, "Department");
        DataValidator.validateRequired(description, "Job description");
        DataValidator.validateRequired(requirementsText, "Requirements");
        DataValidator.validatePositive(hoursPerWeek, "Hours per week");
        DataValidator.validateDeadline(deadline);
    }

    private List<String> parseRequirements(final String requirementsText) {
        return Arrays.stream(requirementsText.split("\\r?\\n|,"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();
    }

    private boolean matchesKeyword(final Job job, final String normalizedKeyword) {
        return containsIgnoreCase(job.getTitle(), normalizedKeyword)
                || containsIgnoreCase(job.getDepartment(), normalizedKeyword)
                || job.getRequirements().stream()
                        .anyMatch(requirement -> containsIgnoreCase(requirement, normalizedKeyword));
    }

    private boolean containsIgnoreCase(final String value, final String normalizedKeyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedKeyword);
    }
}
