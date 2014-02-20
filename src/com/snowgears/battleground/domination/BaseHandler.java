package com.snowgears.battleground.domination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import com.snowgears.battleground.Battleground;

public class BaseHandler {
	public Battleground plugin = Battleground.plugin;
	
	private HashMap<String, ArrayList<Base>> allBases = new HashMap<String, ArrayList<Base>>();
//	private HashMap<String, ArrayList<Base>> redBases = new HashMap<String, ArrayList<Base>>(); //World, red bases
//	private HashMap<String, ArrayList<Base>> blueBases = new HashMap<String, ArrayList<Base>>(); //World, blue bases
	
	public BaseHandler(Battleground instance){
		plugin = instance;
	}
	
	public Base getBaseFromGroundLocation(String world, Location loc){ //the location must be from ground locations
		if(allBases.get(world) == null)
			return null;
		for(Base b : allBases.get(world)){
			for(Location l : b.getGroundLocations()){
				if(l.equals(loc))
					return b;
			}
		}
		return null;
	}
	
	public Base getBaseFromIndicatorLocation(String world, Location loc){ //the location must be from indicator locations
		if(allBases.get(world) == null)
			return null;
		for(Base b : allBases.get(world)){
			for(Location l : b.getIndicatorLocations()){
				if(l.equals(loc))
					return b;
			}
		}
		return null;
	}
	
	public Base getBaseFromName(String world, String name){ 
		if(allBases.get(world) == null)
			return null;
		for(Base b : allBases.get(world)){
			if(b.getName().equals(name))
				return b;
		}
		return null;
	}
	
	public ArrayList<Base> getAllBasesInWorld(String world){
		if(allBases.get(world) != null)
			return allBases.get(world);
		return new ArrayList<Base>();
	}
	
	public void setAllBasesOnLoad(HashMap<String, ArrayList<Base>> allBasesTemp){
		allBases = allBasesTemp;
	}
	
	public HashMap<String, ArrayList<Base>> getAllBasesHashMap(){
		return allBases;
	}
	
	public Base createBase(String world, String name){
		Base base = null;
		for(Base b : getAllBasesInWorld(world)){
			if(b.getName().equals(name))
				return null;
		}
		base = new Base(world, name);
		if(allBases.get(world) == null)
			allBases.put(world, new ArrayList<Base>());
		ArrayList<Base> temp = allBases.get(world);
		temp.add(base);
		allBases.put(world, temp);
		return base;
	}
	
	public void deleteBase(Base base){
		if(base == null)
			return;
		ArrayList<Base> temp = allBases.get(base.getWorldName());
		if(temp.contains(base))
			temp.remove(base);
		allBases.put(base.getWorldName(), temp);
		base = null;
	}

	public int getNumberOfRedBases(String world){
		int num = 0;
		for(Base b : allBases.get(world)){
			if(b.getColor().equals("red"))
				num++;
		}
		return num;
	}
	
	public int getNumberOfBlueBases(String world){
		int num = 0;
		for(Base b : allBases.get(world)){
			if(b.getColor().equals("blue"))
				num++;
		}
		return num;
	}
	
	public void resetBases(String world){
		for(Base b : allBases.get(world)){
			b.reset();
		}
	}
}
