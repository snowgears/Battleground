package com.snowgears.battleground.domination;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.snowgears.battleground.Battleground;

public class AreaContains {
	
	private Battleground plugin = Battleground.plugin;
	private boolean redTeam = false;
	private boolean blueTeam = false;
	private Base base = null;
	private ArrayList<String> playersInArea = new ArrayList<String>();
	
	public AreaContains(Battleground instance, Base b){
		plugin = instance;
		base = b;
		refresh();
	}
	
	public boolean getRedTeam(){
		return redTeam;
	}
	
	public boolean getBlueTeam(){
		return blueTeam;
	}
	
	public Base getBase(){
		return base;
	}
	
	public ArrayList<String> getPlayersInArea(){
		return playersInArea;
	}
	
	public String getColorInBase(){
		if(redTeam==true && blueTeam==false)
			return "red";
		else if(redTeam==false && blueTeam==true)
			return "blue";
		return "";
	}
	
	public void refresh(){      // find if there are red/blue players in area
		
		redTeam = false;
		blueTeam = false;
		playersInArea.clear();
		
		ArrayList<Player> players = new ArrayList<Player>();
	    List<Player> worldPlayers = Bukkit.getWorld(base.getWorldName()).getPlayers();
	     
	    for (Player player : worldPlayers) {
	         for(Location loc : base.getGroundLocations()){
	        	 //players location is within groundLocs
	        	 if(player.getLocation().clone().add(0,-2,0).getBlock().getLocation().equals(loc)){
	        		 players.add(player);
	        		 playersInArea.add(player.getName());
	        		 break;
	        	 }
	         }
	    }

		for(Player p : players){
			if(plugin.playerManager.getRedTeam().contains(p.getName())){
				redTeam = true;
			}
			else if(plugin.playerManager.getBlueTeam().contains(p.getName())){
				blueTeam = true;
			}
		}
		players = null;
	}
}
