package com.snowgears.battleground.api;

import org.bukkit.entity.Player;

import com.snowgears.battleground.Battleground;
import com.snowgears.battleground.stats.PlayerStats;

public final class StatsAPI {

	public static int getKills(Player p){
		PlayerStats ps = Battleground.plugin.playerStatsHandler.getPlayerStats(p);
		if(ps == null)
			return 0;
		return ps.getKills();
	}
	
	public static int getDeaths(Player p){
		PlayerStats ps = Battleground.plugin.playerStatsHandler.getPlayerStats(p);
		if(ps == null)
			return 0;
		return ps.getDeaths();
	}
	
	public static double getKillDeathRatio(Player p){
		PlayerStats ps = Battleground.plugin.playerStatsHandler.getPlayerStats(p);
		if(ps == null)
			return 0;
		return ps.getKillDeathRatio();
	}
	
	public static int getDominationCaptures(Player p){
		PlayerStats ps = Battleground.plugin.playerStatsHandler.getPlayerStats(p);
		if(ps == null)
			return 0;
		return ps.getDominationCaptures();
	}
	
	public static int getDominationDefends(Player p){
		PlayerStats ps = Battleground.plugin.playerStatsHandler.getPlayerStats(p);
		if(ps == null)
			return 0;
		return ps.getDominationDefends();
	}
	
	public static int getDominationAssaults(Player p){
		PlayerStats ps = Battleground.plugin.playerStatsHandler.getPlayerStats(p);
		if(ps == null)
			return 0;
		return ps.getDominationAssaults();
	}
	
	
	
	public static void addKill(Player p){
		PlayerStats ps = Battleground.plugin.playerStatsHandler.getPlayerStats(p);
		if(ps == null)
			return;
		ps.addKill();
	}
	
	public static void addDeath(Player p){
		PlayerStats ps = Battleground.plugin.playerStatsHandler.getPlayerStats(p);
		if(ps == null)
			return;
		ps.addDeath();
	}
	
	public static void addDominationCapture(Player p){
		PlayerStats ps = Battleground.plugin.playerStatsHandler.getPlayerStats(p);
		if(ps == null)
			return;
		ps.addDominationCapture();
	}
	
	public static void addDominationDefend(Player p){
		PlayerStats ps = Battleground.plugin.playerStatsHandler.getPlayerStats(p);
		if(ps == null)
			return;
		ps.addDominationDefend();
	}
	
	public static void addDominationAssault(Player p){
		PlayerStats ps = Battleground.plugin.playerStatsHandler.getPlayerStats(p);
		if(ps == null)
			return;
		ps.addDominationAssault();
	}
}
