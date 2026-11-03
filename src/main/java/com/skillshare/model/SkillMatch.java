package com.skillshare.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a match found between students who can teach and learn complementary skills.
 */
public class SkillMatch {
    private int studentId;
    private String studentName;
    private String department;
    private String matchingSkill; // The skill they can teach that the current user wants to learn
    private List<String> allTeachSkills = new ArrayList<>();
    private List<String> allLearnSkills = new ArrayList<>();
    private boolean mutualMatch; // True if current user also teaches something they want to learn
    private boolean requestAlreadySent;

    public SkillMatch() {
    }

    public SkillMatch(int studentId, String studentName, String department, String matchingSkill) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.department = department;
        this.matchingSkill = matchingSkill;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getMatchingSkill() {
        return matchingSkill;
    }

    public void setMatchingSkill(String matchingSkill) {
        this.matchingSkill = matchingSkill;
    }

    public List<String> getAllTeachSkills() {
        return allTeachSkills;
    }

    public void setAllTeachSkills(List<String> allTeachSkills) {
        this.allTeachSkills = allTeachSkills;
    }

    public List<String> getAllLearnSkills() {
        return allLearnSkills;
    }

    public void setAllLearnSkills(List<String> allLearnSkills) {
        this.allLearnSkills = allLearnSkills;
    }

    public boolean isMutualMatch() {
        return mutualMatch;
    }

    public void setMutualMatch(boolean mutualMatch) {
        this.mutualMatch = mutualMatch;
    }

    public boolean isRequestAlreadySent() {
        return requestAlreadySent;
    }

    public void setRequestAlreadySent(boolean requestAlreadySent) {
        this.requestAlreadySent = requestAlreadySent;
    }
}
