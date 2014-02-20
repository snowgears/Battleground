package com.snowgears.battleground.stats;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class PlayerStatsHandler {

	private ArrayList<PlayerStats> allPlayerStats = new ArrayList<PlayerStats>();
	
	public PlayerStats getPlayerStats(Player p){
		String playerName = p.getName();
		for(PlayerStats ps : allPlayerStats){
			if(ps.getPlayerName().equals(playerName))
				return ps;
		}
		return null;
	}
	
	public void addPlayerStats(PlayerStats ps){
		if(!allPlayerStats.contains(ps))
			allPlayerStats.add(ps);
	}
	
	public void removePlayerStats(PlayerStats ps){
		if(allPlayerStats.contains(ps))
			allPlayerStats.add(ps);
	}

	public void saveAllPlayerStats(){
		for(PlayerStats ps : allPlayerStats){
			ps.saveToYML();
		}
	}
}
