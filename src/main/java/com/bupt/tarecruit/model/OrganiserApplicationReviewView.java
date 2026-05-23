package com.bupt.tarecruit.model;

public class OrganiserApplicationReviewView {
    private Application application;
    private Applicant applicant;
    private SkillMatchView skillMatch;

    public Application getApplication() { return application; }
    public void setApplication(final Application application) { this.application = application; }
    public Applicant getApplicant() { return applicant; }
    public void setApplicant(final Applicant applicant) { this.applicant = applicant; }
    public SkillMatchView getSkillMatch() { return skillMatch; }
    public void setSkillMatch(final SkillMatchView skillMatch) { this.skillMatch = skillMatch; }
}
