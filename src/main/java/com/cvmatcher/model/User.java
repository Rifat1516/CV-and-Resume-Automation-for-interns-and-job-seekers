package com.cvmatcher.model;

public class User {
    private int id;
    private String fullName;
    private String phone;
    private String passwordHash;
    private Integer age;
    private String city;
    private Role role;

    public User() {}

    public User(int id, String fullName, String phone, String passwordHash,
                Integer age, String city, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.age = age;
        this.city = city;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isAdmin() { return role == Role.ADMIN; }
}
