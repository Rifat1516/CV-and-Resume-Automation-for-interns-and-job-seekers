package com.cvmatcher.model.state;

public class ClosedState implements NoticeState {
    @Override public boolean isRankable() { return false; }
    @Override public boolean isVisibleAsAvailable() { return false; }
    @Override public String label() { return "Closed"; }
}
