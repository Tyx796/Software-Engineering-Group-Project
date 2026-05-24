package com.bupt.tarecruit.model;

public class JobRecommendationView {
    private Job job;
    private SkillMatchView skillMatch;

    public Job getJob() { return job; }
    public void setJob(final Job job) { this.job = job; }
    public SkillMatchView getSkillMatch() { return skillMatch; }
    public void setSkillMatch(final SkillMatchView skillMatch) { this.skillMatch = skillMatch; }

    public int getMatchScore() {
        return skillMatch == null ? 0 : skillMatch.getMatchScore();
    }
}
