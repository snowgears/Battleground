package com.snowgears.battleground;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class TeamManager {

	public Battleground plugin = Battleground.plugin;
	
	private ArrayList<String> redTeam = new ArrayList<String>();
	private ArrayList<String> blueTeam = new ArrayList<String>();
	private ArrayList<String> spectators = new ArrayList<String>();
	
	private ArrayList<String> queue = new ArrayList<String>();

	public TeamManager(Battleground instance){
        plugin = instance;
    }
	
	public ArrayList<String> getRedTeam(){
		return redTeam;
	}
	
	public ArrayList<String> getBlueTeam(){
		return blueTeam;
	}
	
	public ArrayList<String> getSpectators(){
		return spectators;
	}
	
	public ArrayList<String> getQueue(){
		return queue;
	}
	
	public void clearQueue(){
		queue.clear();
	}
	
	public void resetGroups(){
		redTeam.clear();
		blueTeam.clear();
		spectators.clear();
	}

	public void addPlayerToQueue(Player player){
		if(!queue.contains(player.getName()))
			queue.add(player.getName());
	}
	
	public void removePlayerFromQueue(Player player){
		if(queue.contains(player.getName()))
			queue.remove(player.getName());
	}
	
	public void addPlayerToRedTeam(Player player){
		if(!redTeam.contains(player.getName()))
			redTeam.add(player.getName());
	}
	
	public void addPlayerToBlueTeam(Player player){
		if(!blueTeam.contains(player.getName()))
			blueTeam.add(player.getName());
	}
	
	public void addPlayerToSpectators(Player player){
		if(!spectators.contains(player.getName()))
			spectators.add(player.getName());
	}
	
	public void removePlayerFromAllTeams(Player player){
		if(redTeam.contains(player.getName()))
			redTeam.remove(player.getName());
		else if(blueTeam.contains(player.getName()))
			blueTeam.remove(player.getName());
		else if(spectators.contains(player.getName()))
			spectators.remove(player.getName());
	}
}
