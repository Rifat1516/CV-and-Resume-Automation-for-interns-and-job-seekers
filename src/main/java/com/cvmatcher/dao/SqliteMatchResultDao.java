package com.cvmatcher.dao;

import com.cvmatcher.model.GapRecommendation;
import com.cvmatcher.model.MatchResult;
import com.cvmatcher.model.RankLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteMatchResultDao implements MatchResultDao {

    private final Connection conn = DatabaseManager.getInstance().getConnection();

    /**
     * Upserts the score/rank, then replaces the gap list entirely -- ranking
     * is re-run every time a user opens the matches screen, so this must be
     * idempotent rather than appending duplicate gap rows on every re-rank.
     */
    @Override
    public MatchResult save(MatchResult result) {
        String upsertSql = "INSERT INTO match_results (user_id, job_notice_id, score, rank_level) VALUES (?,?,?,?) " +
                "ON CONFLICT(user_id, job_notice_id) DO UPDATE SET score = excluded.score, rank_level = excluded.rank_level";
        try {
            try (PreparedStatement ps = conn.prepareStatement(upsertSql)) {
                ps.setInt(1, result.getUserId());
                ps.setInt(2, result.getJobNoticeId());
                ps.setDouble(3, result.getScore());
                ps.setString(4, result.getRankLevel().name());
                ps.executeUpdate();
            }

            int matchResultId = findId(result.getUserId(), result.getJobNoticeId());
            result.setId(matchResultId);

            deleteGaps(matchResultId);
            for (GapRecommendation gap : result.getGaps()) {
                insertGap(matchResultId, gap);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save match result", e);
        }
    }

    private int findId(int userId, int jobNoticeId) throws SQLException {
        String sql = "SELECT id FROM match_results WHERE user_id = ? AND job_notice_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, jobNoticeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        throw new SQLException("match_results row not found after upsert");
    }

    private void deleteGaps(int matchResultId) throws SQLException {
        String sql = "DELETE FROM gap_recommendations WHERE match_result_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, matchResultId);
            ps.executeUpdate();
        }
    }

    private void insertGap(int matchResultId, GapRecommendation gap) throws SQLException {
        String sql = "INSERT INTO gap_recommendations (match_result_id, missing_item, suggestion) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, matchResultId);
            ps.setString(2, gap.getMissingItem());
            ps.setString(3, gap.getSuggestion());
            ps.executeUpdate();
        }
    }

    @Override
    public List<MatchResult> findByUserId(int userId) {
        List<MatchResult> results = new ArrayList<>();
        String sql = "SELECT * FROM match_results WHERE user_id = ? ORDER BY score DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MatchResult m = new MatchResult(rs.getInt("user_id"), rs.getInt("job_notice_id"), rs.getDouble("score"));
                    m.setId(rs.getInt("id"));
                    m.setRankLevel(RankLevel.valueOf(rs.getString("rank_level")));
                    results.add(m);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load match results", e);
        }
        return results;
    }
}
