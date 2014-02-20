package com.snowgears.battleground.domination;

public enum CaptureType {

	/**
     * Event call is of very low importance and should be ran first, to allow
     * other plugins to further customise the outcome
     */
    CAPTURE(0),
    /**
     * Event call is of low importance
     */
    DEFEND(1),
    /**
     * Event call is neither important or unimportant, and may be ran normally
     */
    ASSAULT(2);
    
    private final int slot;

    private CaptureType(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}
