package com.cvmatcher.service;

import com.cvmatcher.dao.JobNoticeDao;
import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.JobPosition;

/** Real implementation -- performs the actual write. Never called directly by the UI. */
public class AvailabilityServiceImpl implements AvailabilityService {

    private final JobNoticeDao jobNoticeDao;

    public AvailabilityServiceImpl(JobNoticeDao jobNoticeDao) {
        this.jobNoticeDao = jobNoticeDao;
    }

    @Override
    public void updateFilledPositions(JobNotice notice, int filledPositions) {
        if (filledPositions < 0 || filledPositions > notice.getTotalPositions()) {
            throw new IllegalArgumentException("filledPositions must be between 0 and totalPositions");
        }
        jobNoticeDao.updateFilledPositions(notice.getId(), filledPositions);
        notice.setFilledPositions(filledPositions);
    }

    @Override
    public void updateDescription(JobNotice notice, String description) {
        jobNoticeDao.updateDescription(notice.getId(), description);
        notice.setDescription(description);
    }

    @Override
    public void addPosition(JobNotice notice, String roleName, int totalCount) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("roleName is required");
        }
        if (totalCount < 1) {
            throw new IllegalArgumentException("totalCount must be at least 1");
        }
        JobPosition position = new JobPosition(roleName.trim(), totalCount, 0);
        jobNoticeDao.addPosition(notice.getId(), position);
        notice.getPositions().add(position);
    }

    @Override
    public void updatePositionFilledCount(JobPosition position, int filledCount) {
        if (filledCount < 0 || filledCount > position.getTotalCount()) {
            throw new IllegalArgumentException("filledCount must be between 0 and totalCount");
        }
        jobNoticeDao.updatePositionFilledCount(position.getId(), filledCount);
        position.setFilledCount(filledCount);
    }
}
