package com.snowgears.battleground;

public enum BattleType {

	/**
     * Event call is of very low importance and should be ran first, to allow
     * other plugins to further customise the outcome
     */
    DOMINATION(0),
    /**
     * Event call is of low importance
     */
    STORM(1),
    /**
     * Event call is neither important or unimportant, and may be ran normally
     */
    GAURDIAN(2);
    
    private final int slot;

    private BattleType(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}
