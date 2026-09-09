package com.cvmatcher.model;

public class Certification {
    private int id;
    private int cvId;
    private String name;
    private String issuer;
    private Integer year;

    public Certification() {}

    public Certification(String name, String issuer, Integer year) {
        this.name = name;
        this.issuer = issuer;
        this.year = year;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCvId() { return cvId; }
    public void setCvId(int cvId) { this.cvId = cvId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}
