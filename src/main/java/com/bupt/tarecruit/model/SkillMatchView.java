package com.bupt.tarecruit.model;

import java.util.ArrayList;
import java.util.List;

public class SkillMatchView {
    private int matchScore;
    private List<String> matchedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();

    public int getMatchScore() { return matchScore; }
    public void setMatchScore(final int matchScore) { this.matchScore = matchScore; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(final List<String> matchedSkills) {
        this.matchedSkills = matchedSkills == null ? new ArrayList<>() : new ArrayList<>(matchedSkills);
    }
    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(final List<String> missingSkills) {
        this.missingSkills = missingSkills == null ? new ArrayList<>() : new ArrayList<>(missingSkills);
    }
}
