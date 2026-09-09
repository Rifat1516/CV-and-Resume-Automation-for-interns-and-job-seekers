package com.cvmatcher.model;

public class Skill {
    private int id;
    private int cvId;
    private String name;

    public Skill() {}

    public Skill(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCvId() { return cvId; }
    public void setCvId(int cvId) { this.cvId = cvId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
