package com.snowgears.battleground;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerDataHandler {

	private Battleground plugin = Battleground.plugin;

	private ArrayList<PlayerData> allPlayerData = new ArrayList<PlayerData>();
	
	public PlayerDataHandler(Battleground instance){
		plugin = instance;
	}
	
	public void savePlayerData(Player p){
		PlayerData pd = new PlayerData(p);
		allPlayerData.add(pd);
		
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
	    p.getInventory().setChestplate(null);
	    p.getInventory().setLeggings(null);
	    p.getInventory().setBoots(null);
	    
	    for (PotionEffect effect : p.getActivePotionEffects()){
	        p.removePotionEffect(effect.getType());
	    }
	}
	
	public boolean returnDataToPlayer(Player p){
		PlayerData playerData = null;
		for(PlayerData pd : allPlayerData){
			if(pd.getPlayerName().equals(p.getName())){
				playerData = pd;
				break;
			}
		}
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
	    p.getInventory().setChestplate(null);
	    p.getInventory().setLeggings(null);
	    p.getInventory().setBoots(null);
		
	    for (PotionEffect effect : p.getActivePotionEffects()){
	        p.removePotionEffect(effect.getType());
	    }
	    
		if(playerData == null)
			return false;
		
		ItemStack[] inventory = playerData.getInventory().toArray(new ItemStack[playerData.getInventory().size()]);
		p.getInventory().setContents(inventory);
		ItemStack[] armor = playerData.getArmor().toArray(new ItemStack[playerData.getArmor().size()]);
		p.getInventory().setArmorContents(armor);
		
		p.teleport(playerData.getLocation());
		p.setGameMode(playerData.getGameMode());
		p.setFlying(false);
		
		allPlayerData.remove(playerData);
		playerData = null;
		return true;
	}
	
	public void returnDataToAllPlayersInWorld(String world){
		for(Player p : plugin.getServer().getWorld(world).getPlayers()){
			returnDataToPlayer(p);
		}
	}
}
