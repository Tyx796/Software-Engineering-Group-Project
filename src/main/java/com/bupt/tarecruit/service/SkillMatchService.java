package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.Applicant;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.SkillMatchView;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates deterministic, explainable skill match results between an applicant
 * profile and a job posting.
 *
 * <p>The current implementation is intentionally rule-based: skills and
 * requirements are trimmed, lower-cased for comparison, deduplicated, and then
 * reported as matched or missing display values. This keeps the result
 * transparent for coursework demonstration and viva questions.</p>
 */
public class SkillMatchService {
    /**
     * Calculates matched skills, missing skills, and a percentage match score.
     */
    public SkillMatchView calculateMatch(final Applicant applicant, final Job job) {
        if (job == null) {
            throw new IllegalArgumentException("Job is required.");
        }

        Map<String, String> applicantSkillsByKey = normalizeSkills(applicant == null ? List.of() : applicant.getSkills());
        Map<String, String> requirementsByKey = normalizeSkills(job.getRequirements());

        SkillMatchView view = new SkillMatchView();
        if (requirementsByKey.isEmpty()) {
            view.setMatchScore(100);
            return view;
        }

        List<String> matchedSkills = requirementsByKey.entrySet().stream()
                .filter(entry -> applicantSkillsByKey.containsKey(entry.getKey()))
                .map(Map.Entry::getValue)
                .toList();
        List<String> missingSkills = requirementsByKey.entrySet().stream()
                .filter(entry -> !applicantSkillsByKey.containsKey(entry.getKey()))
                .map(Map.Entry::getValue)
                .toList();

        view.setMatchedSkills(matchedSkills);
        view.setMissingSkills(missingSkills);
        view.setMatchScore((matchedSkills.size() * 100) / requirementsByKey.size());
        return view;
    }

    private Map<String, String> normalizeSkills(final List<String> rawSkills) {
        Map<String, String> normalized = new LinkedHashMap<>();
        if (rawSkills == null) {
            return normalized;
        }
        for (String rawSkill : rawSkills) {
            if (rawSkill == null) {
                continue;
            }
            String displayValue = rawSkill.trim();
            if (displayValue.isEmpty()) {
                continue;
            }
            normalized.putIfAbsent(displayValue.toLowerCase(), displayValue);
        }
        return normalized;
    }
}
