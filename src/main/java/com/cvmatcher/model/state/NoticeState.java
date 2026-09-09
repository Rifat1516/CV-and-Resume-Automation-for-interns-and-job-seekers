package com.cvmatcher.model.state;

/**
 * State pattern: encapsulates behavior that differs depending on a
 * JobNotice's availability lifecycle, instead of scattering
 * if (filledPositions >= totalPositions) checks across the app.
 */
public interface NoticeState {
    boolean isRankable();
    boolean isVisibleAsAvailable();
    String label();
}
