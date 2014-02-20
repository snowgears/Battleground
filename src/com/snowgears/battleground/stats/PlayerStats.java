package com.snowgears.battleground.stats;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlayerStats {

	private FileConfiguration ymlConfig = null;
	private File ymlFile = null;
	
	private String playerName = "";
	private int kills = 0; //kills only count if in battleground during a game
	private int deaths = 0; //deaths only count if in battleground during a game
	
	private int dominationCaptures = 0;
	private int dominationDefends = 0;
	private int dominationAssaults = 0;
	
	public PlayerStats(Player player, File statsFile, FileConfiguration statsFileConfig){
		ymlFile = statsFile;
		ymlConfig = statsFileConfig;
		playerName = player.getName();
        kills = (Integer) statsFileConfig.get("kills");
        deaths = (Integer) statsFileConfig.get("deaths");
        dominationCaptures = (Integer) statsFileConfig.get("dominationCaptures");
        dominationDefends = (Integer) statsFileConfig.get("dominationDefends");
        dominationAssaults = (Integer) statsFileConfig.get("dominationAssaults");
	}
	
	public void saveToYML(){
		ymlConfig.set("kills", kills);
		ymlConfig.set("deaths", deaths);
		ymlConfig.set("dominationCaptures", dominationCaptures);
		ymlConfig.set("dominationDefends", dominationDefends);
		ymlConfig.set("dominationAssaults", dominationAssaults);
		
		try {
			ymlConfig.save(ymlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public double getKillDeathRatio(){
		double kd = kills/deaths;
		return kd;
	}
	
	public String getPlayerName(){
		return playerName;
	}
	
	public int getKills(){
		return kills;
	}
	
	public int getDeaths(){
		return deaths;
	}
	
	public int getDominationCaptures(){
		return dominationCaptures;
	}
	
	public int getDominationDefends(){
		return dominationDefends;
	}
	
	public int getDominationAssaults(){
		return dominationAssaults;
	}
	
	public void addKill(){
		kills++;
	}
	
	public void addDeath(){
		deaths++;
	}
	
	public void addDominationCapture(){
		dominationCaptures++;
	}
	
	public void addDominationDefend(){
		dominationDefends++;
	}
	
	public void addDominationAssault(){
		dominationAssaults++;
	}
}
