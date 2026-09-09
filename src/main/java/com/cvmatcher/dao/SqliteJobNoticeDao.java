package com.cvmatcher.dao;

import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.JobPosition;
import com.cvmatcher.model.JobRequirement;
import com.cvmatcher.model.RequirementCategory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Persists a JobNotice and its child responsibility/requirement bullets as
 * one transactional unit, the same pattern SqliteCvDao uses for a CV's
 * child sections.
 */
public class SqliteJobNoticeDao implements JobNoticeDao {

    private final Connection conn = DatabaseManager.getInstance().getConnection();

    @Override
    public JobNotice save(JobNotice notice) {
        String sql = "INSERT INTO job_notices " +
                "(company, title, location, salary, experience_required, description, " +
                " total_positions, filled_positions, deadline, contact_email, uploaded_by_user_id) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, notice.getCompany());
                ps.setString(2, notice.getTitle());
                ps.setString(3, notice.getLocation());
                ps.setString(4, notice.getSalary());
                ps.setString(5, notice.getExperienceRequired());
                ps.setString(6, notice.getDescription());
                ps.setInt(7, notice.getTotalPositions());
                ps.setInt(8, notice.getFilledPositions());
                ps.setString(9, notice.getDeadline());
                ps.setString(10, notice.getContactEmail());
                ps.setInt(11, notice.getUploadedByUserId());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) notice.setId(keys.getInt(1));
                }
            }
            for (JobRequirement r : notice.getRequirements()) {
                insertRequirement(notice.getId(), r);
            }
            for (JobPosition p : notice.getPositions()) {
                insertPosition(notice.getId(), p);
            }
            conn.commit();
            return notice;
        } catch (SQLException e) {
            rollbackQuietly();
            throw new RuntimeException("Failed to save job notice", e);
        } finally {
            resetAutoCommitQuietly();
        }
    }

    private void insertRequirement(int jobNoticeId, JobRequirement r) throws SQLException {
        String sql = "INSERT INTO job_requirements (job_notice_id, category, description) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jobNoticeId);
            ps.setString(2, r.getCategory().name());
            ps.setString(3, r.getDescription());
            ps.executeUpdate();
        }
    }

    private void insertPosition(int jobNoticeId, JobPosition p) throws SQLException {
        String sql = "INSERT INTO job_positions (job_notice_id, role_name, total_count, filled_count) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, jobNoticeId);
            ps.setString(2, p.getRoleName());
            ps.setInt(3, p.getTotalCount());
            ps.setInt(4, p.getFilledCount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
            p.setJobNoticeId(jobNoticeId);
        }
    }

    @Override
    public List<JobNotice> findAll() {
        List<JobNotice> results = new ArrayList<>();
        String sql = "SELECT * FROM job_notices ORDER BY created_at DESC";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) results.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list job notices", e);
        }
        for (JobNotice n : results) {
            loadRequirements(n);
            loadPositions(n);
        }
        return results;
    }

    @Override
    public Optional<JobNotice> findById(int id) {
        String sql = "SELECT * FROM job_notices WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    JobNotice notice = map(rs);
                    loadRequirements(notice);
                    loadPositions(notice);
                    return Optional.of(notice);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find job notice", e);
        }
        return Optional.empty();
    }

    @Override
    public List<JobNotice> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        String placeholders = ids.stream().map(i -> "?").collect(Collectors.joining(","));
        String sql = "SELECT * FROM job_notices WHERE id IN (" + placeholders + ")";
        List<JobNotice> results = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) ps.setInt(i + 1, ids.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find job notices by ids", e);
        }
        for (JobNotice n : results) {
            loadRequirements(n);
            loadPositions(n);
        }
        return results;
    }

    @Override
    public void updateFilledPositions(int noticeId, int filledPositions) {
        String sql = "UPDATE job_notices SET filled_positions = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filledPositions);
            ps.setInt(2, noticeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update filled positions", e);
        }
    }

    @Override
    public void updateDescription(int noticeId, String description) {
        String sql = "UPDATE job_notices SET description = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setInt(2, noticeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update description", e);
        }
    }

    @Override
    public void addPosition(int jobNoticeId, JobPosition position) {
        try {
            insertPosition(jobNoticeId, position);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add job position", e);
        }
    }

    @Override
    public void updatePositionFilledCount(int positionId, int filledCount) {
        String sql = "UPDATE job_positions SET filled_count = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, filledCount);
            ps.setInt(2, positionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update position filled count", e);
        }
    }

    @Override
    public List<JobPosition> findAllPositions() {
        List<JobPosition> results = new ArrayList<>();
        String sql = "SELECT * FROM job_positions ORDER BY job_notice_id";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                JobPosition p = new JobPosition(
                        rs.getString("role_name"),
                        rs.getInt("total_count"),
                        rs.getInt("filled_count")
                );
                p.setId(rs.getInt("id"));
                p.setJobNoticeId(rs.getInt("job_notice_id"));
                results.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list job positions", e);
        }
        return results;
    }

    private void loadPositions(JobNotice notice) {
        String sql = "SELECT * FROM job_positions WHERE job_notice_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notice.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JobPosition p = new JobPosition(
                            rs.getString("role_name"),
                            rs.getInt("total_count"),
                            rs.getInt("filled_count")
                    );
                    p.setId(rs.getInt("id"));
                    p.setJobNoticeId(notice.getId());
                    notice.getPositions().add(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load job positions", e);
        }
    }

    private void loadRequirements(JobNotice notice) {
        String sql = "SELECT * FROM job_requirements WHERE job_notice_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notice.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JobRequirement r = new JobRequirement(
                            RequirementCategory.valueOf(rs.getString("category")),
                            rs.getString("description")
                    );
                    r.setId(rs.getInt("id"));
                    r.setJobNoticeId(notice.getId());
                    notice.getRequirements().add(r);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load job requirements", e);
        }
    }

    private JobNotice map(ResultSet rs) throws SQLException {
        JobNotice n = new JobNotice(
                rs.getString("company"),
                rs.getString("title"),
                rs.getInt("total_positions"),
                rs.getInt("uploaded_by_user_id")
        );
        n.setId(rs.getInt("id"));
        n.setLocation(rs.getString("location"));
        n.setSalary(rs.getString("salary"));
        n.setExperienceRequired(rs.getString("experience_required"));
        n.setDescription(rs.getString("description"));
        n.setFilledPositions(rs.getInt("filled_positions"));
        n.setDeadline(rs.getString("deadline"));
        n.setContactEmail(rs.getString("contact_email"));
        return n;
    }

    private void rollbackQuietly() {
        try { conn.rollback(); } catch (SQLException ignored) {}
    }

    private void resetAutoCommitQuietly() {
        try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
    }
}
