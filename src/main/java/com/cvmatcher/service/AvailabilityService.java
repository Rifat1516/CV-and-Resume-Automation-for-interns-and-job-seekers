package com.cvmatcher.service;

import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.JobPosition;

public interface AvailabilityService {
    void updateFilledPositions(JobNotice notice, int filledPositions);
    void updateDescription(JobNotice notice, String description);
    void addPosition(JobNotice notice, String roleName, int totalCount);
    void updatePositionFilledCount(JobPosition position, int filledCount);
}
