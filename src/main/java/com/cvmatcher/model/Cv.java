package com.cvmatcher.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A Cv is constructed via CvBuilder (Builder pattern) rather than
 * a large constructor, since most of its sections are optional and
 * variable in length -- modeled after a real resume layout (summary,
 * work experience, education, skills, certificates, languages,
 * awards/affiliations).
 */
public class Cv {
    private int id;
    private int userId;
    private String fullName;
    private String email;
    private String summary;
    private final List<Education> educationList = new ArrayList<>();
    private final List<Experience> experienceList = new ArrayList<>();
    private final List<Skill> skills = new ArrayList<>();
    private final List<Certification> certifications = new ArrayList<>();
    private final List<Language> languages = new ArrayList<>();
    private final List<CvHighlight> highlights = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<Education> getEducationList() { return educationList; }
    public List<Experience> getExperienceList() { return experienceList; }
    public List<Skill> getSkills() { return skills; }
    public List<Certification> getCertifications() { return certifications; }
    public List<Language> getLanguages() { return languages; }
    public List<CvHighlight> getHighlights() { return highlights; }

    public List<CvHighlight> getAwards() {
        return highlights.stream()
                .filter(h -> h.getCategory() == CvHighlightCategory.AWARD)
                .toList();
    }

    public List<CvHighlight> getAffiliations() {
        return highlights.stream()
                .filter(h -> h.getCategory() == CvHighlightCategory.AFFILIATION)
                .toList();
    }

    /** Convenience used heavily by the scoring/matching services and duplicate/gap checks. */
    public String toSearchableText() {
        StringBuilder sb = new StringBuilder();
        if (summary != null) sb.append(summary).append(" ");
        for (Skill s : skills) sb.append(s.getName()).append(" ");
        for (Experience e : experienceList) {
            sb.append(e.getTitle()).append(" ");
            if (e.getDescription() != null) sb.append(e.getDescription()).append(" ");
        }
        for (Education e : educationList) {
            sb.append(e.getDegree()).append(" ").append(e.getFieldOfStudy()).append(" ");
        }
        for (Certification c : certifications) sb.append(c.getName()).append(" ");
        for (Language l : languages) sb.append(l.getName()).append(" ");
        for (CvHighlight h : highlights) sb.append(h.getDescription()).append(" ");
        return sb.toString();
    }
}
