package com.snowgears.battleground;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;


public class WorldHandler {
	
	public Battleground plugin = Battleground.plugin;
	
	private ArrayList<String> battleWorlds = new ArrayList<String>();
	private HashMap<String, WorldLocations> allWorldLocations = new HashMap<String, WorldLocations>();
	private String currentWorld = "";
	
	public WorldHandler(Battleground instance){
		plugin = instance;
		defineBattleWorlds();
	}
	
	public String getBattleTypeString(String world){
		if(!battleWorlds.contains(world))
			return null;
		if(world.contains("bg_domination"))
			return "Domination";
		else if(world.contains("bg_storm"))
			return "Storm";
		return "";
	}
	
	public BattleType getBattleType(String world){
		if(!battleWorlds.contains(world))
			return null;
		if(world.contains("bg_domination"))
			return BattleType.DOMINATION;
		else if(world.contains("bg_storm"))
			return BattleType.STORM;
		return null;
	}
	
	public String getCurrentWorld(){
		return currentWorld;
	}
	
	public boolean setCurrentWorld(String world){
		if(battleWorlds.contains(world)){
			currentWorld = world;
			return true;
		}
		else
			return false;
	}
	
	public ArrayList<String> getAllBattleWorlds(){
		return battleWorlds;
	}
	
	public WorldLocations getWorldLocations(String world){
		if(allWorldLocations.containsKey(world))
			return allWorldLocations.get(world);
		return null;
	}
	
	public void setWorldLocations(String world, WorldLocations wl){
		allWorldLocations.put(world, wl);
	}
	
	public HashMap<String, WorldLocations> getAllWorldLocationsHashMap(){
		return allWorldLocations;
	}
	
	public void setAllWorldLocationsOnLoad(HashMap<String, WorldLocations> allTemp){
		allWorldLocations = allTemp;
	}
	
	public String getCleanWorldName(String world){
		String cleanName = world.replaceAll("bg_", "");
		if(cleanName.contains("_")){
			int index = cleanName.indexOf("_");
			cleanName = cleanName.substring(index+1);
			cleanName.replace("_", " ");
		}
		return cleanName;
	}
	
	private void defineBattleWorlds(){
		battleWorlds.clear();
		File worldContainer = plugin.getServer().getWorldContainer();
		if(worldContainer.isDirectory()==false)
			return;
		int count = 0;
		for(File worldFile : worldContainer.listFiles()){
			if(worldFile.getName().contains("bg_") && worldFile.isDirectory()){
				World bgWorld = plugin.getServer().createWorld(new WorldCreator(worldFile.getName()));
				battleWorlds.add(bgWorld.getName());
				bgWorld.setPVP(true);
				count++;
			}
		}
		System.out.println("[Battleground] There were "+count+" battle worlds detected and loaded.");
	}
	
	public void resetWorld(String world){
		if(plugin.getServer().getWorld(world) == null)
			return;
		for(Entity e : plugin.getServer().getWorld(world).getEntities()){
			if(! (e instanceof ItemFrame || e instanceof Player))
				e.remove();
		}
		WorldLocations wl = allWorldLocations.get(world);
		for(Location loc : wl.getGateLocations()){
			loc.getBlock().setType(Material.FENCE);
		}
		
		if(plugin.worldHandler.getBattleType(world) == BattleType.DOMINATION)
			plugin.baseHandler.resetBases(world);

		plugin.getServer().getConsoleSender().sendMessage("[Battleground]"+ChatColor.GRAY+" Battleground world reset complete.");
	}
}
