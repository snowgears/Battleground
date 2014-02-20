package com.snowgears.battleground.customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.snowgears.battleground.Battleground;

	public class PlayerJoinBattlegroundEvent extends Event {
		public Battleground plugin = Battleground.plugin;
	    private static final HandlerList handlers = new HandlerList();
	    private Player player;
	    private String team;
	    private String worldFrom = "";
	    private String worldTo = "";
	    
	    public PlayerJoinBattlegroundEvent(Battleground instance, Player example, String t, String world) {
	    	plugin = instance;
	        player = example;
	        team = t;
	        worldFrom = player.getWorld().getName();
	        worldTo = world;
	    }
	 
	    public Player getPlayer() {
	        return player;
	    }
	    
	    public String getTeam() {
	        return team;
	    }
	    
	    public String getOldWorld() {
	        return worldFrom;
	    }
	    
	    public String getNewWorld() {
	        return worldTo;
	    }
	 
	    public HandlerList getHandlers() {
	        return handlers;
	    }
	 
	    public static HandlerList getHandlerList() {
	        return handlers;
	    }
}
