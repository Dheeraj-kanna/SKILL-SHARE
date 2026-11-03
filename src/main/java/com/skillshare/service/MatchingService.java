package com.skillshare.service;

import com.skillshare.config.DBConnection;
import com.skillshare.dao.ExchangeDAO;
import com.skillshare.dao.SkillDAO;
import com.skillshare.model.Skill;
import com.skillshare.model.SkillMatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Intelligent matching engine connecting students who can teach skills another student wants to learn.
 */
public class MatchingService {
    private final SkillDAO skillDAO;
    private final ExchangeDAO exchangeDAO;

    public MatchingService() {
        this.skillDAO = new SkillDAO();
        this.exchangeDAO = new ExchangeDAO();
    }

    public List<SkillMatch> findMatches(int currentStudentId) throws SQLException {
        // Query matches: other students who TEACH a skill that the current user wants to LEARN
        String sql = "SELECT s.student_id, s.name, s.department, sk.skill_name " +
                     "FROM students s " +
                     "JOIN skills sk ON s.student_id = sk.student_id " +
                     "WHERE sk.skill_type = 'TEACH' " +
                     "AND LOWER(sk.skill_name) IN " +
                     "(SELECT LOWER(skill_name) FROM skills WHERE student_id = ? AND skill_type = 'LEARN') " +
                     "AND s.student_id != ? " +
                     "ORDER BY s.name, sk.skill_name";

        List<SkillMatch> matches = new ArrayList<>();
        Map<Integer, SkillMatch> matchMap = new HashMap<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, currentStudentId);
            ps.setInt(2, currentStudentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    String studentName = rs.getString("name");
                    String department = rs.getString("department");
                    String skillName = rs.getString("skill_name");

                    SkillMatch match = matchMap.get(studentId);
                    if (match == null) {
                        match = new SkillMatch(studentId, studentName, department, skillName);
                        matchMap.put(studentId, match);
                        matches.add(match);
                    } else {
                        // Append additional matching skill
                        match.setMatchingSkill(match.getMatchingSkill() + ", " + skillName);
                    }
                }
            }
        }

        // Fetch detailed skills list and check mutual match & existing request status
        List<Skill> currentUserTeach = skillDAO.getSkillsByStudentAndType(currentStudentId, "TEACH");

        for (SkillMatch m : matches) {
            List<Skill> otherSkills = skillDAO.getAllSkillsByStudent(m.getStudentId());
            for (Skill s : otherSkills) {
                if (s.isTeach()) {
                    m.getAllTeachSkills().add(s.getSkillName());
                } else if (s.isLearn()) {
                    m.getAllLearnSkills().add(s.getSkillName());
                    // Check if current user can teach what they want to learn (mutual match!)
                    for (Skill userTeach : currentUserTeach) {
                        if (userTeach.getSkillName().equalsIgnoreCase(s.getSkillName())) {
                            m.setMutualMatch(true);
                            break;
                        }
                    }
                }
            }

            // Check if current user already sent request for any of the matching skills
            String[] skills = m.getMatchingSkill().split(", ");
            for (String singleSkill : skills) {
                if (exchangeDAO.hasExistingExchange(currentStudentId, m.getStudentId(), singleSkill)) {
                    m.setRequestAlreadySent(true);
                    break;
                }
            }
        }

        return matches;
    }
}
