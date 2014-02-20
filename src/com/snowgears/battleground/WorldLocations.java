package com.snowgears.battleground;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Location;

import com.snowgears.battleground.utilities.SerializableLocation;

public class WorldLocations implements Serializable{
	
	private static final long serialVersionUID = 5332669380453727985L;

	private String world = "";
	
	private ArrayList<SerializableLocation> redSpawnLocations = new ArrayList<SerializableLocation>();
	private ArrayList<SerializableLocation> blueSpawnLocations = new ArrayList<SerializableLocation>();
	private ArrayList<SerializableLocation> gateLocations = new ArrayList<SerializableLocation>();

	public WorldLocations(String w){
		world = w;
	}
	public String getWorld(){
		return world;
	}
	// MAKE METHODS IN HERE FOR THE SET/ADD/REMOVE SO THAT YOU DONT HAVE TO CALCULATE IN COMMANDS
	
	public ArrayList<Location> getRedSpawnLocations(){
		ArrayList<Location> locs = new ArrayList<Location>();
		for(SerializableLocation sl : redSpawnLocations){
			locs.add(sl.deserialize());
		}
		return locs;
	}
	
	public ArrayList<Location> getBlueSpawnLocations(){
		ArrayList<Location> locs = new ArrayList<Location>();
		for(SerializableLocation sl : blueSpawnLocations){
			locs.add(sl.deserialize());
		}
		return locs;
	}
	
	public ArrayList<Location> getGateLocations(){
		ArrayList<Location> locs = new ArrayList<Location>();
		for(SerializableLocation sl : gateLocations){
			locs.add(sl.deserialize());
		}
		return locs;
	}
	
	
	public void setSpawnLocations(ArrayList<Location> spawnLocs, String color){
		ArrayList<SerializableLocation> locs = new ArrayList<SerializableLocation>();
		for(Location l : spawnLocs){
			SerializableLocation serLoc = new SerializableLocation(l);
			locs.add(serLoc);
		}
		if(color.equals("red"))
			redSpawnLocations = locs;
		else if(color.equals("blue"))
			blueSpawnLocations = locs;
	}
	
	public void addSpawnLocations(ArrayList<Location> spawnLocs, String color){
		ArrayList<SerializableLocation> locs = new ArrayList<SerializableLocation>();
		for(Location l : spawnLocs){
			SerializableLocation serLoc = new SerializableLocation(l);
			locs.add(serLoc);
		}
		if(color.equals("red"))
			redSpawnLocations.addAll(locs);
		else if(color.equals("blue"))
			blueSpawnLocations.addAll(locs);
	}
	
	public void removeSpawnLocations(ArrayList<Location> spawnLocs, String color){
		ArrayList<SerializableLocation> locs = new ArrayList<SerializableLocation>();
		for(Location l : spawnLocs){
			SerializableLocation serLoc = new SerializableLocation(l);
			locs.add(serLoc);
		}
		if(color.equals("red")){
			for(SerializableLocation l : locs){
				if(redSpawnLocations.contains(l))
					redSpawnLocations.remove(l);
			}
		}
		else if(color.equals("blue")){
			for(SerializableLocation l : locs){
				if(blueSpawnLocations.contains(l))
					blueSpawnLocations.remove(l);
			}
		}
	}
	
	public void setGateLocations(ArrayList<Location> gateLocs){
		ArrayList<SerializableLocation> locs = new ArrayList<SerializableLocation>();
		for(Location l : gateLocs){
			SerializableLocation serLoc = new SerializableLocation(l);
			locs.add(serLoc);
		}
		gateLocations = locs;
	}
	
	public void addGateLocations(ArrayList<Location> gateLocs){
		ArrayList<SerializableLocation> locs = new ArrayList<SerializableLocation>();
		for(Location l : gateLocs){
			SerializableLocation serLoc = new SerializableLocation(l);
			locs.add(serLoc);
		}
		gateLocations.addAll(locs);
	}
	
	public void removeGateLocations(ArrayList<Location> gateLocs){
		ArrayList<SerializableLocation> locs = new ArrayList<SerializableLocation>();
		for(Location l : gateLocs){
			SerializableLocation serLoc = new SerializableLocation(l);
			locs.add(serLoc);
		}
		
		for(SerializableLocation l : locs){
			if(gateLocations.contains(l))
				gateLocations.remove(l);
		}
	}
	
}
