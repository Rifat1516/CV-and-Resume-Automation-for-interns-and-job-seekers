package com.cvmatcher.service;

import com.cvmatcher.model.JobNotice;
import com.cvmatcher.model.JobPosition;

/**
 * Service responsible for managing the availability and details of job notices and positions.
 * This includes tracking filled positions, updating descriptions, and adding new roles.
 */
public interface AvailabilityService {

    /**
     * Updates the total number of filled positions for a specific job notice.
     *
     * @param notice          The job notice to update.
     * @param filledPositions The new count of overall filled positions for the notice.
     */
    void updateFilledPositions(JobNotice notice, int filledPositions);

    /**
     * Updates the description of an existing job notice.
     *
     * @param notice      The job notice to update.
     * @param description The new description text.
     */
    void updateDescription(JobNotice notice, String description);

    /**
     * Adds a new job position (role) to an existing job notice.
     *
     * @param notice     The job notice to which the position will be added.
     * @param roleName   The title or name of the role (e.g., "Software Engineer").
     * @param totalCount The total number of openings for this specific role.
     */
    void addPosition(JobNotice notice, String roleName, int totalCount);

    /**
     * Updates the count of filled openings for a specific job position.
     *
     * @param position    The job position to update.
     * @param filledCount The new count of filled openings for this specific position.
     */
    void updatePositionFilledCount(JobPosition position, int filledCount);
}
