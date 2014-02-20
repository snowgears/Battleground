package com.snowgears.battleground;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerEditListener implements Listener{
	
		public Battleground plugin = Battleground.plugin;
	
		public HashMap<String, ArrayList<Location>> playerEditLocations = new HashMap<String, ArrayList<Location>>();
		public HashMap<String, Location[]> playerKeyLocations = new HashMap<String, Location[]>();
		
		public PlayerEditListener(Battleground instance)
	    {
	        plugin = instance;
	    }
		
		@EventHandler
		public void onBlockRightClick(PlayerInteractEvent event){
			Player player = event.getPlayer();
			if(!plugin.worldHandler.getAllBattleWorlds().contains(event.getPlayer().getWorld().getName()))
				return;
			if(event.getAction() == Action.LEFT_CLICK_BLOCK){
				Location clicked = event.getClickedBlock().getLocation();
				if(player.getItemInHand().getType() == Material.BLAZE_ROD){
					Location[] keyLocs = getKeyLocations(player);
					keyLocs[0] = clicked;
					playerKeyLocations.put(player.getName(), keyLocs);
					player.sendMessage(ChatColor.GOLD+"[Battleground]"+ChatColor.YELLOW+" First position set to ("+clicked.getBlockX()+", "+clicked.getBlockY()+", "+clicked.getBlockZ()+").");
					if(keyLocs[1] != null){
						ArrayList<Location> cuboid = getCuboidRegion(player,clicked, keyLocs[1]);
						playerEditLocations.put(player.getName(), cuboid);
						player.sendMessage(ChatColor.GRAY+"Use '/bg [set,add,remove] b:<BaseName> a:<AreaName>' to edit base locations.");
						player.sendMessage(ChatColor.GRAY+"Use '/bg [set,add,remove] s:<Red/Blue>' to edit spawn locations.");
						player.sendMessage(ChatColor.GRAY+"Use '/bg [set,add,remove] gates' to edit gate locations.");
					}
				}
			}
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
				Location clicked = event.getClickedBlock().getLocation();
				if(player.getItemInHand().getType() == Material.BLAZE_ROD){
					Location[] keyLocs = getKeyLocations(player);
					keyLocs[1] = clicked;
					playerKeyLocations.put(player.getName(), keyLocs);
					player.sendMessage(ChatColor.GOLD+"[Battleground]"+ChatColor.YELLOW+" Second position set to ("+clicked.getBlockX()+", "+clicked.getBlockY()+", "+clicked.getBlockZ()+").");
					if(keyLocs[0] != null){
						ArrayList<Location> cuboid = getCuboidRegion(player,clicked, keyLocs[0]);
						playerEditLocations.put(player.getName(), cuboid);
						player.sendMessage(ChatColor.GRAY+"Use '/bg [set,add,remove] b:<BaseName> a:<AreaName>' to edit base locations.");
						player.sendMessage(ChatColor.GRAY+"Use '/bg [set,add,remove] s:<Red/Blue>' to edit spawn locations.");
						player.sendMessage(ChatColor.GRAY+"Use '/bg [set,add,remove] gates' to edit gate locations.");
					}
				}
			}
		}
		
		public void clearLocations(Player player){
			if(playerEditLocations.containsKey(player.getName())){
		    	playerEditLocations.put(player.getName(), new ArrayList<Location>());
			}
			if(playerKeyLocations.containsKey(player.getName())){
				playerKeyLocations.put(player.getName(), new Location[2]);
			}
		}

		public ArrayList<Location> getEditLocations(Player player)
		{
		    if(playerEditLocations.containsKey(player.getName())){
		    	ArrayList<Location> selectedLocs = playerEditLocations.get(player.getName());
		    	for(Location l : selectedLocs){
		    		l.setPitch(player.getLocation().getPitch());
		    		l.setYaw(player.getLocation().getYaw());
		    	}
		    	return selectedLocs;
		    }
		    else
		    	return new ArrayList<Location>();
		}
		
		public Location[] getKeyLocations(Player player)
		{
		    if(playerKeyLocations.containsKey(player.getName())){
		    	return playerKeyLocations.get(player.getName());
		    }
		    else
		    	return new Location[2];
		}
		
		@EventHandler
		public void breakBlockWhileSettingLocations(BlockBreakEvent event){
			Player player = event.getPlayer();
			Material mat = player.getItemInHand().getType();
			if(player.getGameMode() == GameMode.CREATIVE && mat == Material.BLAZE_ROD){
				event.setCancelled(true);
				return;
			}
		}
		
		public ArrayList<Location> getCuboidRegion(Player p, Location loc1, Location loc2){
			 ArrayList<Location> allLocations = new ArrayList<Location>();
			 World world = p.getWorld();
			 int count = 0;
			 int minx = Math.min(loc1.getBlockX(), loc2.getBlockX()),
				        miny = Math.min(loc1.getBlockY(), loc2.getBlockY()),
				        minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ()),
				        maxx = Math.max(loc1.getBlockX(), loc2.getBlockX()),
				        maxy = Math.max(loc1.getBlockY(), loc2.getBlockY()),
				        maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
				    for(int x = minx; x<=maxx;x++){
				        for(int y = miny; y<=maxy;y++){
				            for(int z = minz; z<=maxz;z++){
				                Location loc = new Location(world,x,y,z);
				                allLocations.add(loc);
				                count++;
				            }
				        }
				    }
				    p.sendMessage(ChatColor.GOLD+"[Battleground]"+ChatColor.WHITE+" "+count+ChatColor.YELLOW+" blocks selected.");
				    return allLocations;
		}
}
