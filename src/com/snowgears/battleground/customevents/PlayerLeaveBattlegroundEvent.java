package com.snowgears.battleground.customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.snowgears.battleground.Battleground;

	public class PlayerLeaveBattlegroundEvent extends Event {
		public Battleground plugin = Battleground.plugin;
	    private static final HandlerList handlers = new HandlerList();
	    private Player player;
	    
	    public PlayerLeaveBattlegroundEvent(Battleground instance, Player example) {
	    	plugin = instance;
	        player = example;
	    }
	 
	    public Player getPlayer() {
	        return player;
	    }
	 
	    public HandlerList getHandlers() {
	        return handlers;
	    }
	 
	    public static HandlerList getHandlerList() {
	        return handlers;
	    }
}
