package com.snowgears.battleground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.kitteh.tag.PlayerReceiveNameTagEvent;
import org.mcsg.double0negative.tabapi.TabAPI;

import com.snowgears.battleground.customevents.PlayerJoinBattlegroundEvent;
import com.snowgears.battleground.customevents.PlayerLeaveBattlegroundEvent;
import com.snowgears.battleground.domination.AreaContains;
import com.snowgears.battleground.domination.Base;
import com.snowgears.battleground.domination.BaseHandler;


public class BattleworldListener implements Listener{
	
	
	public Battleground plugin = Battleground.plugin;

	public BattleworldListener(Battleground instance)
    {
        plugin = instance;
    }
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onNameTag(PlayerReceiveNameTagEvent event) {
		//if person who is seeing the tag is on the red team and person who is getting the tag is on the blue team
		if (plugin.playerManager.getRedTeam().contains(event.getPlayer().getName()) && plugin.playerManager.getBlueTeam().contains(event.getNamedPlayer().getName())) {
			event.setTag(ChatColor.RED + event.getNamedPlayer().getDisplayName());
		}
		else if (plugin.playerManager.getBlueTeam().contains(event.getPlayer().getName()) && plugin.playerManager.getRedTeam().contains(event.getNamedPlayer().getName())) {
			event.setTag(ChatColor.RED + event.getNamedPlayer().getDisplayName());
		}
		else if (plugin.playerManager.getBlueTeam().contains(event.getPlayer().getName()) && plugin.playerManager.getBlueTeam().contains(event.getNamedPlayer().getName())) {
			event.setTag(ChatColor.GREEN + event.getNamedPlayer().getDisplayName());
		}
		else if (plugin.playerManager.getRedTeam().contains(event.getPlayer().getName()) && plugin.playerManager.getRedTeam().contains(event.getNamedPlayer().getName())) {
			event.setTag(ChatColor.GREEN + event.getNamedPlayer().getDisplayName());
		}
	}
	
	@EventHandler
	public void breakItemFrame(HangingBreakByEntityEvent event){
		if(event.getEntity().getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			if(event.getRemover() instanceof Player){
				Player p = (Player)event.getRemover();
				if(p.getGameMode() != GameMode.CREATIVE)
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onFrameClick(PlayerInteractEntityEvent event){
		Player player = event.getPlayer();
        if(event.getRightClicked().getType() == EntityType.ITEM_FRAME){
        	ItemFrame itemFrame = (ItemFrame)event.getRightClicked();

        	if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
        		if(player.getGameMode() != GameMode.CREATIVE)
        			event.setCancelled(true);
        	}
        	if(itemFrame.getItem() == null)
        		return;

        	Material item = itemFrame.getItem().getType();
        	
        	if(item == Material.POTION){
        		plugin.dominationGame.givePlayerHealer(player);
        	}
        	else if(item == Material.IRON_SWORD){
        		plugin.dominationGame.givePlayerMelee(player);
        	}
        	else if(item == Material.BOW){
        		plugin.dominationGame.givePlayerArcher(player);
        	}
        	else if(item == Material.DIAMOND_CHESTPLATE){
        		plugin.dominationGame.givePlayerTank(player);
        	}
        }
	}
	
	@EventHandler
	public void joinBattleground(PlayerJoinBattlegroundEvent event){
		Player player = event.getPlayer();
		
		plugin.playerDataHandler.savePlayerData(player);

		//joining blue team
		if(event.getTeam().equals("blue")){
			plugin.playerManager.getBlueTeam().add(player.getName());
			ArrayList<Location> blueSpawnLocs = plugin.worldHandler.getWorldLocations(event.getNewWorld()).getBlueSpawnLocations();
			Collections.shuffle(blueSpawnLocs);
			
			Location toPort = blueSpawnLocs.get(0);
			player.teleport(toPort);
		}
		//joining red team
		else if(event.getTeam().equals("red")){
			plugin.playerManager.getRedTeam().add(player.getName());
			ArrayList<Location> redSpawnLocs = plugin.worldHandler.getWorldLocations(event.getNewWorld()).getRedSpawnLocations();
			Collections.shuffle(redSpawnLocs);
			
			Location toPort = redSpawnLocs.get(0);
			player.teleport(toPort);
		}
		//joining as a spectator
		else{
			PotionEffect pe = new PotionEffect(PotionEffectType.INVISIBILITY, 30000000, 1);
			player.addPotionEffect(pe, true);
			plugin.playerManager.getSpectators().add(player.getName());
			player.teleport(plugin.getServer().getWorld(plugin.worldHandler.getCurrentWorld()).getSpawnLocation());
		}
		
		player.setGameMode(GameMode.SURVIVAL);
		
		if(plugin.playerManager.getSpectators().contains(player.getName())){
			player.setAllowFlight(true);
		}

		plugin.dominationGame.updateTabStats(player, 0, 0);
	}
	
	@EventHandler
	public void leaveBattleground(PlayerLeaveBattlegroundEvent event){
		Player player = event.getPlayer();
		
		if(plugin.playerManager.getQueue().contains(player.getName())){
			plugin.playerManager.getQueue().remove(player.getName());
			player.sendMessage(ChatColor.GRAY+"You have left the queue for the battleground.");
		}
		//they are in the battleground and want to leave
		else if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			
			for (PotionEffect effect : player.getActivePotionEffects())
		        player.removePotionEffect(effect.getType());
			
			if(plugin.playerManager.getRedTeam().contains(player.getName()))
				plugin.playerManager.getRedTeam().remove(player.getName());
			else if(plugin.playerManager.getBlueTeam().contains(player.getName()))
				plugin.playerManager.getBlueTeam().remove(player.getName());
			else if(plugin.playerManager.getSpectators().contains(player.getName()))
				plugin.playerManager.getSpectators().remove(player.getName());

			plugin.playerDataHandler.returnDataToPlayer(player);
			
			//set player tab back to normal
			TabAPI.setPriority(plugin, player, -2);
			TabAPI.updatePlayer(player);
			TabAPI.disableTabForPlayer(player);
			
			for(int i = 0; i<Bukkit.getServer().getWorld(plugin.worldHandler.getCurrentWorld()).getPlayers().size(); i++){
				Bukkit.getServer().getWorld(plugin.worldHandler.getCurrentWorld()).getPlayers().get(i).sendMessage(ChatColor.YELLOW + player.getName() + " has left the battle.");
			}
			
			//get first player from spectators and place in emptier team
			Player toFill = null;
			if(plugin.playerManager.getSpectators().size()>0){
				toFill = Bukkit.getPlayer(plugin.playerManager.getSpectators().get(0)); 
				plugin.playerManager.getSpectators().remove(toFill.getName());
				
				//red team has more players
				if(plugin.playerManager.getRedTeam().size() >= plugin.playerManager.getBlueTeam().size()){ //SHOULD DO A JOIN BATTLE EVENT AND CALL HERE
					plugin.playerManager.getBlueTeam().add(toFill.getName());
					ArrayList<Location> blueSpawnLocs = plugin.worldHandler.getWorldLocations(player.getWorld().getName()).getBlueSpawnLocations();
					Collections.shuffle(blueSpawnLocs);
					
					Location toPort = blueSpawnLocs.get(0);
					//toPort.setYaw((toPort.getYaw()+270)%360);
					player.teleport(toPort);
				}
				//blue team has more players
				else{
					plugin.playerManager.getRedTeam().add(toFill.getName());
					ArrayList<Location> redSpawnLocs = plugin.worldHandler.getWorldLocations(player.getWorld().getName()).getRedSpawnLocations();
					Collections.shuffle(redSpawnLocs);
					
					Location toPort = redSpawnLocs.get(0);
					//toPort.setYaw((toPort.getYaw()+90)%360);
					toFill.teleport(toPort);
				}
			}
			//if no spectators, take a player from the queue
			else if(plugin.playerManager.getQueue().size()>0){
				toFill = Bukkit.getPlayer(plugin.playerManager.getQueue().get(0)); //NEED TO ASK PLAYER TO JOIN HERE + SAVE STUFF
				
				toFill.sendMessage(ChatColor.YELLOW+"The battleground is ready. Type "+ ChatColor.GOLD +"/bg accept"+ ChatColor.YELLOW +" to join the battle.");
				toFill.sendMessage(ChatColor.GRAY+"You have 1 minute to accept.");
				
				Battleground.playersReadyForBG.add(toFill.getName()); // ready to accept
				plugin.playerManager.getQueue().remove(toFill.getName()); // no longer in waiting
				
				final String playerName = toFill.getName();
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { 
					public void run() { 
						Battleground.playersReadyForBG.remove(playerName);
						} 
				}, 1200L); // one minute
			}
			
			if(toFill == null)
				return;

			for(int i = 0; i<Bukkit.getServer().getWorld(plugin.worldHandler.getCurrentWorld()).getPlayers().size(); i++){
				Bukkit.getServer().getWorld(plugin.worldHandler.getCurrentWorld()).getPlayers().get(i).sendMessage(ChatColor.YELLOW + toFill.getName() + " has joined the battle.");
			}
		}
		else if(plugin.worldHandler.getAllBattleWorlds().contains(player.getWorld().getName())){
			plugin.playerDataHandler.returnDataToPlayer(player);
		}
		//they are not in queue for battleground
		else{
			player.sendMessage(ChatColor.RED+"You are not in the queue for the battleground.");
		}
		
		if((plugin.playerManager.getRedTeam().size() == 0 || plugin.playerManager.getBlueTeam().size() == 0) && plugin.dominationGame.gameInProgress){
			plugin.dominationGame.endGame();
		}
	}
	
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event){ //players can only damage the other team in the battleground
		if( ! (event.getEntity().getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())))
			return;
		if( ! (event.getEntity() instanceof Player && event.getDamager() instanceof Player))
			return;
		Player damaged = (Player)event.getEntity();
		Player damager = (Player)event.getDamager();
		
		if(plugin.playerManager.getSpectators().contains(damaged.getName()))
			event.setCancelled(true);
		else if(plugin.playerManager.getSpectators().contains(damager.getName()))
			event.setCancelled(true);
		else if(plugin.playerManager.getRedTeam().contains(damaged.getName()) && plugin.playerManager.getRedTeam().contains(damager.getName()))
			event.setCancelled(true);
		else if(plugin.playerManager.getBlueTeam().contains(damaged.getName()) && plugin.playerManager.getBlueTeam().contains(damager.getName()))
			event.setCancelled(true);
	}
	
	//=================================================================================================================
	/* BATTLEGROUND NECESSARY WORLD IMPROVEMENTS */
	//=================================================================================================================
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event){
		if(plugin.worldHandler.getAllBattleWorlds().contains(event.getEntity().getWorld().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event){
		//weather is being set to raining in battleground world
		if(plugin.worldHandler.getAllBattleWorlds().contains(event.getWorld().getName()) && event.toWeatherState() == true)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onExplosion(EntityExplodeEvent event){
		if(plugin.worldHandler.getAllBattleWorlds().contains(event.getLocation().getWorld().getName()))
			event.blockList().clear();
	}
	
	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event){
		if(plugin.worldHandler.getAllBattleWorlds().contains(event.getEntity().getWorld().getName()))
			event.setCancelled(true);
	}
	
	
	@EventHandler()
    public void blockForm(BlockFormEvent event) {
		if(plugin.worldHandler.getAllBattleWorlds().contains(event.getBlock().getWorld().getName())){
			if(event.getNewState().getType() == Material.ICE) 
				event.setCancelled(true);
			if(event.getNewState().getType() == Material.SNOW) 
				event.setCancelled(true);
		}
	}
	
	@EventHandler()
    public void onBlockBreak(BlockBreakEvent event) {
		if(plugin.worldHandler.getAllBattleWorlds().contains(event.getBlock().getWorld().getName())){
			if(event.getPlayer().getGameMode() != GameMode.CREATIVE) 
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event){
		if(plugin.worldHandler.getAllBattleWorlds().contains(event.getPlayer().getWorld().getName())){
			if(event.getPlayer().getGameMode() != GameMode.CREATIVE)
				event.setCancelled(true);
		}
	}
	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event){
		if(plugin.worldHandler.getAllBattleWorlds().contains(event.getPlayer().getWorld().getName())){
			if(event.getPlayer().getGameMode() != GameMode.CREATIVE)
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event){
		if(plugin.worldHandler.getAllBattleWorlds().contains(event.getWhoClicked().getWorld().getName()))
				event.setCancelled(true);
	}
	
	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent event){
			if(event.getPlayer().getGameMode() != GameMode.CREATIVE && plugin.worldHandler.getAllBattleWorlds().contains(event.getPlayer().getWorld().getName()))
				event.setCancelled(true);
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event){
			if(event.getPlayer().getGameMode() != GameMode.CREATIVE && plugin.worldHandler.getAllBattleWorlds().contains(event.getPlayer().getWorld().getName()))
				event.setCancelled(true);
	}

	//===========================================================================================================//
	// CODING FOR DIFFERENT CLASSES FOR FIGHTING START HERE //
	//===========================================================================================================//
	
	@EventHandler
	public void speedRegen(PlayerInteractEvent event){
		final Player player = event.getPlayer();
		if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			if((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
					&& player.getItemInHand().getType() == Material.FEATHER){
				player.getInventory().remove(Material.FEATHER);
				PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 100, 2); //5 seconds
				speed.apply(player);
				
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
    				public void run() {        //create a delayed event to give player potion
    					player.getInventory().addItem(new ItemStack(Material.FEATHER));
    					player.sendMessage(ChatColor.GRAY+"Your speed ability is ready for use again.");
    					}
    				}, (long)(600)); //30 seconds
				}
			
			}
	}
	
	@EventHandler
	public void healerRegenPotion(PlayerInteractEvent event){
		final Player player = event.getPlayer();
		if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			if((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
					&& player.getItemInHand().getType() == Material.POTION){
				final Potion potion = new Potion(PotionType.INSTANT_HEAL, 1, true, false);
				final Potion potion1 = new Potion(PotionType.REGEN, 1, true, false);
				final ArrayList<Integer> heal = new ArrayList<Integer>();
				final ArrayList<Integer> regen = new ArrayList<Integer>();

				if(player.getItemInHand().equals(potion.toItemStack(1)))
					heal.add(1);
				else if(player.getItemInHand().equals(potion1.toItemStack(1)))
					regen.add(1);
				
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
    				public void run() {        //create a delayed event to give player potion
    					if(heal.size() > 0){
    						ItemStack potionstack = potion.toItemStack(1);
    						player.getInventory().addItem(potionstack);
    					}
    				}
    			}, (long)(60)); //3 seconds
				
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
    				public void run() {        //create a delayed event to give player potion
    					if(regen.size() > 0){
    						ItemStack potionstack = potion1.toItemStack(1);
    						player.getInventory().addItem(potionstack);
    					}
    				}
    			}, (long)(200)); //10 seconds
			}
			
		}
	}
	
	@EventHandler
	public void onHealSplash(PotionSplashEvent event){
		if(! (event.getEntity() instanceof Player))
			return;
		Player player = (Player)event.getEntity();
		
		if( ! (player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())))
			return;

		if(plugin.playerManager.getRedTeam().contains(player.getName())){
			for(Entity e : event.getAffectedEntities()){
				if(e instanceof Player){
					Player lp = (Player)e;
					if(plugin.playerManager.getBlueTeam().contains(lp.getName()))
						event.setIntensity(lp, 0.0);
				}
			}
		}
		else if(plugin.playerManager.getBlueTeam().contains(player.getName())){
			for(Entity e : event.getAffectedEntities()){
				if(e instanceof Player){
					Player lp = (Player)e;
					if(plugin.playerManager.getRedTeam().contains(lp.getName()))
						event.setIntensity(lp, 0.0);
				}
			}
		}
	}
	
	@EventHandler
	public void archerNoArrowLoss(EntityShootBowEvent event){
		if( ! (event.getEntity() instanceof Player))
			return;
		final Player player = (Player)event.getEntity();
		if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			if(player.getGameMode() == GameMode.CREATIVE)
				return;
			
			if(getArrowType(player).equals("fire"))
				event.getProjectile().setFireTicks(600);
			
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	            public void run(){
	                player.getInventory().addItem(new ItemStack(Material.ARROW));
	                player.updateInventory();
	            }
			}, 1L);
		}
	}
	
	@EventHandler
    public void archerArrowSwitch(PlayerInteractEvent event){
    	final Player player = event.getPlayer();

    	if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
    		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
  
    			if(player.getItemInHand().getType() == Material.BOW){
    				String arrowType = getArrowType(player);
    				if(arrowType == null)
    					return;
    				if(arrowType.equalsIgnoreCase("normal")){
    					setArrowType(player,"fire");
    					player.sendMessage(ChatColor.GRAY+"Equipped fire arrows.");
    				}
    				else if(arrowType.equalsIgnoreCase("fire")){
    					setArrowType(player,"slow");
    					player.sendMessage(ChatColor.GRAY+"Equipped slowness arrows.");
    				}
    				else if(arrowType.equalsIgnoreCase("slow")){
    					setArrowType(player,"normal");
    					player.sendMessage(ChatColor.GRAY+"Equipped normal arrows.");
    				}
    			}
    		}
    	}
    }
	
	@EventHandler
	public void archerArrowEffects(EntityDamageByEntityEvent event){
	    if(event.getDamager() instanceof org.bukkit.entity.Arrow){
	    	org.bukkit.entity.Arrow arrow = (org.bukkit.entity.Arrow)event.getDamager();
	    	if(arrow.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
		    	Player damager = (Player)arrow.getShooter();
		    	Player damaged = (Player)event.getEntity();
		    	
		    	if(plugin.playerManager.getSpectators().contains(damaged.getName()))
					event.setCancelled(true);
				else if(plugin.playerManager.getSpectators().contains(damager.getName()))
					event.setCancelled(true);
				else if(plugin.playerManager.getRedTeam().contains(damaged.getName()) && plugin.playerManager.getRedTeam().contains(damager.getName()))
					event.setCancelled(true);
				else if(plugin.playerManager.getBlueTeam().contains(damaged.getName()) && plugin.playerManager.getBlueTeam().contains(damager.getName()))
					event.setCancelled(true);
		    	
		    	if(event.isCancelled())
		    		return;
		    	
		    	String arrowType = getArrowType(damager);
		    	
		    	if(arrowType.equalsIgnoreCase("fire"))
		    		damaged.setFireTicks(100); //5 seconds
		    	else if(arrowType.equalsIgnoreCase("slow")){
		    		PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, 140, 2); //7 seconds
					slowness.apply(damaged);
		    	}
	    	}
	    }
	}
	
	ArrayList<String> noPullAbility = new ArrayList<String>();
	
	@EventHandler
	public void tankPull(PlayerInteractEvent event){
		final Player player = event.getPlayer();
		
		if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
				if(player.getItemInHand().getType() == Material.GOLD_SWORD){
					if(noPullAbility.contains(player.getName()))
						player.sendMessage(ChatColor.GRAY+"That ability is not ready yet.");
					else{
						player.sendMessage(ChatColor.GRAY+"You have used your pull ability.");
						noPullAbility.add(player.getName());
						
						if(plugin.playerManager.getRedTeam().contains(player.getName())){
							for(Entity e : player.getNearbyEntities(15, 0, 15)){
								if(e instanceof Player){
									Player lp = (Player)e;
									if(plugin.playerManager.getBlueTeam().contains(lp.getName()))
										lp.teleport(player.getLocation());
								}
							}
						}
						else if(plugin.playerManager.getBlueTeam().contains(player.getName())){
							for(Entity e : player.getNearbyEntities(15, 0, 15)){
								if(e instanceof Player){
									Player lp = (Player)e;
									if(plugin.playerManager.getRedTeam().contains(lp.getName()))
										lp.teleport(player.getLocation());
								}
							}
						}
						
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		    				public void run() {        //create a delayed event to give player potion
		    					noPullAbility.remove(player.getName());
		    					}
		    				}, (long)(400)); //20 seconds
					}
				}
			}
		}
	}
	
	Map<String, String> arrowTypeMap = new HashMap<String, String>(); //player name, string type
	
	public String getArrowType(Player player) //abilityName, amount
	{
		if(arrowTypeMap.containsKey(player.getName())){
			return arrowTypeMap.get(player.getName()); //need to set abilityAmounts and add to abilityMap on inventoryClose
		}
		else
			return "normal";
	}
	 
	public void setArrowType(Player player, String type)
	{
		if(type.equalsIgnoreCase("fire") || type.equalsIgnoreCase("slow") || type.equalsIgnoreCase("normal"))
			arrowTypeMap.put(player.getName(), type);
		else
			arrowTypeMap.put(player.getName(), "");
	}
	
// SENDING WOOL HELMET PACKET
//	DyeColor dye = DyeColor.WHITE;
//	 
//    for(Player playerInWorld : getServer().getOnlinePlayers()) {
//        if(!playerInWorld.equals(player)) {
//            ((CraftPlayer) playerInWorld).getHandle().netServerHandler.sendPacket(new Packet5EntityEquipment(((CraftPlayer) player).getEntityId(), 4, new CraftItemStack(new Wool(dye).toItemStack(1)).getHandle()));
//        }
//    }
}