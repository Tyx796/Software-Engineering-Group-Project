package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.ApplicationDao;
import com.bupt.tarecruit.dao.JobDao;
import com.bupt.tarecruit.dao.impl.ApplicationDaoImpl;
import com.bupt.tarecruit.dao.impl.JobDaoImpl;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.MessageType;
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
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private final JobDao jobDao;
    private final ApplicationDao applicationDao;
    private final MessageService messageService;

    public JobService() {
        this(new JobDaoImpl(), new ApplicationDaoImpl(), new MessageService());
    }

    public JobService(final Path jobsFile) {
        this(new JobDaoImpl(jobsFile), new ApplicationDaoImpl(), new MessageService());
    }

    public JobService(final JobDao jobDao) {
        this(jobDao, new ApplicationDaoImpl(), new MessageService());
    }

    public JobService(final JobDao jobDao, final ApplicationDao applicationDao, final MessageService messageService) {
        this.jobDao = jobDao;
        this.applicationDao = applicationDao;
        this.messageService = messageService;
    }

    public Job createJob(final String organiserUserId, final String title, final String department,
            final String description, final String requirementsText, final int hoursPerWeek, final LocalDate deadline) {
        validateJobData(title, department, description, requirementsText, hoursPerWeek, deadline);
        Job job = Job.create(organiserUserId);
        applyJobData(job, title, department, description, requirementsText, hoursPerWeek, deadline);
        jobDao.save(job);
        return job;
    }

    public Job updateJobForOrganiser(final String organiserUserId, final String jobId, final String title,
            final String department, final String description, final String requirementsText,
            final int hoursPerWeek, final LocalDate deadline) {
        Job job = getOwnedJobForOrganiser(organiserUserId, jobId);
        if (STATUS_CANCELLED.equals(job.getStatus())) {
            throw new IllegalArgumentException("Cancelled jobs cannot be edited.");
        }
        validateJobData(title, department, description, requirementsText, hoursPerWeek, deadline);
        applyJobData(job, title, department, description, requirementsText, hoursPerWeek, deadline);
        jobDao.save(job);
        return job;
    }

    public Job cancelJobForOrganiser(final String organiserUserId, final String jobId) {
        Job job = getOwnedJobForOrganiser(organiserUserId, jobId);
        if (STATUS_CANCELLED.equals(job.getStatus())) {
            throw new IllegalArgumentException("This job has already been cancelled.");
        }
        job.setStatus(STATUS_CANCELLED);
        jobDao.save(job);
        cancelApplicationsAndNotifyApplicants(job);
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
                .filter(job -> STATUS_OPEN.equals(job.getStatus()))
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

    public Job getOwnedJobForOrganiser(final String organiserUserId, final String jobId) {
        DataValidator.validateRequired(organiserUserId, "Organiser user ID");
        DataValidator.validateRequired(jobId, "Job ID");
        Job job = findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("The selected job does not exist."));
        if (!organiserUserId.equals(job.getOrganiserUserId())) {
            throw new IllegalArgumentException("You are not allowed to access this job.");
        }
        return job;
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

    public String formatRequirements(final List<String> requirements) {
        if (requirements == null || requirements.isEmpty()) {
            return "";
        }
        return String.join("\n", requirements);
    }

    private void applyJobData(final Job job, final String title, final String department, final String description,
            final String requirementsText, final int hoursPerWeek, final LocalDate deadline) {
        job.setTitle(title.trim());
        job.setDepartment(department.trim());
        job.setDescription(description.trim());
        job.setRequirements(parseRequirements(requirementsText));
        setWorkloadAndDeadline(job, hoursPerWeek, deadline);
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

    private void cancelApplicationsAndNotifyApplicants(final Job job) {
        List<Application> applications = applicationDao.findByJobId(job.getId()).stream()
                .map(application -> {
                    application.setStatus(ApplicationStatus.CANCELLED);
                    return application;
                })
                .toList();
        if (!applications.isEmpty()) {
            applicationDao.saveAll(applications);
            applications.stream()
                    .collect(Collectors.toMap(
                            Application::getApplicantUserId,
                            application -> application,
                            (left, right) -> left))
                    .values()
                    .forEach(application -> messageService.sendMessage(
                            job.getOrganiserUserId(),
                            application.getApplicantUserId(),
                            "Job Posting Cancelled",
                            "The organiser has cancelled the job posting " + job.getTitle() + ".",
                            MessageType.JOB_CANCELLED,
                            application.getId(),
                            job.getId()));
        }
    }
}
