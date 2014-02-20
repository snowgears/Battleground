package com.snowgears.battleground.domination;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.mcsg.double0negative.tabapi.TabAPI;

import com.snowgears.battleground.Battleground;

public class DominationGame {

	public Battleground plugin = Battleground.plugin;
	public boolean gameInProgress = false;
	
	private int redScore = 0;
	private int blueScore = 0;
	private int scoreTask = 0;
	
	public DominationGame(Battleground instance){
		plugin = instance;
	}
	
	public void startGame(final String world){
		plugin.baseHandler.resetBases(world);
		gameInProgress = true;
		plugin.worldHandler.setCurrentWorld(world);
		for(int i = 0; i<Bukkit.getServer().getWorld(world).getPlayers().size(); i++){
			Bukkit.getServer().getWorld(world).getPlayers().get(i).sendMessage(ChatColor.YELLOW+"The battle will begin in 1 minute!");
		}
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { 
			public void run() { 
				for(Player p : Bukkit.getServer().getWorld(world).getPlayers()){
					p.sendMessage(ChatColor.YELLOW+"The battle will begin in 30 seconds!");
				}
			} 
		}, 600L); // wait 30 seconds
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { 
			public void run() { 
				for(Player p : Bukkit.getServer().getWorld(world).getPlayers()){
					p.sendMessage(ChatColor.YELLOW+"The battle will begin in 10 seconds!");
				}
			} 
		}, 1000L); // wait 50 seconds
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { 
			public void run() { 
				for(Player p : Bukkit.getServer().getWorld(world).getPlayers()){
					p.sendMessage(ChatColor.YELLOW+"The Battle Has Begun!");
				}
				for(Location loc : plugin.worldHandler.getWorldLocations(world).getGateLocations()){
					loc.getBlock().setType(Material.AIR);
				}
			} 
		}, 1200L); // wait 60 seconds		
		
		scoreTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() { 
			public void run() { 
				int redBases = plugin.baseHandler.getNumberOfRedBases(world);
				int blueBases = plugin.baseHandler.getNumberOfBlueBases(world);
				
				if(redBases == 1)
					redScore = redScore + 8;
				else if(redBases == 2)
					redScore = redScore + 11;
				else if(redBases == 3)
					redScore = redScore + 17;
				else if(redBases == 4)
					redScore = redScore + 33;
				else if(redBases == 5)
					redScore = redScore + 300;
				
				if(blueBases == 1)
					blueScore = blueScore + 8;
				else if(blueBases == 2)
					blueScore = blueScore + 11;
				else if(blueBases == 3)
					blueScore = blueScore + 17;
				else if(blueBases == 4)
					blueScore = blueScore + 33;
				else if(blueBases == 5)
					blueScore = blueScore + 300;
				
				if(redScore >= 1600 || blueScore >= 1600){
					Bukkit.getServer().getScheduler().cancelTask(scoreTask);
					if(redScore > blueScore){
						for(Player p : Bukkit.getServer().getWorld(world).getPlayers()){
							updateTabStats(p, 1600, blueScore);
						}
					}
					else{
						for(Player p : Bukkit.getServer().getWorld(world).getPlayers()){
							updateTabStats(p, redScore, 1600);
						}
					}
					endGame();
				}
				else{
					for(Player p : Bukkit.getServer().getWorld(world).getPlayers()){
						updateTabStats(p, redScore, blueScore);
					}
				}
			} 
		}, 1400L, 200L); // wait 70 seconds, then start task every 10 seconds
	}
	
	public void endGame(){
		//NEED TO GET SCORES AND WINNING TEAM AND DISPLAY HERE

		if(redScore > blueScore)
			for(Player p : Bukkit.getServer().getWorld(plugin.worldHandler.getCurrentWorld()).getPlayers()){
				p.sendMessage(ChatColor.YELLOW+"The "+ChatColor.RED+"Red Team"+ChatColor.YELLOW+" Has Won!"); 
			}
		else
			for(Player p : Bukkit.getServer().getWorld(plugin.worldHandler.getCurrentWorld()).getPlayers()){
				p.sendMessage(ChatColor.YELLOW+"The "+ChatColor.BLUE+"Blue Team"+ChatColor.YELLOW+" Has Won!"); 
			}
		
		Bukkit.getServer().getScheduler().cancelTask(scoreTask);
		
		//DISPLAY INDIVIDUAL PLAYER STATS HERE
		
		for(String s : plugin.playerManager.getRedTeam()){
			if(Bukkit.getPlayer(s) != null){
				Player p = Bukkit.getPlayer(s);
				p.sendMessage(ChatColor.DARK_RED+"You will be returned to your world after 1 minute."); 
				p.sendMessage(ChatColor.GRAY+"To leave early type /bg leave");
			}
		}
		for(String s : plugin.playerManager.getBlueTeam()){
			if(Bukkit.getPlayer(s) != null){
				Player p = Bukkit.getPlayer(s);
				p.sendMessage(ChatColor.DARK_RED+"You will be returned to your world after 1 minute."); 
				p.sendMessage(ChatColor.GRAY+"To leave early type /bg leave");
			}
		}
		
		plugin.playerManager.getRedTeam().clear();
		plugin.playerManager.getBlueTeam().clear();
		plugin.playerManager.getSpectators().clear();
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { 
			public void run() { 
				
				gameInProgress = false;
				redScore = 0;
				blueScore = 0;
				
				plugin.worldHandler.resetWorld(plugin.worldHandler.getCurrentWorld());
				
				for(String s : plugin.playerManager.getRedTeam()){
					Player p = Bukkit.getPlayer(s);
					plugin.playerDataHandler.returnDataToPlayer(p);
				}
				for(String s : plugin.playerManager.getBlueTeam()){
					Player p = Bukkit.getPlayer(s);
					plugin.playerDataHandler.returnDataToPlayer(p);
				}
				for(String s : plugin.playerManager.getSpectators()){
					Player p = Bukkit.getPlayer(s);
					plugin.playerDataHandler.returnDataToPlayer(p);
				}
				plugin.worldHandler.setCurrentWorld("");
			} 
		}, 1200L); // wait 60 seconds, then teleport all players to their previous locations
	}
	
	public void updateTabStats(Player player, int redScore, int blueScore){
		
		String world = player.getWorld().getName();
		int maxPlayers = player.getServer().getMaxPlayers();
		if(maxPlayers > 50){
			TabAPI.setPriority(plugin, player, 2);
			TabAPI.updatePlayer(player); //update player after priority change so plugin with priority gets displayed

			TabAPI.setTabString(plugin, player, 0, 0, ChatColor.RED+"Red Team");
			TabAPI.setTabString(plugin, player, 0, 2, ChatColor.BLUE+"Blue Team");
		
			TabAPI.setTabString(plugin, player, 3, 1, ChatColor.YELLOW+"Total Players");
			TabAPI.setTabString(plugin, player, 4, 1, ChatColor.WHITE + "" + (plugin.playerManager.getRedTeam().size() + plugin.playerManager.getBlueTeam().size())+"/"+Battleground.basinMaxPlayers);
		
			TabAPI.setTabString(plugin, player, 1, 0,ChatColor.YELLOW+"Score; "+ ChatColor.RED + redScore);
			TabAPI.setTabString(plugin, player, 1, 2, ChatColor.YELLOW+"Score; "+ ChatColor.BLUE + blueScore);
	 	
			TabAPI.setTabString(plugin, player, 2, 0, ChatColor.YELLOW+"Bases; "+ ChatColor.RED + plugin.baseHandler.getNumberOfRedBases(world));
			TabAPI.setTabString(plugin, player, 2, 2, ChatColor.YELLOW+"Bases; "+ ChatColor.BLUE + plugin.baseHandler.getNumberOfBlueBases(world));
		
			TabAPI.setTabString(plugin, player, 6, 1, ChatColor.YELLOW+"Spectators");
			TabAPI.setTabString(plugin, player, 7, 1, ChatColor.WHITE+""+plugin.playerManager.getSpectators().size());

			TabAPI.setTabString(plugin, player, 5, 0, ChatColor.YELLOW+"Team Size; "+ ChatColor.RED + plugin.playerManager.getRedTeam().size());
			TabAPI.setTabString(plugin, player, 5, 2, ChatColor.YELLOW+"Team Size; "+ ChatColor.BLUE + plugin.playerManager.getBlueTeam().size());

			TabAPI.updatePlayer(player);
		}
		else if(maxPlayers > 20){
			TabAPI.setPriority(plugin, player, 2);
			TabAPI.updatePlayer(player); //update player after priority change so plugin with priority gets displayed

			TabAPI.setTabString(plugin, player, 0, 0, ChatColor.RED+"Red Team");
			TabAPI.setTabString(plugin, player, 0, 1, ChatColor.BLUE+"Blue Team");
			
			TabAPI.setTabString(plugin, player, 1, 1,ChatColor.YELLOW+"Score; "+ ChatColor.RED + redScore);
			TabAPI.setTabString(plugin, player, 1, 2, ChatColor.YELLOW+"Score; "+ ChatColor.BLUE + blueScore);
			
			TabAPI.setTabString(plugin, player, 2, 0, ChatColor.YELLOW+"Bases; "+ ChatColor.RED + plugin.baseHandler.getNumberOfRedBases(world));
			TabAPI.setTabString(plugin, player, 2, 1, ChatColor.YELLOW+"Bases; "+ ChatColor.BLUE + plugin.baseHandler.getNumberOfBlueBases(world));

			TabAPI.setTabString(plugin, player, 4, 0, ChatColor.YELLOW+"Team Size; "+ ChatColor.RED + plugin.playerManager.getRedTeam().size());
			TabAPI.setTabString(plugin, player, 4, 1, ChatColor.YELLOW+"Team Size; "+ ChatColor.BLUE + plugin.playerManager.getBlueTeam().size());

			TabAPI.updatePlayer(player);
		}
		else{
			TabAPI.setPriority(plugin, player, 2);
			TabAPI.updatePlayer(player); //update player after priority change so plugin with priority gets displayed

			TabAPI.setTabString(plugin, player, 0, 0, ChatColor.RED+"Red Team");
			TabAPI.setTabString(plugin, player, 0, 1,ChatColor.YELLOW+"Score; "+ ChatColor.RED + redScore);
			TabAPI.setTabString(plugin, player, 0, 2, ChatColor.YELLOW+"Bases; "+ ChatColor.RED + plugin.baseHandler.getNumberOfRedBases(world));
			
			TabAPI.setTabString(plugin, player, 1, 1, ChatColor.BLUE+"Blue Team");
			TabAPI.setTabString(plugin, player, 1, 2, ChatColor.YELLOW+"Score; "+ ChatColor.BLUE + blueScore);
			TabAPI.setTabString(plugin, player, 2, 0, ChatColor.YELLOW+"Bases; "+ ChatColor.BLUE + plugin.baseHandler.getNumberOfBlueBases(world));

			TabAPI.updatePlayer(player);
		}
	}
	
	public void promptAllPlayersToEnterBattleground(){
		Collections.shuffle(plugin.playerManager.getQueue());
		for(String s : plugin.playerManager.getQueue()){
			Player p = plugin.getServer().getPlayer(s);
			if(p != null){
				p.sendMessage(ChatColor.YELLOW+"The battleworld "+ChatColor.GOLD+plugin.worldHandler.getCleanWorldName(plugin.worldHandler.getCurrentWorld())+ChatColor.YELLOW+" has been selected.");
				p.sendMessage(ChatColor.YELLOW+"The battleground is ready. Type "+ ChatColor.GOLD +"/bg accept"+ ChatColor.YELLOW +" to join the battle.");
				p.sendMessage(ChatColor.GRAY+"You have 1 minute to accept.");
			
				Battleground.playersReadyForBG.add(p.getName()); // ready to accept
             // VotingManager will set a current world for the battle
			}
		}
		plugin.playerManager.getQueue().clear();
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { 
			public void run() { 
				Battleground.playersReadyForBG.clear();
				plugin.dominationGame.startGame(plugin.worldHandler.getCurrentWorld()); //==============================THIS STRING WILL LATER BE CHANGED DEPENDING ON VOTES FOR THE MAP AND GAMEMODE===========================================
				} 
		}, 1200L); // one minute
	}
	
	public void promptPlayerToEnterBattleground(Player player, boolean isSpectator){
		if(isSpectator){
			player.sendMessage(ChatColor.YELLOW+"There is currently a battle in "+plugin.worldHandler.getCleanWorldName(plugin.worldHandler.getCurrentWorld())+" but it is full.");
			player.sendMessage(ChatColor.YELLOW+"You may watch the battle until there a space opens up for you. Type "+ ChatColor.GOLD +"/bg accept"+ ChatColor.YELLOW +" to join as a spectator.");
		}
		else{
			player.sendMessage(ChatColor.YELLOW+"There is currently a battle in "+plugin.worldHandler.getCleanWorldName(plugin.worldHandler.getCurrentWorld()));
			player.sendMessage(ChatColor.YELLOW+"Type "+ ChatColor.GOLD +"/bg accept"+ ChatColor.YELLOW +" to join the battle.");
		}
		
		player.sendMessage(ChatColor.GRAY+"You have 1 minute to accept.");
	
		Battleground.playersReadyForBG.add(player.getName()); // ready to accept
		plugin.playerManager.getQueue().remove(player.getName()); // no longer in waiting
	}
	
	public void givePlayerHealer(Player player){
		player.getInventory().clear();
		
		player.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
		player.getInventory().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
		player.getInventory().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
		player.getInventory().setBoots(new ItemStack(Material.GOLD_BOOTS));
		
		Potion potion = new Potion(PotionType.INSTANT_HEAL, 1, true, false);
		ItemStack potionstack = potion.toItemStack(1);
		player.getInventory().addItem(potionstack);
		
		Potion potion1 = new Potion(PotionType.REGEN, 1, true, false);
		ItemStack potionstack1 = potion1.toItemStack(1);
		player.getInventory().addItem(potionstack1);
		
		player.getInventory().addItem(new ItemStack(Material.FEATHER));
		
		player.sendMessage(ChatColor.GRAY+"You have chosen to be a Healer");
	}
	
	public void givePlayerMelee(Player player){
		player.getInventory().clear();
		
		player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
		player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
		player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
		player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
		
		player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
		
		player.getInventory().addItem(new ItemStack(Material.FEATHER));
		
		player.sendMessage(ChatColor.GRAY+"You have chosen to be a Warrior");
	}
	
	public void givePlayerArcher(Player player){
		player.getInventory().clear();
		
		player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
		player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
		player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
		player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
		
		player.getInventory().addItem(new ItemStack(Material.BOW));
		player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
		
		player.getInventory().addItem(new ItemStack(Material.FEATHER));
		
		player.sendMessage(ChatColor.GRAY+"You have chosen to be an Archer");
	}
	
	public void givePlayerTank(Player player){
		player.getInventory().clear();
		
		player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
		player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
		
		player.getInventory().addItem(new ItemStack(Material.GOLD_SWORD));
		
		player.getInventory().addItem(new ItemStack(Material.FEATHER));
		
		player.sendMessage(ChatColor.GRAY+"You have chosen to be a Tank");
	}
}
