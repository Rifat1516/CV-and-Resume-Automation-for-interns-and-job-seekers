package com.cvmatcher.service;

import com.cvmatcher.model.Certification;
import com.cvmatcher.model.Cv;
import com.cvmatcher.model.CvHighlight;
import com.cvmatcher.model.CvHighlightCategory;
import com.cvmatcher.model.Education;
import com.cvmatcher.model.Experience;
import com.cvmatcher.model.Language;
import com.cvmatcher.model.Skill;

/**
 * Builder pattern: a Cv has several optional, variable-length sections.
 * This assembles a valid Cv step by step and validates once at build().
 */
public class CvBuilder {

    private final Cv cv = new Cv();

    public CvBuilder forUser(int userId) {
        cv.setUserId(userId);
        return this;
    }

    public CvBuilder withFullName(String fullName) {
        cv.setFullName(fullName);
        return this;
    }

    public CvBuilder withSummary(String summary) {
        cv.setSummary(summary);
        return this;
    }

    public CvBuilder withEmail(String email) {
        cv.setEmail(email);
        return this;
    }

    public CvBuilder addLanguage(Language language) {
        cv.getLanguages().add(language);
        return this;
    }

    public CvBuilder addLanguage(String name, String proficiency) {
        return addLanguage(new Language(name, proficiency));
    }

    public CvBuilder addAward(String description) {
        cv.getHighlights().add(new CvHighlight(CvHighlightCategory.AWARD, description));
        return this;
    }

    public CvBuilder addAffiliation(String description) {
        cv.getHighlights().add(new CvHighlight(CvHighlightCategory.AFFILIATION, description));
        return this;
    }

    public CvBuilder addEducation(Education education) {
        cv.getEducationList().add(education);
        return this;
    }

    public CvBuilder addExperience(Experience experience) {
        cv.getExperienceList().add(experience);
        return this;
    }

    public CvBuilder addSkill(Skill skill) {
        cv.getSkills().add(skill);
        return this;
    }

    public CvBuilder addSkill(String name) {
        return addSkill(new Skill(name));
    }

    public CvBuilder addCertification(Certification certification) {
        cv.getCertifications().add(certification);
        return this;
    }

    public Cv build() {
        if (cv.getUserId() == 0) {
            throw new IllegalStateException("A CV must belong to a user");
        }
        if (cv.getFullName() == null || cv.getFullName().isBlank()) {
            throw new IllegalStateException("A CV must have a full name");
        }
        return cv;
    }
}
