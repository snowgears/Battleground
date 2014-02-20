package com.snowgears.battleground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerData {
	
	private String player = null;
	private List<ItemStack> inventory = new ArrayList<ItemStack>();
	private List<ItemStack> armor = new ArrayList<ItemStack>();
	private Location location = null;
	private GameMode gameMode = null;
	
	public PlayerData(Player p){
		player = p.getName();
		inventory = Arrays.asList(p.getInventory().getContents());
		armor = Arrays.asList(p.getInventory().getArmorContents());
		location = p.getLocation();
		gameMode = p.getGameMode();
	}
	
	public String getPlayerName(){
		return player;
	}
	
	public List<ItemStack> getInventory(){
		return inventory;
	}
	
	public List<ItemStack> getArmor(){
		return armor;
	}
	
	public Location getLocation(){
		return location;
	}
	
	public GameMode getGameMode(){
		return gameMode;
	}
}
