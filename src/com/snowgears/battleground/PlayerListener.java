package com.snowgears.battleground;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;

import com.snowgears.battleground.api.StatsAPI;
import com.snowgears.battleground.customevents.PlayerLeaveBattlegroundEvent;
import com.snowgears.battleground.stats.PlayerStats;

public class PlayerListener implements Listener{

	public Battleground plugin = Battleground.plugin;

	public PlayerListener(Battleground instance)
    {
        plugin = instance;
    }
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		FileConfiguration config = null;
		File file = new File("plugins"+File.separator+"Battleground"+File.separator+"Data"+File.separator+"Players"+File.separator+event.getPlayer().getName()+".yml");
		if(!file.exists()){
		        try {
					file.createNewFile();
				} catch (IOException e1) {
					System.out.println("[Battleground] Something went wrong while creating player, "+player.getName()+"'s file. Please submit a ticket to the Battleground project.");
				}
		        config = YamlConfiguration.loadConfiguration(file);
		        config.set("kills", 0);
		        config.set("deaths", 0);
		        config.set("dominationCaptures", 0);
		        config.set("dominationDefends", 0);
		        config.set("dominationAssaults", 0);
		        try {
					config.save(file);
				} catch (IOException e) {
					System.out.println("[Battleground] Something went wrong while saving player, "+player.getName()+"'s file. Please submit a ticket to the Battleground project.");
				}
		}
		config = YamlConfiguration.loadConfiguration(file);
		PlayerStats pStats = new PlayerStats(player, file, config);
		plugin.playerStatsHandler.addPlayerStats(pStats);
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event){
		Player player = event.getPlayer();
		
		if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			PlayerLeaveBattlegroundEvent e = new PlayerLeaveBattlegroundEvent(plugin, player);
			Bukkit.getServer().getPluginManager().callEvent(e);
		}
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event){
		Player player = event.getPlayer();

		if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			PlayerLeaveBattlegroundEvent e = new PlayerLeaveBattlegroundEvent(plugin, player);
			Bukkit.getServer().getPluginManager().callEvent(e);
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
		Player player = event.getEntity();

		if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			StatsAPI.addDeath(player);
			StatsAPI.addKill(player.getKiller());
			Location loc = player.getLocation();
			loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.BONE));
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerRepawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			if(plugin.playerManager.getRedTeam().contains(player.getName())){
				ArrayList<Location> redSpawnLocs = plugin.worldHandler.getWorldLocations(player.getWorld().getName()).getRedSpawnLocations();
				Collections.shuffle(redSpawnLocs);
				
				Location toPort = redSpawnLocs.get(0);
				//toPort.setYaw((toPort.getYaw()+90)%360);
				event.setRespawnLocation(toPort);
			}
			else if(plugin.playerManager.getBlueTeam().contains(player.getName())){
				ArrayList<Location> blueSpawnLocs = plugin.worldHandler.getWorldLocations(player.getWorld().getName()).getBlueSpawnLocations();
				Collections.shuffle(blueSpawnLocs);
				
				Location toPort = blueSpawnLocs.get(0);
				//toPort.setYaw((toPort.getYaw()+270)%360);
				event.setRespawnLocation(toPort);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void playerToggleFlight(PlayerToggleFlightEvent event){
		Player player = event.getPlayer();
		if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			// player is trying to fly but does not have permission
			if(event.isFlying()==false && Battleground.usePerms && !player.hasPermission("battleground.allowflight")){
				player.sendMessage(ChatColor.DARK_RED+"You are not authorized to fly in the battleground.");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void playerChangeGamemode(PlayerGameModeChangeEvent event){
		Player player = event.getPlayer();
		if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			// player is trying to change gamemodes but does not have permission
			if(event.getNewGameMode()==GameMode.CREATIVE && Battleground.usePerms && !player.hasPermission("battleground.allowcreative")){
				player.sendMessage(ChatColor.DARK_RED+"You are not authorized to use creative mode in the battleground.");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event){
	    Player player = event.getPlayer();
	    
	    String worldName = player.getWorld().getName();

	    //the chat was sent from the battleground, send it to correct team
	    if(worldName.equals(plugin.worldHandler.getCurrentWorld())){
	    	if(plugin.playerManager.getRedTeam().contains(player.getName())){
	    		for(Player p : player.getWorld().getPlayers()){
	    			//red team does not have this player
	    			if( ! (plugin.playerManager.getRedTeam().contains(p.getName())))
	    				event.getRecipients().remove(p);
	    		}
	    	}
	    	else if(plugin.playerManager.getBlueTeam().contains(player.getName())){
	    		for(Player p : player.getWorld().getPlayers()){
	    			//blue team does not have this player
	    			if( ! (plugin.playerManager.getBlueTeam().contains(p.getName())))
	    				event.getRecipients().remove(p);
	    		}
	    	}
	    	else if(plugin.playerManager.getSpectators().contains(player.getName())){
	    		for(Player p : player.getWorld().getPlayers()){
	    			//spectators does not have this player
	    			if( ! (plugin.playerManager.getSpectators().contains(p.getName())))
	    				event.getRecipients().remove(p);
	    		}
	    	}
	    }
	    else{
	    	//prevent the chat from going to players in the battleground
	    	for(Player p : plugin.getServer().getOnlinePlayers()){
	    		if(p.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld()))
	    			event.getRecipients().remove(p);
	    	}
	    }
	}
	
	//====================================================================================================================
	// UNBREAKABLE CODE
	//====================================================================================================================
	
	public void onArmorDamage(EntityDamageEvent event)
    {
        if(event.getEntity().getType() == EntityType.PLAYER)
        {
            Player player = (Player)event.getEntity();
            ItemStack aitemstack[];
            int j = (aitemstack = player.getInventory().getArmorContents()).length;
            for(int i = 0; i < j; i++)
            {
                ItemStack is = aitemstack[i];
                if(is.getType() != Material.WOOL)
                {
                    is.setDurability((short)-10);
                    player.updateInventory();
                }
            }

        }
    }

    public void onWeaponUse(EntityDamageByEntityEvent event)
    {
        if(event.getDamager().getType() == EntityType.PLAYER)
        {
            Player player = (Player)event.getDamager();
            if(player.getItemInHand().getType() != Material.WOOL)
            {
                player.getItemInHand().setDurability((short)-10);
                player.updateInventory();
            }
        }
    }

    public void onToolUse(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if(player.getItemInHand().getType() != Material.WOOL)
        {
            player.getItemInHand().setDurability((short)-10);
            player.updateInventory();
        }
    }

    public void onBowUse(EntityShootBowEvent event)
    {
        if(event.getEntity().getType() == EntityType.PLAYER)
        {
            Player player = (Player)event.getEntity();
            player.getItemInHand().setDurability((short)-10);
            player.updateInventory();
        }
    }
}
