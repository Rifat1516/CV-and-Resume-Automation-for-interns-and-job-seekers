package com.cvmatcher.model;

public class Language {
    private int id;
    private int cvId;
    private String name;
    private String proficiency;

    public Language() {}

    public Language(String name, String proficiency) {
        this.name = name;
        this.proficiency = proficiency;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCvId() { return cvId; }
    public void setCvId(int cvId) { this.cvId = cvId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProficiency() { return proficiency; }
    public void setProficiency(String proficiency) { this.proficiency = proficiency; }
}
