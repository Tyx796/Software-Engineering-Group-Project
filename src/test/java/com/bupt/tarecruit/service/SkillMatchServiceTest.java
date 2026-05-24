package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.Applicant;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.SkillMatchView;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SkillMatchServiceTest {
    private final SkillMatchService service = new SkillMatchService();

    @Test
    void calculateMatchReturnsHundredForFullRequirementCoverage() {
        Applicant applicant = applicantWithSkills(" Java ", "communication", "Java");
        Job job = jobWithRequirements("Java", "Communication");

        SkillMatchView result = service.calculateMatch(applicant, job);

        assertEquals(100, result.getMatchScore());
        assertEquals(List.of("Java", "Communication"), result.getMatchedSkills());
        assertEquals(List.of(), result.getMissingSkills());
    }

    @Test
    void calculateMatchReturnsPartialScoreAndMissingSkills() {
        Applicant applicant = applicantWithSkills("SQL");
        Job job = jobWithRequirements("SQL", "Database Design", "Java");

        SkillMatchView result = service.calculateMatch(applicant, job);

        assertEquals(33, result.getMatchScore());
        assertEquals(List.of("SQL"), result.getMatchedSkills());
        assertEquals(List.of("Database Design", "Java"), result.getMissingSkills());
    }

    @Test
    void calculateMatchTreatsMissingApplicantSkillsAsZeroMatch() {
        Job job = jobWithRequirements("Python", "Machine Learning");

        SkillMatchView result = service.calculateMatch(null, job);

        assertEquals(0, result.getMatchScore());
        assertEquals(List.of(), result.getMatchedSkills());
        assertEquals(List.of("Python", "Machine Learning"), result.getMissingSkills());
    }

    @Test
    void calculateMatchTreatsJobsWithoutRequirementsAsFullyMatched() {
        Applicant applicant = applicantWithSkills("Python");
        Job job = jobWithRequirements();

        SkillMatchView result = service.calculateMatch(applicant, job);

        assertEquals(100, result.getMatchScore());
        assertEquals(List.of(), result.getMatchedSkills());
        assertEquals(List.of(), result.getMissingSkills());
    }

    @Test
    void calculateMatchRejectsNullJob() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateMatch(applicantWithSkills("Java"), null));

        assertEquals("Job is required.", exception.getMessage());
    }

    private Applicant applicantWithSkills(final String... skills) {
        Applicant applicant = new Applicant();
        applicant.setSkills(List.of(skills));
        return applicant;
    }

    private Job jobWithRequirements(final String... requirements) {
        Job job = Job.create("organiser-1");
        job.setRequirements(List.of(requirements));
        return job;
    }
}
