package com.skillshare.model;

/**
 * Represents a skill offered or wanted by a student.
 * skillType is typically "TEACH" or "LEARN".
 */
public class Skill {
    private int skillId;
    private int studentId;
    private String skillName;
    private String skillType; // "TEACH" or "LEARN"

    public Skill() {
    }

    public Skill(int studentId, String skillName, String skillType) {
        this.studentId = studentId;
        this.skillName = skillName;
        this.skillType = skillType;
    }

    public Skill(int skillId, int studentId, String skillName, String skillType) {
        this.skillId = skillId;
        this.studentId = studentId;
        this.skillName = skillName;
        this.skillType = skillType;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public boolean isTeach() {
        return "TEACH".equalsIgnoreCase(skillType);
    }

    public boolean isLearn() {
        return "LEARN".equalsIgnoreCase(skillType);
    }

    @Override
    public String toString() {
        return skillName + " (" + skillType + ")";
    }
}
