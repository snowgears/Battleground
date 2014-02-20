package com.snowgears.battleground.customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.snowgears.battleground.domination.Base;
import com.snowgears.battleground.domination.CaptureType;

public class PlayerCaptureBaseEvent extends Event{

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Base base = null;
    private String teamColor;
    private CaptureType captureType = null;
    
	public PlayerCaptureBaseEvent(Base b, Player p, String team, CaptureType ct) {
        base = b;
        player = p;
        teamColor = team;
        captureType = ct;
    }
 
    public Player getPlayer() {
        return player;
    }
    
    public String getTeamColor() {
        return teamColor;
    }
    
    public Base getBase() {
        return base;
    }
    
    public CaptureType getCaptureType() {
        return captureType;
    }
 
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
