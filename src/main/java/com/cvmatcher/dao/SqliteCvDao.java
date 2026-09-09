package com.cvmatcher.dao;

import com.cvmatcher.model.Certification;
import com.cvmatcher.model.Cv;
import com.cvmatcher.model.CvHighlight;
import com.cvmatcher.model.CvHighlightCategory;
import com.cvmatcher.model.Education;
import com.cvmatcher.model.Experience;
import com.cvmatcher.model.Language;
import com.cvmatcher.model.Skill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Persists a Cv and its child sections (education, experience, skills,
 * certifications) as one transactional unit.
 */
public class SqliteCvDao implements CvDao {

    private final Connection conn = DatabaseManager.getInstance().getConnection();

    @Override
    public Cv save(Cv cv) {
        try {
            conn.setAutoCommit(false);

            String sql = "INSERT INTO cvs (user_id, full_name, email, summary) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, cv.getUserId());
                ps.setString(2, cv.getFullName());
                ps.setString(3, cv.getEmail());
                ps.setString(4, cv.getSummary());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) cv.setId(keys.getInt(1));
                }
            }

            for (Education e : cv.getEducationList()) insertEducation(cv.getId(), e);
            for (Experience e : cv.getExperienceList()) insertExperience(cv.getId(), e);
            for (Skill s : cv.getSkills()) insertSkill(cv.getId(), s);
            for (Certification c : cv.getCertifications()) insertCertification(cv.getId(), c);
            for (Language l : cv.getLanguages()) insertLanguage(cv.getId(), l);
            for (CvHighlight h : cv.getHighlights()) insertHighlight(cv.getId(), h);

            conn.commit();
            return cv;
        } catch (SQLException e) {
            rollbackQuietly();
            throw new RuntimeException("Failed to save CV", e);
        } finally {
            resetAutoCommitQuietly();
        }
    }

    @Override
    public Optional<Cv> findByUserId(int userId) {
        String sql = "SELECT * FROM cvs WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Cv cv = new Cv();
                cv.setId(rs.getInt("id"));
                cv.setUserId(rs.getInt("user_id"));
                cv.setFullName(rs.getString("full_name"));
                cv.setEmail(rs.getString("email"));
                cv.setSummary(rs.getString("summary"));
                loadEducation(cv);
                loadExperience(cv);
                loadSkills(cv);
                loadCertifications(cv);
                loadLanguages(cv);
                loadHighlights(cv);
                return Optional.of(cv);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load CV", e);
        }
    }

    private void insertEducation(int cvId, Education e) throws SQLException {
        String sql = "INSERT INTO education (cv_id, institution, degree, field_of_study, start_year, end_year) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cvId);
            ps.setString(2, e.getInstitution());
            ps.setString(3, e.getDegree());
            ps.setString(4, e.getFieldOfStudy());
            setNullableInt(ps, 5, e.getStartYear());
            setNullableInt(ps, 6, e.getEndYear());
            ps.executeUpdate();
        }
    }

    private void insertExperience(int cvId, Experience e) throws SQLException {
        String sql = "INSERT INTO experience (cv_id, company, title, description, start_year, end_year) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cvId);
            ps.setString(2, e.getCompany());
            ps.setString(3, e.getTitle());
            ps.setString(4, e.getDescription());
            setNullableInt(ps, 5, e.getStartYear());
            setNullableInt(ps, 6, e.getEndYear());
            ps.executeUpdate();
        }
    }

    private void insertSkill(int cvId, Skill s) throws SQLException {
        String sql = "INSERT INTO skills (cv_id, name) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cvId);
            ps.setString(2, s.getName());
            ps.executeUpdate();
        }
    }

    private void insertCertification(int cvId, Certification c) throws SQLException {
        String sql = "INSERT INTO certifications (cv_id, name, issuer, year) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cvId);
            ps.setString(2, c.getName());
            ps.setString(3, c.getIssuer());
            setNullableInt(ps, 4, c.getYear());
            ps.executeUpdate();
        }
    }

    private void insertLanguage(int cvId, Language l) throws SQLException {
        String sql = "INSERT INTO languages (cv_id, name, proficiency) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cvId);
            ps.setString(2, l.getName());
            ps.setString(3, l.getProficiency());
            ps.executeUpdate();
        }
    }

    private void insertHighlight(int cvId, CvHighlight h) throws SQLException {
        String sql = "INSERT INTO cv_highlights (cv_id, category, description) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cvId);
            ps.setString(2, h.getCategory().name());
            ps.setString(3, h.getDescription());
            ps.executeUpdate();
        }
    }

    private void loadEducation(Cv cv) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM education WHERE cv_id = ?")) {
            ps.setInt(1, cv.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Education e = new Education(rs.getString("institution"), rs.getString("degree"),
                            rs.getString("field_of_study"), (Integer) rs.getObject("start_year"), (Integer) rs.getObject("end_year"));
                    e.setId(rs.getInt("id"));
                    e.setCvId(cv.getId());
                    cv.getEducationList().add(e);
                }
            }
        }
    }

    private void loadExperience(Cv cv) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM experience WHERE cv_id = ?")) {
            ps.setInt(1, cv.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Experience e = new Experience(rs.getString("company"), rs.getString("title"),
                            rs.getString("description"), (Integer) rs.getObject("start_year"), (Integer) rs.getObject("end_year"));
                    e.setId(rs.getInt("id"));
                    e.setCvId(cv.getId());
                    cv.getExperienceList().add(e);
                }
            }
        }
    }

    private void loadSkills(Cv cv) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM skills WHERE cv_id = ?")) {
            ps.setInt(1, cv.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Skill s = new Skill(rs.getString("name"));
                    s.setId(rs.getInt("id"));
                    s.setCvId(cv.getId());
                    cv.getSkills().add(s);
                }
            }
        }
    }

    private void loadCertifications(Cv cv) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM certifications WHERE cv_id = ?")) {
            ps.setInt(1, cv.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Certification c = new Certification(rs.getString("name"), rs.getString("issuer"), (Integer) rs.getObject("year"));
                    c.setId(rs.getInt("id"));
                    c.setCvId(cv.getId());
                    cv.getCertifications().add(c);
                }
            }
        }
    }

    private void loadLanguages(Cv cv) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM languages WHERE cv_id = ?")) {
            ps.setInt(1, cv.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Language l = new Language(rs.getString("name"), rs.getString("proficiency"));
                    l.setId(rs.getInt("id"));
                    l.setCvId(cv.getId());
                    cv.getLanguages().add(l);
                }
            }
        }
    }

    private void loadHighlights(Cv cv) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM cv_highlights WHERE cv_id = ?")) {
            ps.setInt(1, cv.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CvHighlight h = new CvHighlight(
                            CvHighlightCategory.valueOf(rs.getString("category")),
                            rs.getString("description")
                    );
                    h.setId(rs.getInt("id"));
                    h.setCvId(cv.getId());
                    cv.getHighlights().add(h);
                }
            }
        }
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) ps.setNull(index, java.sql.Types.INTEGER);
        else ps.setInt(index, value);
    }

    private void rollbackQuietly() {
        try { conn.rollback(); } catch (SQLException ignored) {}
    }

    private void resetAutoCommitQuietly() {
        try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
    }
}
