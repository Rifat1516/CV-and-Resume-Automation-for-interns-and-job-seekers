package com.cvmatcher.service;

import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.JobPosition;
import com.cvmatcher.model.User;

/**
 * Proxy pattern: the UI talks to this proxy, never to
 * AvailabilityServiceImpl directly. The proxy enforces that only an
 * ADMIN may edit availability, keeping that access-control rule in one
 * place instead of scattered role checks across controllers.
 */
public class AvailabilityServiceProxy implements AvailabilityService {

    private final AvailabilityService realService;
    private final User requestingUser;

    public AvailabilityServiceProxy(AvailabilityService realService, User requestingUser) {
        this.realService = realService;
        this.requestingUser = requestingUser;
    }

    @Override
    public void updateFilledPositions(JobNotice notice, int filledPositions) {
        if (!requestingUser.isAdmin()) {
            throw new SecurityException("Only an admin can edit job notice availability.");
        }
        realService.updateFilledPositions(notice, filledPositions);
    }

    @Override
    public void updateDescription(JobNotice notice, String description) {
        if (!requestingUser.isAdmin()) {
            throw new SecurityException("Only an admin can edit a job notice's description.");
        }
        realService.updateDescription(notice, description);
    }

    @Override
    public void addPosition(JobNotice notice, String roleName, int totalCount) {
        if (!requestingUser.isAdmin()) {
            throw new SecurityException("Only an admin can add a job position.");
        }
        realService.addPosition(notice, roleName, totalCount);
    }

    @Override
    public void updatePositionFilledCount(JobPosition position, int filledCount) {
        if (!requestingUser.isAdmin()) {
            throw new SecurityException("Only an admin can edit position availability.");
        }
        realService.updatePositionFilledCount(position, filledCount);
    }
}
