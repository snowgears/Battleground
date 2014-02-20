package com.snowgears.battleground.domination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.snowgears.battleground.Battleground;
import com.snowgears.battleground.api.StatsAPI;
import com.snowgears.battleground.customevents.PlayerCaptureBaseEvent;

public class DominationListener implements Listener{

	public Battleground plugin = Battleground.plugin;
	
	public DominationListener(Battleground instance)
    {
        plugin = instance;
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if( ! (player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())))
			return;
		if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.GLASS){
			Location loc = player.getLocation().getBlock().getLocation().clone().add(0,-2,0);
			Base base = plugin.baseHandler.getBaseFromGroundLocation(player.getWorld().getName(), loc);
			if(base == null)
				return;
			
			AreaContains ac = new AreaContains(plugin, base);
			
			//player is already capturing base with team
			if(Battleground.tools.getPrimaryColor(base.getColor()).equals(base.getTeamCapturing()) || Battleground.tools.getPrimaryColor(base.getColor()).equals(ac.getColorInBase())) //|| base.getTeamCapturing().equals(ac.getColorInBase()))
				return;
			else if(ac.getColorInBase().isEmpty()==false && (!base.getTeamCapturing().equals(ac.getColorInBase()))){
				base.cancelTasks();
				base.setTeamCapturing("");
				base.resetGround();
			}
			
			if(ac.getRedTeam() == true && ac.getBlueTeam() == false){
				if(base.getTeamCapturing().equals("red"))
					return;
				base.setTeamCapturing("red");
				beginBaseCapture(base, ac, "red");
			}
			else if(ac.getRedTeam() == false && ac.getBlueTeam() == true){
				if(base.getTeamCapturing().equals("blue"))
					return;
				base.setTeamCapturing("blue");
				beginBaseCapture(base, ac, "blue");
			}
		}
	}

	private ArrayList<Location> getGroundLocsDifferentFromColor(HashMap<String, ArrayList<Location>> groundLocsMap, String color){
		ArrayList<Location> otherColorsInGround = new ArrayList<Location>();
		for(Map.Entry<String, ArrayList<Location>> entry : groundLocsMap.entrySet()){
			if(!entry.getKey().equals(color)){
				otherColorsInGround.addAll(entry.getValue());
			}
		}
		return otherColorsInGround;
	}
	
	private void beginBaseCapture(final Base base, final AreaContains areaContains, final String teamCapturing){

		final ArrayList<Location> groundLocations = base.getGroundLocations();

			int taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
    		    public void run() {    //this will run every 2 seconds
    		    	
    		    	final HashMap<String, ArrayList<Location>> groundLocsMap = countGroundColors(groundLocations);
    		    	
    		    	areaContains.refresh();
    		    	
    		    	if(areaContains.getColorInBase().isEmpty()==false)
    		    		base.setTeamCapturing(areaContains.getColorInBase());

    		    	//if neither team is in area, fade ground back to original color
    		    	if(areaContains.getColorInBase().isEmpty()){
    		    		base.setTeamCapturing(Battleground.tools.getPrimaryColor(base.getColor()));
    		    		DyeColor color = Battleground.tools.getDyeColorFromString(Battleground.tools.getPrimaryColor(base.getColor()));
    		    		ArrayList<Location> groundLocsToChange = getGroundLocsDifferentFromColor(groundLocsMap, base.getColor());
    		    		
    		    		for(int i=0; i<5; i++){
    		    			Collections.shuffle(groundLocsToChange); //shuffle white locs
    		    			if(groundLocsToChange.isEmpty()){
    		    				base.setRepeatingTaskID(0);
    		    				base.setTeamCapturing("");
    		    				return;
    		    			}
    		    			Location randomLoc = groundLocsToChange.get(0);
    		    			randomLoc.getBlock().setTypeIdAndData(35, color.getWoolData(), true);
    		    			groundLocsToChange.remove(0);
    		    		}			
    		    		base.playEffect(base.getColor());
    		    	} 
    		    	//the area contains only one team, change ground to that team color
    		    	else if(base.getTeamCapturing().equals(teamCapturing)){
    		    		DyeColor color = Battleground.tools.getDyeColorFromString(base.getTeamCapturing());
    		    		ArrayList<Location> groundLocsToChange = groundLocsMap.get(Battleground.tools.getPrimaryColor(base.getColor()));
    		    		
    		    		//get 5 red blocks from ground and change to capture color
    		    		for(int i=0; i<5; i++){
    		    			Collections.shuffle(groundLocsToChange); //shuffle white locs
    		    			//ground is done changing. Change base color
    		    			if(groundLocsToChange.isEmpty()){
    		    				
    		    				base.setRepeatingTaskID(0); //cancel repeating task
    		    				CaptureType captureType = changeColorAndDisplayMessages(base); //send messages to players
    		    				callCaptureBaseEvents(base, areaContains.getPlayersInArea(), base.getTeamCapturing(), captureType);
    		    				
    		    				setupDelayedColorChange(base, base.getTeamCapturing());
    		    				
    		    				base.setTeamCapturing(""); //reset team capturing
    		    				return;
    		    			}
    		    			Location randomLoc = groundLocsToChange.get(0);
    		    			randomLoc.getBlock().setTypeIdAndData(35, color.getWoolData(), true);
    		    			groundLocsToChange.remove(0);
    		    		}			
    		    		base.playEffect(base.getTeamCapturing());
    		    	}
    		    	//team capturing has changed and player move event will start this method again with new team
    		    	else{
    		    		base.setTeamCapturing("");
    		    		base.cancelTasks();
    		    		base.resetGround();
    		    		return;
    		    	}
    		    }
			}, 1L, 40L); //every 2 seconds
			
			base.setRepeatingTaskID(taskID);
	}
	
	private void setupDelayedColorChange(final Base base, final String teamColor){
		//delayed task to change base to capture color after 1 minute
		
		int delayedTaskID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		    public void run() { 
		    	if(! base.getColor().equals(Battleground.tools.getSecondaryColor(teamColor)))
		    		return;
		    	base.setColor(teamColor);
		    	base.playEffect(teamColor);
		    	
		    	for(Player p : plugin.getServer().getWorld(base.getWorldName()).getPlayers()){
		    		p.sendMessage(ChatColor.YELLOW+"The "+teamColor+" team has taken "+base.getName()+"!");
		    	}
		    }
		}, 1200L); //execute after 1 minute
		
		base.setDelayedTaskID(delayedTaskID);
	}
	
	private void callCaptureBaseEvents(Base b, ArrayList<String> playersInArea, String teamColor, CaptureType captureType){
		for(String name : playersInArea){
			Player p = Bukkit.getPlayer(name);
			if(p != null){
				PlayerCaptureBaseEvent ev = new PlayerCaptureBaseEvent(b, p, teamColor, captureType);
				plugin.getServer().getPluginManager().callEvent(ev);
			}
		}
	}
	
	@EventHandler
	public void onBaseCapture(PlayerCaptureBaseEvent event){
		Player player = event.getPlayer();
		player.sendMessage(ChatColor.GOLD+"You have helped your team "+event.getCaptureType()+" "+event.getBase().getName()+"!");
		if(event.getCaptureType() == CaptureType.CAPTURE)
			StatsAPI.addDominationCapture(player);
		else if(event.getCaptureType() == CaptureType.DEFEND)
			StatsAPI.addDominationDefend(player);
		else if(event.getCaptureType() == CaptureType.ASSAULT)
			StatsAPI.addDominationAssault(player);
		StatsAPI.addKill(player.getKiller());
	}
	
	private CaptureType changeColorAndDisplayMessages(Base base){
		List<Player> playersInWorld = plugin.getServer().getWorld(base.getWorldName()).getPlayers();
		
		if(base.getPreviousColor().equals(base.getTeamCapturing())){
			base.setColor(base.getTeamCapturing()); //on defend, directly change base to team color
			for(Player p : playersInWorld){
				p.sendMessage(ChatColor.YELLOW+"The "+base.getTeamCapturing()+" team has defended "+base.getName()+"!");
			}
			return CaptureType.DEFEND;
		}
		else if(base.getColor().equals("white")){
			base.setColor(Battleground.tools.getSecondaryColor(base.getTeamCapturing()));
			for(Player p : playersInWorld){
				p.sendMessage(ChatColor.YELLOW+"The "+base.getTeamCapturing()+" team has taken "+base.getName()+"! If left unchallenged they will control it in 1 minute!");
			}
		}
		else{
			base.setColor(Battleground.tools.getSecondaryColor(base.getTeamCapturing()));
			for(Player p : playersInWorld){
				p.sendMessage(ChatColor.YELLOW+"The "+base.getTeamCapturing()+" team has assaulted "+base.getName()+"!");
			}
			return CaptureType.ASSAULT;
		}
		return CaptureType.CAPTURE;
	}
	
	@EventHandler
	public void onMapClick(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
				if(event.getClickedBlock().getType() == Material.WOOL){
					Location blockLoc = event.getClickedBlock().getLocation();
					String color = null;
					if(plugin.playerManager.getRedTeam().contains(player.getName()))
						color = "red";
					else if(plugin.playerManager.getBlueTeam().contains(player.getName()))
						color = "blue";
					if(color == null)
						return;
					DyeColor teamColor = Battleground.tools.getDyeColorFromString(color);
					
					if( ! (event.getClickedBlock().getData() == teamColor.getWoolData()))
						return;
					
					if(color.equalsIgnoreCase("red")){
						Location redSpawnLoc = plugin.worldHandler.getWorldLocations(player.getWorld().getName()).getRedSpawnLocations().get(0);
						if(player.getLocation().distance(redSpawnLoc) > 50){
							player.sendMessage(ChatColor.GRAY+"You must be in your own base to do that.");
							return;
						}
					}
					else if(color.equalsIgnoreCase("blue")){
						Location blueSpawnLoc = plugin.worldHandler.getWorldLocations(player.getWorld().getName()).getBlueSpawnLocations().get(0);
						if(player.getLocation().distance(blueSpawnLoc) > 50){
							player.sendMessage(ChatColor.GRAY+"You must be in your own base to do that.");
							return;
						}
					}
					
					Base base = plugin.baseHandler.getBaseFromIndicatorLocation(blockLoc.getWorld().getName(), blockLoc);
					if(base == null)
						return;
					player.sendMessage(ChatColor.GRAY+"Teleporting to "+base.getName());
					player.teleport(base.getWarpLocation());
				}
			}
		}			
	}
	
	private HashMap<String, ArrayList<Location>> countGroundColors(ArrayList<Location> groundLocations){
		
		HashMap<String, ArrayList<Location>> groundLocsMap = new HashMap<String, ArrayList<Location>>();
		ArrayList<Location> redLocs = new ArrayList<Location>();
		ArrayList<Location> blueLocs = new ArrayList<Location>();
		ArrayList<Location> whiteLocs = new ArrayList<Location>();

		for (Location l: groundLocations) {
			if(l.getBlock().getData() == DyeColor.RED.getWoolData())
				redLocs.add(l.clone());
			else if(l.getBlock().getData() == DyeColor.BLUE.getWoolData())
				blueLocs.add(l.clone());
			else
				whiteLocs.add(l.clone());
		}
		groundLocsMap.put("red", redLocs);
		groundLocsMap.put("blue", blueLocs);
		groundLocsMap.put("white", whiteLocs);
		return groundLocsMap;
	}
}
