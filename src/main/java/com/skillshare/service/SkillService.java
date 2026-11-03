package com.skillshare.service;

import com.skillshare.dao.SkillDAO;
import com.skillshare.model.Skill;
import com.skillshare.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Handles business logic for adding, viewing, and deleting skills.
 */
public class SkillService {
    private final SkillDAO skillDAO;

    public SkillService() {
        this.skillDAO = new SkillDAO();
    }

    public SkillService(SkillDAO skillDAO) {
        this.skillDAO = skillDAO;
    }

    public Skill addSkill(int studentId, String skillName, String skillType) throws Exception {
        if (!ValidationUtil.isNotEmpty(skillName)) {
            throw new IllegalArgumentException("Skill name cannot be empty.");
        }
        if (!"TEACH".equalsIgnoreCase(skillType) && !"LEARN".equalsIgnoreCase(skillType)) {
            throw new IllegalArgumentException("Skill type must be either TEACH or LEARN.");
        }

        String normalizedName = skillName.trim();
        String normalizedType = skillType.trim().toUpperCase();

        if (skillDAO.hasSkill(studentId, normalizedName, normalizedType)) {
            throw new IllegalArgumentException("You have already added '" + normalizedName + "' to your " +
                    (normalizedType.equals("TEACH") ? "teaching" : "learning") + " skills.");
        }

        return skillDAO.addSkill(studentId, normalizedName, normalizedType);
    }

    public List<Skill> getTeachingSkills(int studentId) throws SQLException {
        return skillDAO.getSkillsByStudentAndType(studentId, "TEACH");
    }

    public List<Skill> getLearningSkills(int studentId) throws SQLException {
        return skillDAO.getSkillsByStudentAndType(studentId, "LEARN");
    }

    public List<Skill> getAllSkills(int studentId) throws SQLException {
        return skillDAO.getAllSkillsByStudent(studentId);
    }

    public boolean deleteSkill(int skillId, int studentId) throws SQLException {
        return skillDAO.deleteSkill(skillId, studentId);
    }

    public int countTeachingSkills(int studentId) throws SQLException {
        return skillDAO.countSkills(studentId, "TEACH");
    }

    public int countLearningSkills(int studentId) throws SQLException {
        return skillDAO.countSkills(studentId, "LEARN");
    }

    public List<Skill> getRecentSkills(int limit) throws SQLException {
        return skillDAO.getRecentSkills(limit);
    }
}
