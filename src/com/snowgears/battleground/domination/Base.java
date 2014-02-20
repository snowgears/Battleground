package com.snowgears.battleground.domination;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;

import com.snowgears.battleground.Battleground;
import com.snowgears.battleground.utilities.SerializableLocation;

public class Base implements Serializable{
	
	private static final long serialVersionUID = -94148326515625030L;
	private String world = "";
	private String name = "";
	private String color = "";
	private String previousColor = "";
	
	private SerializableLocation flagLocation = null;
	private SerializableLocation warpLocation = null;
	private ArrayList<SerializableLocation> groundLocations = new ArrayList<SerializableLocation>();
	private ArrayList<SerializableLocation> skyLocations = new ArrayList<SerializableLocation>();
	private ArrayList<SerializableLocation> indicatorLocations = new ArrayList<SerializableLocation>();
	
	private String teamCapturing = "";
	private int repeatingTaskId = 0;
	private int delayedTaskId = 0;
	
	public Base(String w, String n){
		color = "white";
		world = w;
		name = n;
	}
	
	public void reset(){
		color = "white";
		previousColor = "white";
		teamCapturing = "";
		cancelTasks();
		flagLocation.deserialize().getBlock().setTypeIdAndData(35, DyeColor.WHITE.getWoolData(), true);
		for(SerializableLocation loc : groundLocations){
			loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.WHITE.getWoolData(), true);
		}
		for(SerializableLocation loc : skyLocations){
			loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.WHITE.getWoolData(), true);
		}
		for(SerializableLocation loc : indicatorLocations){
			loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.WHITE.getWoolData(), true);
		}
	}
	
	public void resetGround(){
		DyeColor dc = Battleground.tools.getDyeColorFromString(Battleground.tools.getPrimaryColor(color));
		for(SerializableLocation loc : groundLocations){
			loc.deserialize().getBlock().setTypeIdAndData(35, dc.getWoolData(), true);
		}
	}
	
	public void cancelTasks(){
		Battleground.plugin.getServer().getScheduler().cancelTask(repeatingTaskId);
		Battleground.plugin.getServer().getScheduler().cancelTask(delayedTaskId);
	}
	
	public void playEffect(String color){
		if(color.equalsIgnoreCase("blue")){
			for(Location loc : getAllLocations()){
				loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.LAPIS_BLOCK.getId());
			}
		}
		else if(color.equalsIgnoreCase("red")){
			for(Location loc : getAllLocations()){
				loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.LAVA.getId());
			}
		}
		else if(color.equalsIgnoreCase("white")){
			for(Location loc : getAllLocations()){
				loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.WOOL.getId());
			}
		}
	}
	
	//====================================================================================//
	//           GETTERS
	//====================================================================================//
	
	public String getWorldName(){
		return world;
	}
	
	public String getName(){
		return name;
	}
	
	public String getColor(){
		return color;
	}
	
	public String getPreviousColor(){
		return previousColor;
	}
	
	public ArrayList<Location> getAllLocations(){
		ArrayList<Location> locs = new ArrayList<Location>();
		locs.add(flagLocation.deserialize());			//flag
		locs.add(warpLocation.deserialize());      		//warp
		for(SerializableLocation sl : groundLocations){ //ground
			locs.add(sl.deserialize());
		}
		for(SerializableLocation sl : skyLocations){ 	//sky
			locs.add(sl.deserialize());
		}
		for(SerializableLocation sl : indicatorLocations){ //indicators
			locs.add(sl.deserialize());
		}
		return locs;
	}
	
	public Location getFlagLocation(){
		return flagLocation.deserialize();
	}
	
	public Location getWarpLocation(){
		return warpLocation.deserialize();
	}
	
	public ArrayList<Location> getGroundLocations(){
		ArrayList<Location> locs = new ArrayList<Location>();
		for(SerializableLocation sl : groundLocations){
			locs.add(sl.deserialize());
		}
		return locs;
	}
	
	public ArrayList<Location> getSkyLocations(){
		ArrayList<Location> locs = new ArrayList<Location>();
		for(SerializableLocation sl : skyLocations){
			locs.add(sl.deserialize());
		}
		return locs;
	}
	
	public ArrayList<Location> getIndicatorLocations(){
		ArrayList<Location> locs = new ArrayList<Location>();
		for(SerializableLocation sl : indicatorLocations){
			locs.add(sl.deserialize());
		}
		return locs;
	}
	
	public String getTeamCapturing(){
		return teamCapturing;
	}
	
	public int getRepeatingTaskID(){
		return repeatingTaskId;
	}
	
	public int getDelayedTaskID(){
		return delayedTaskId;
	}
	
	//====================================================================================//
	//           SETTERS
	//====================================================================================//
	
	public void setWorld(String w){
		world = w;
	}
	
	public void setName(String n){
		name = n;
	}
	
	public void setColor(String c){
		previousColor = color;
		color = c;
		if(color.equals("white")){
			flagLocation.deserialize().getBlock().setTypeIdAndData(35, DyeColor.WHITE.getWoolData(), true);
			for(SerializableLocation loc : groundLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.WHITE.getWoolData(), true);
			}
			for(SerializableLocation loc : skyLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.WHITE.getWoolData(), true);
			}
			for(SerializableLocation loc : indicatorLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.WHITE.getWoolData(), true);
			}
		}
		else if(color.equals("blue")){
			flagLocation.deserialize().getBlock().setTypeIdAndData(35, DyeColor.BLUE.getWoolData(), true);
			for(SerializableLocation loc : groundLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.BLUE.getWoolData(), true);
			}
			for(SerializableLocation loc : skyLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.BLUE.getWoolData(), true);
			}
			for(SerializableLocation loc : indicatorLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.BLUE.getWoolData(), true);
			}
		}
		else if(color.equals("lightblue")){
			flagLocation.deserialize().getBlock().setTypeIdAndData(35, DyeColor.LIGHT_BLUE.getWoolData(), true);
			for(SerializableLocation loc : groundLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.BLUE.getWoolData(), true);
			}
			for(SerializableLocation loc : skyLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.LIGHT_BLUE.getWoolData(), true);
			}
			for(SerializableLocation loc : indicatorLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.LIGHT_BLUE.getWoolData(), true);
			}
		}
		else if(color.equals("red")){
			flagLocation.deserialize().getBlock().setTypeIdAndData(35, DyeColor.RED.getWoolData(), true);
			for(SerializableLocation loc : groundLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.RED.getWoolData(), true);
			}
			for(SerializableLocation loc : skyLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.RED.getWoolData(), true);
			}
			for(SerializableLocation loc : indicatorLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.RED.getWoolData(), true);
			}
		}
		else if(color.equals("pink")){
			flagLocation.deserialize().getBlock().setTypeIdAndData(35, DyeColor.PINK.getWoolData(), true);
			for(SerializableLocation loc : groundLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.RED.getWoolData(), true);
			}
			for(SerializableLocation loc : skyLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.PINK.getWoolData(), true);
			}
			for(SerializableLocation loc : indicatorLocations){
				loc.deserialize().getBlock().setTypeIdAndData(35, DyeColor.PINK.getWoolData(), true);
			}
		}
	}

	public void setFlagLocation(Location loc){
		SerializableLocation serLoc = new SerializableLocation(loc);
		flagLocation = serLoc;
	}
	
	public void setWarpLocation(Location loc){
		SerializableLocation serLoc = new SerializableLocation(loc);
		warpLocation = serLoc;
	}
	
	public void setGroundLocations(ArrayList<Location> groundLocs){
		ArrayList<SerializableLocation> locs = new ArrayList<SerializableLocation>();
		for(Location l : groundLocs){
			SerializableLocation serLoc = new SerializableLocation(l);
			locs.add(serLoc);
		}
		groundLocations = locs;
	}
	
	public void setSkyLocations(ArrayList<Location> skyLocs){
		ArrayList<SerializableLocation> locs = new ArrayList<SerializableLocation>();
		for(Location l : skyLocs){
			SerializableLocation serLoc = new SerializableLocation(l);
			locs.add(serLoc);
		}
		skyLocations = locs;
	}
	
	public void setIndicatorLocations(ArrayList<Location> indLocs){
		ArrayList<SerializableLocation> locs = new ArrayList<SerializableLocation>();
		for(Location l : indLocs){
			SerializableLocation serLoc = new SerializableLocation(l);
			locs.add(serLoc);
		}
		indicatorLocations = locs;
	}
	
	public void setTeamCapturing(String team){
		teamCapturing = team;
	}
	
	public void setRepeatingTaskID(int taskId){
		Battleground.plugin.getServer().getScheduler().cancelTask(repeatingTaskId);
		repeatingTaskId = taskId;
	}
	
	public void setDelayedTaskID(int taskId){
		Battleground.plugin.getServer().getScheduler().cancelTask(delayedTaskId);
		delayedTaskId = taskId;
	}
}
