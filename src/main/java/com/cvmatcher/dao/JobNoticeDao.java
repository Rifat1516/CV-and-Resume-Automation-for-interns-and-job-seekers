package com.cvmatcher.dao;

import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.JobPosition;
import java.util.List;
import java.util.Optional;

public interface JobNoticeDao {
    JobNotice save(JobNotice notice);
    List<JobNotice> findAll();
    Optional<JobNotice> findById(int id);
    List<JobNotice> findByIds(List<Integer> ids);
    void updateFilledPositions(int noticeId, int filledPositions);
    void updateDescription(int noticeId, String description);
    void addPosition(int jobNoticeId, JobPosition position);
    void updatePositionFilledCount(int positionId, int filledCount);
    List<JobPosition> findAllPositions();
}
