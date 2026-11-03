package com.skillshare.model;

/**
 * Represents a student registered in the SkillShare platform.
 */
public class Student {
    private int studentId;
    private String name;
    private String email;
    private String passwordHash;
    private String department;

    public Student() {
    }

    public Student(String name, String email, String passwordHash, String department) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.department = department;
    }

    public Student(int studentId, String name, String email, String passwordHash, String department) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.department = department;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return name + " (" + department + ")";
    }
}
