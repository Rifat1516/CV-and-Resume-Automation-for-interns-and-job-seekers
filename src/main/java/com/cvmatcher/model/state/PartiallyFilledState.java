package com.cvmatcher.model.state;

public class PartiallyFilledState implements NoticeState {
    @Override public boolean isRankable() { return true; }
    @Override public boolean isVisibleAsAvailable() { return true; }
    @Override public String label() { return "Partially filled"; }
}
