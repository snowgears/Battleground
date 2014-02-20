package com.snowgears.battleground;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.snowgears.battleground.customevents.PlayerJoinBattlegroundEvent;
import com.snowgears.battleground.customevents.PlayerLeaveBattlegroundEvent;
import com.snowgears.battleground.domination.Base;
import com.snowgears.battleground.voting.VotingWorld;

public class BattlegroundCommandExecutor implements CommandExecutor {

	private Battleground plugin;

	public final ArrayList<String> allAreaTypes = new ArrayList<String>();
	
	public BattlegroundCommandExecutor(Battleground instance) {
		plugin = instance;
		allAreaTypes.add("flag");
		allAreaTypes.add("ground");
		allAreaTypes.add("sky");
		allAreaTypes.add("ind");
		allAreaTypes.add("warp");
		allAreaTypes.add("spawn");
		allAreaTypes.add("gate");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ( ! (sender instanceof Player)){
//			if(cmd.getName().equalsIgnoreCase("bg") && args[0].equalsIgnoreCase("reset")){
//				plugin.worldHandler.resetWorld("");
//				return true;
//			}
			sender.sendMessage("This command only works in-game.");
			return true;
		}
		Player player = (Player)sender;
		if(cmd.getName().equalsIgnoreCase("bg")){
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("warp")){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp()){
						player.sendMessage(ChatColor.GOLD+"Which battleground do you want to warp to?");
						player.sendMessage(ChatColor.GRAY+"To warp to a world, type '/bg warp #'.");
						String worldString = "";
						for(int i=0; i<plugin.worldHandler.getAllBattleWorlds().size(); i++){
							worldString = worldString + ChatColor.GOLD+(i+1)+") "+ ChatColor.WHITE + plugin.worldHandler.getCleanWorldName(plugin.worldHandler.getAllBattleWorlds().get(i)) + " ";
						}
						player.sendMessage(worldString);
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("reset")){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp())
						plugin.worldHandler.resetWorld(player.getWorld().getName());
					else{
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("vote")){
					if(Battleground.usePerms == false || player.hasPermission("battleground.member")){
						if(plugin.votingManager.getIfVotingIsOpen() == true){
							for(VotingWorld voteWorld : plugin.votingManager.getWorldsBeingVotedOn()){
								player.sendMessage(ChatColor.GOLD+""+voteWorld.getWorldNumber()+") "+ChatColor.YELLOW+voteWorld.getCleanWorldName());
								player.sendMessage(ChatColor.GRAY+"     -Battle Type: "+voteWorld.getBattleType());
							}
							return true;
						}
						else{
							player.sendMessage(ChatColor.RED+"Voting is currently closed.");
							return true;
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+"You are not authorized to do that");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("join")){
					if(Battleground.usePerms == false || player.hasPermission("battleground.member")){
						//check if there is a game in progress
						//try to join the game ( < 30 players )
						//if cannot join and game in progress, make them a spectator
						//if no game in progress, add them to the queue
						if(player.getWorld().getName().equals(plugin.worldHandler.getCurrentWorld())){
							player.sendMessage(ChatColor.RED+"You are already in the battleground.");
							return true;
						}
						else if(plugin.alisten.plugin.playerManager.getQueue().contains(player.getName())){
							player.sendMessage(ChatColor.RED+"You are already queued for the battleground.");
							return true;
						}
						else{
							plugin.alisten.plugin.playerManager.getQueue().add(player.getName());
							player.sendMessage(ChatColor.GREEN+"You are now queued for the battleground.");
						}
					
						//game is ready to start and there is no game in progress
						if(plugin.alisten.plugin.playerManager.getQueue().size() >= Battleground.basinPlayersToStart && plugin.dominationGame.gameInProgress == false){ 
							plugin.votingManager.selectNewWorldsAndRun();
						}
						//there is a game in progress and enough open spots to join
						else if(plugin.dominationGame.gameInProgress == true && (plugin.alisten.plugin.playerManager.getRedTeam().size() + plugin.alisten.plugin.playerManager.getBlueTeam().size()) < Battleground.basinMaxPlayers){
							plugin.dominationGame.promptPlayerToEnterBattleground(player, false);
						}
						//game in progress but not enough open spots to join
						else if(plugin.dominationGame.gameInProgress == true){
							plugin.dominationGame.promptPlayerToEnterBattleground(player, true);
						}
						// not enough players to start game
						else{
							for(int i=0; i<plugin.alisten.plugin.playerManager.getQueue().size(); i++){
								Player p = plugin.getServer().getPlayer(plugin.alisten.plugin.playerManager.getQueue().get(i));
								p.sendMessage(ChatColor.GRAY+"The battleground needs "+ ChatColor.GOLD +(Battleground.basinPlayersToStart-plugin.alisten.plugin.playerManager.getQueue().size()) + ChatColor.GRAY+" more players to start the battle.");
							}
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+"You are not authorized to do that");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("leave")){
					if(Battleground.usePerms == false || player.hasPermission("battleground.member")){
						PlayerLeaveBattlegroundEvent e = new PlayerLeaveBattlegroundEvent(plugin, player);
						plugin.getServer().getPluginManager().callEvent(e);
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+"You are not authorized to do that");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("accept")){
					if(Battleground.usePerms == false || player.hasPermission("battleground.member")){
						if( ! (Battleground.playersReadyForBG.contains(player.getName())))
							player.sendMessage(ChatColor.RED+"You either waited too long to accept or you were never summoned.");
						// the player has accepted their invitation to the battleground
						else{
							Battleground.playersReadyForBG.remove(player.getName());
							
							// the battleground is full
							if((plugin.alisten.plugin.playerManager.getBlueTeam().size() + plugin.alisten.plugin.playerManager.getRedTeam().size()) == Battleground.basinMaxPlayers){ 
								PlayerJoinBattlegroundEvent e = new PlayerJoinBattlegroundEvent(plugin, player, "spectator", plugin.worldHandler.getCurrentWorld());
								plugin.getServer().getPluginManager().callEvent(e);
							}
							//red team is smaller
							else if(plugin.alisten.plugin.playerManager.getBlueTeam().size() > plugin.alisten.plugin.playerManager.getRedTeam().size()){ 
								PlayerJoinBattlegroundEvent e = new PlayerJoinBattlegroundEvent(plugin, player, "red", plugin.worldHandler.getCurrentWorld());
								plugin.getServer().getPluginManager().callEvent(e);
							}
							else{ //blue team is smaller
								PlayerJoinBattlegroundEvent e = new PlayerJoinBattlegroundEvent(plugin, player, "blue", plugin.worldHandler.getCurrentWorld());
								plugin.getServer().getPluginManager().callEvent(e);
							}
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+"You are not authorized to do that");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("help")){
					if(Battleground.usePerms == false || player.hasPermission("battleground.member")){
						player.sendMessage(ChatColor.YELLOW+"List Of Battleground Commands:");
						player.sendMessage(ChatColor.GOLD+"/bg join"+ChatColor.GRAY+" - puts you in the queue to join the battleground.");
						player.sendMessage(ChatColor.GOLD+"/bg leave"+ChatColor.GRAY+" - leave the queue to join the battleground. Also leaves battleground.");
						player.sendMessage(ChatColor.GOLD+"/bg accept"+ChatColor.GRAY+" - when the battleground is ready, you will be prompted to join it using this command.");
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+"You are not authorized to do that");
						return true;
					}
				}
				return true;
			}
			else if(args.length == 2){	
				if(args[0].equalsIgnoreCase("vote") && Battleground.tools.isInteger(args[1])){
					if(Battleground.usePerms == false || player.hasPermission("battleground.member")){
						if(plugin.votingManager.getIfVotingIsOpen()){
							if(!plugin.alisten.plugin.playerManager.getQueue().contains(player.getName())){
								player.sendMessage(ChatColor.RED+"You can only vote if you are in the queue for the battleground.");
								return true;
							}
							int vote = Integer.parseInt(args[1]);
							plugin.votingManager.castVote(player, vote);
							return true;
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+"You are not authorized to do that");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("warp") && args[1].length() > 0){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp()){
						int num = Integer.parseInt(args[1]);
						num--;
						if(num < 0 || num >= plugin.worldHandler.getAllBattleWorlds().size())
							player.sendMessage(ChatColor.RED+"That number was not one of the options.");
						else{
							player.sendMessage("Warping to battleground: "+ ChatColor.GOLD+plugin.worldHandler.getCleanWorldName(plugin.worldHandler.getAllBattleWorlds().get(num)));
							player.teleport(plugin.getServer().getWorld(plugin.worldHandler.getAllBattleWorlds().get(num)).getSpawnLocation());
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("team") && args[1].length() > 0){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp()){
						if(args[1].equalsIgnoreCase("red")){
							if(plugin.alisten.plugin.playerManager.getBlueTeam().contains(player.getName()))
								plugin.alisten.plugin.playerManager.getBlueTeam().remove(player.getName());
							if(!plugin.alisten.plugin.playerManager.getRedTeam().contains(player.getName()))
								plugin.alisten.plugin.playerManager.getRedTeam().add(player.getName());
							player.sendMessage(ChatColor.GOLD+"You have joined the"+ChatColor.RED+" red team.");
						}
						else if(args[1].equalsIgnoreCase("blue")){
							if(plugin.alisten.plugin.playerManager.getRedTeam().contains(player.getName()))
								plugin.alisten.plugin.playerManager.getRedTeam().remove(player.getName());
							if(!plugin.alisten.plugin.playerManager.getBlueTeam().contains(player.getName()))
								plugin.alisten.plugin.playerManager.getBlueTeam().add(player.getName());
							player.sendMessage(ChatColor.GOLD+"You have joined the"+ChatColor.BLUE+" blue team.");
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("effect") && args[1].length() > 2){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp()){
						String baseName = args[1].substring(2);
						Base b = plugin.baseHandler.getBaseFromName(player.getWorld().getName(), baseName);
						if(b != null){
							b.playEffect("red");
						}
						else{
							player.sendMessage(ChatColor.RED+"There is no base with that name in this world.");
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("create") && args[1].length() > 2){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp()){
						if(!args[1].substring(0,2).equalsIgnoreCase("b:")){
							player.sendMessage(ChatColor.GRAY+"/bg create b:<BaseName>");
							return true;
						}
						String baseName = args[1].substring(2);
						Base base = plugin.baseHandler.getBaseFromName(player.getWorld().getName(), baseName);
						if(base != null){
							player.sendMessage(ChatColor.RED+ "There is another base in this world with the same name. Please choose another name.");
							return true;
						}
						else{
							base = plugin.baseHandler.createBase(player.getWorld().getName(), baseName);
						}
						player.sendMessage(ChatColor.GREEN+ "Successfully created a base named "+ChatColor.GOLD + baseName);
						player.sendMessage(ChatColor.GRAY+ "To add locations to the base, use a blaze rod to select locations.");
					}
					else
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
				}
				else if(args[0].equalsIgnoreCase("delete") && args[1].length() > 2){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp()){
						if(!args[1].substring(0,2).equalsIgnoreCase("b:")){
							player.sendMessage(ChatColor.GRAY+"/bg delete b:<BaseName>");
							return true;
						}
						String baseName = args[1].substring(2);
						Base base = plugin.baseHandler.getBaseFromName(player.getWorld().getName(), baseName);
						if(base == null){
							player.sendMessage(ChatColor.RED+ "There is no base in this world with that name.");
							return true;
						}
						plugin.baseHandler.deleteBase(base);
						player.sendMessage(ChatColor.GREEN+ "Successfully deleted the base named "+ChatColor.GOLD + baseName);
					}
					else
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
				}
				else if(args[0].equalsIgnoreCase("set") && args[1].length() > 2){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp()){
						if(args[1].equalsIgnoreCase("gates")){
							if(plugin.playerEditListener.getEditLocations(player).size() == 0){
								player.sendMessage(ChatColor.RED+"You do not have an area selected. Use a blaze rod to select a region.");
								return true;
							}
							WorldLocations wl = plugin.worldHandler.getWorldLocations(player.getWorld().getName());
							if(wl == null)
								return true;
							wl.setGateLocations(plugin.playerEditListener.getEditLocations(player));
							player.sendMessage(ChatColor.GREEN+"Successfully set the selected area to the gate locations for this world.");
						}
						else if(!args[1].substring(0,2).equalsIgnoreCase("s:")){
							player.sendMessage(ChatColor.GRAY+"/bg set s:<Red/Blue>");
							return true;
						}
						String teamColor = args[1].substring(2);
						if(! (teamColor.equalsIgnoreCase("red") || teamColor.equalsIgnoreCase("blue"))){
							player.sendMessage(ChatColor.GRAY+"/bg set s:<Red/Blue>");
							return true;
						}
						else if(plugin.playerEditListener.getEditLocations(player).size() == 0){
							player.sendMessage(ChatColor.RED+"You do not have an area selected. Use a blaze rod to select a region.");
							return true;
						}
						
						WorldLocations wl = plugin.worldHandler.getWorldLocations(player.getWorld().getName());
						if(wl == null)
							return true;
						
						if(teamColor.equalsIgnoreCase("red")){
							wl.setSpawnLocations(plugin.playerEditListener.getEditLocations(player), "red");
							player.sendMessage(ChatColor.GREEN+"Successfully set the selected area to the red spawn locations for this world.");
						}
						else if(teamColor.equalsIgnoreCase("blue")){
							wl.setSpawnLocations(plugin.playerEditListener.getEditLocations(player), "blue");
							player.sendMessage(ChatColor.GREEN+"Successfully set the selected area to the blue spawn locations for this world.");
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("add") && args[1].length() > 2){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp()){
						if(args[1].equalsIgnoreCase("gates")){
							if(plugin.playerEditListener.getEditLocations(player).size() == 0){
								player.sendMessage(ChatColor.RED+"You do not have an area selected. Use a blaze rod to select a region.");
								return true;
							}
							WorldLocations wl = plugin.worldHandler.getWorldLocations(player.getWorld().getName());
							if(wl == null)
								return true;
							wl.addGateLocations(plugin.playerEditListener.getEditLocations(player));
							player.sendMessage(ChatColor.GREEN+"Successfully added the selected area to the gate locations for this world.");
						}
						else if(!args[1].substring(0,2).equalsIgnoreCase("s:")){
							player.sendMessage(ChatColor.GRAY+"/bg add s:<Red/Blue>");
							return true;
						}
						String teamColor = args[1].substring(2);
						if(! (teamColor.equalsIgnoreCase("red") || teamColor.equalsIgnoreCase("blue"))){
							player.sendMessage(ChatColor.GRAY+"/bg add s:<Red/Blue>");
							return true;
						}
						else if(plugin.playerEditListener.getEditLocations(player).size() == 0){
							player.sendMessage(ChatColor.RED+"You do not have an area selected. Use a blaze rod to select a region.");
							return true;
						}
						
						WorldLocations wl = plugin.worldHandler.getWorldLocations(player.getWorld().getName());
						if(wl == null)
							return true;
						
						if(teamColor.equalsIgnoreCase("red")){
							wl.addSpawnLocations(plugin.playerEditListener.getEditLocations(player), "red");
							player.sendMessage(ChatColor.GREEN+"Successfully added the selected area to the red spawn locations for this world.");
						}
						else if(teamColor.equalsIgnoreCase("blue")){
							wl.addSpawnLocations(plugin.playerEditListener.getEditLocations(player), "blue");
							player.sendMessage(ChatColor.GREEN+"Successfully added the selected area to the blue spawn locations for this world.");
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("remove") && args[1].length() > 2){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp()){
						if(args[1].equalsIgnoreCase("gates")){
							if(plugin.playerEditListener.getEditLocations(player).size() == 0){
								player.sendMessage(ChatColor.RED+"You do not have an area selected. Use a blaze rod to select a region.");
								return true;
							}
							WorldLocations wl = plugin.worldHandler.getWorldLocations(player.getWorld().getName());
							if(wl == null)
								return true;
							wl.removeGateLocations(plugin.playerEditListener.getEditLocations(player));
							player.sendMessage(ChatColor.GREEN+"Successfully removed the selected area from the gate locations for this world.");
						}
						else if(!args[1].substring(0,2).equalsIgnoreCase("s:")){
							player.sendMessage(ChatColor.GRAY+"/bg remove s:<Red/Blue>");
							return true;
						}
						String teamColor = args[1].substring(2);
						if(! (teamColor.equalsIgnoreCase("red") || teamColor.equalsIgnoreCase("blue"))){
							player.sendMessage(ChatColor.GRAY+"/bg remove s:<Red/Blue>");
							return true;
						}
						else if(plugin.playerEditListener.getEditLocations(player).size() == 0){
							player.sendMessage(ChatColor.RED+"You do not have an area selected. Use a blaze rod to select a region.");
							return true;
						}
						
						WorldLocations wl = plugin.worldHandler.getWorldLocations(player.getWorld().getName());
						if(wl == null)
							return true;
						
						if(teamColor.equalsIgnoreCase("red")){
							wl.removeSpawnLocations(plugin.playerEditListener.getEditLocations(player), "red");
							player.sendMessage(ChatColor.GREEN+"Successfully removed the selected area from the red spawn locations for this world.");
						}
						else if(teamColor.equalsIgnoreCase("blue")){
							wl.removeSpawnLocations(plugin.playerEditListener.getEditLocations(player), "blue");
							player.sendMessage(ChatColor.GREEN+"Successfully removed the selected area from the blue spawn locations for this world.");
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
						return true;
					}
				}
				return true;
			}
			else if(args.length == 3){
				if(args[0].equalsIgnoreCase("set") && args[1].length() > 2 && args[2].length() > 2){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp()){
						if(!args[1].substring(0,2).equalsIgnoreCase("b:")){
							player.sendMessage(ChatColor.GRAY+"/bg set b:<BaseName> a:<AreaName>");
							return true;
						}
						String baseName = args[1].substring(2);
						Base base = plugin.baseHandler.getBaseFromName(player.getWorld().getName(), baseName);
						// there is no base with that name
						if(base == null){
							player.sendMessage(ChatColor.RED+"There is no base with that name. Refer to the base names in this list:");
							String list = "";
							for(Base b : plugin.baseHandler.getAllBasesInWorld(player.getWorld().getName())){
								list = list + ChatColor.GOLD + b.getName() + ChatColor.GRAY+ ", ";
							}
							player.sendMessage(list);
							return true;
						}
						if(!args[2].substring(0,2).equalsIgnoreCase("a:")){
							player.sendMessage(ChatColor.GRAY+"/bg set b:<BaseName> a:<AreaName>");
							return true;
						}
						String areaName = args[2].substring(2);
						if(!allAreaTypes.contains(areaName)){
							player.sendMessage(ChatColor.RED+"There is no area type with that name. Refer to the area types in this list:");
							player.sendMessage(ChatColor.GRAY+""+allAreaTypes);
							return true;
						}
						else if(plugin.playerEditListener.getEditLocations(player).size() == 0){
							player.sendMessage(ChatColor.RED+"You do not have an area selected. Use a blaze rod to select a region.");
							return true;
						}
						else{
							if(areaName.equalsIgnoreCase("flag"))
								base.setFlagLocation(plugin.playerEditListener.getEditLocations(player).get(0));
							else if(areaName.equalsIgnoreCase("ground"))
								base.setGroundLocations(plugin.playerEditListener.getEditLocations(player));
							else if(areaName.equalsIgnoreCase("sky"))
								base.setSkyLocations(plugin.playerEditListener.getEditLocations(player));
							else if(areaName.equalsIgnoreCase("ind"))
								base.setIndicatorLocations(plugin.playerEditListener.getEditLocations(player));
							else if(areaName.equalsIgnoreCase("warp"))
								base.setWarpLocation(plugin.playerEditListener.getEditLocations(player).get(0));
							
							player.sendMessage(ChatColor.GREEN+"Successfully set the area "+areaName+" in the base "+baseName+" to the selected region.");
							plugin.playerEditListener.playerEditLocations.put(player.getName(), new ArrayList<Location>());
							plugin.playerEditListener.playerKeyLocations.put(player.getName(), new Location[2]);
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("add") && args[1].length() > 2 && args[2].length() > 2){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp()){
						if(!args[1].substring(0,2).equalsIgnoreCase("b:")){
							player.sendMessage(ChatColor.GRAY+"/bg add b:<BaseName> a:<AreaName>");
							return true;
						}
						String baseName = args[1].substring(2);
						Base base = plugin.baseHandler.getBaseFromName(player.getWorld().getName(), baseName);
						// there is no base with that name
						if(base == null){
							player.sendMessage(ChatColor.RED+"There is no base with that name. Refer to the base names in this list:");
							String list = "";
							for(Base b : plugin.baseHandler.getAllBasesInWorld(player.getWorld().getName())){
								list = list + ChatColor.GOLD + b.getName() + ChatColor.GRAY+ ", ";
							}
							player.sendMessage(list);
							return true;
						}
						if(!args[2].substring(0,2).equalsIgnoreCase("a:")){
							player.sendMessage(ChatColor.GRAY+"/bg add b:<BaseName> a:<AreaName>");
							return true;
						}
						String areaName = args[2].substring(2);
						if(!allAreaTypes.contains(areaName)){
							player.sendMessage(ChatColor.RED+"There is no area type with that name. Refer to the area types in this list:");
							player.sendMessage(ChatColor.GRAY+""+allAreaTypes);
							return true;
						}
						else if(plugin.playerEditListener.getEditLocations(player).size() == 0){
							player.sendMessage(ChatColor.RED+"You do not have an area selected. Use a blaze rod to select a region.");
							return true;
						}
						else{
							if(areaName.equalsIgnoreCase("ground")){
								ArrayList<Location> locs = base.getGroundLocations();
								locs.addAll(plugin.playerEditListener.getEditLocations(player));
								base.setGroundLocations(locs);
							}
							else if(areaName.equalsIgnoreCase("sky")){
								ArrayList<Location> locs = base.getSkyLocations();
								locs.addAll(plugin.playerEditListener.getEditLocations(player));
								base.setSkyLocations(locs);
							}
							else if(areaName.equalsIgnoreCase("ind")){
								ArrayList<Location> locs = base.getIndicatorLocations();
								locs.addAll(plugin.playerEditListener.getEditLocations(player));
								base.setIndicatorLocations(locs);
							}
							
							player.sendMessage(ChatColor.GREEN+"Successfully set the area "+areaName+" in the base "+baseName+" to the selected region.");
							plugin.playerEditListener.playerEditLocations.put(player.getName(), new ArrayList<Location>());
							plugin.playerEditListener.playerKeyLocations.put(player.getName(), new Location[2]);
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("remove") && args[1].length() > 2 && args[2].length() > 2){
					if((Battleground.usePerms && player.hasPermission("battleground.operator")) || player.isOp()){
						if(!args[1].substring(0,2).equalsIgnoreCase("b:")){
							player.sendMessage(ChatColor.GRAY+"/bg base:[BaseName] area:[AreaName] remove");
							return true;
						}
						String baseName = args[1].substring(2);
						Base base = plugin.baseHandler.getBaseFromName(player.getWorld().getName(), baseName);
						// there is no base with that name
						if(base == null){
							player.sendMessage(ChatColor.RED+"There is no base with that name. Refer to the base names in this list:");
							String list = "";
							for(Base b : plugin.baseHandler.getAllBasesInWorld(player.getWorld().getName())){
								list = list + ChatColor.GOLD + b.getName() + ChatColor.GRAY+ ", ";
							}
							player.sendMessage(list);
							return true;
						}
						if(!args[2].substring(0,2).equalsIgnoreCase("a:")){
							player.sendMessage(ChatColor.GRAY+"/bg base:[BaseName] area:[AreaName] set");
							return true;
						}
						String areaName = args[2].substring(2);
						if(!allAreaTypes.contains(areaName)){
							player.sendMessage(ChatColor.RED+"There is no area type with that name. Refer to the area types in this list:");
							player.sendMessage(ChatColor.GRAY+""+allAreaTypes);
							return true;
						}
						else if(plugin.playerEditListener.getEditLocations(player).size() == 0){
							player.sendMessage(ChatColor.RED+"You do not have an area selected. Use a blaze rod to select a region.");
							return true;
						}
						else{
							if(areaName.equalsIgnoreCase("ground")){
								ArrayList<Location> allLocs = base.getGroundLocations();
								ArrayList<Location> selected = plugin.playerEditListener.getEditLocations(player);
								for(Location l : selected){
									if(allLocs.contains(l))
										allLocs.remove(l);
								}
								base.setGroundLocations(allLocs);
							}
							else if(areaName.equalsIgnoreCase("sky")){
								ArrayList<Location> allLocs = base.getSkyLocations();
								ArrayList<Location> selected = plugin.playerEditListener.getEditLocations(player);
								for(Location l : selected){
									if(allLocs.contains(l))
										allLocs.remove(l);
								}
								base.setSkyLocations(allLocs);
							}
							else if(areaName.equalsIgnoreCase("ind")){
								ArrayList<Location> allLocs = base.getIndicatorLocations();
								ArrayList<Location> selected = plugin.playerEditListener.getEditLocations(player);
								for(Location l : selected){
									if(allLocs.contains(l))
										allLocs.remove(l);
								}
								base.setIndicatorLocations(allLocs);
							}
							
							player.sendMessage(ChatColor.GREEN+"Successfully removed the selected region from the area "+areaName+" in the base "+baseName+".");
							plugin.playerEditListener.playerEditLocations.put(player.getName(), new ArrayList<Location>());
							plugin.playerEditListener.playerKeyLocations.put(player.getName(), new Location[2]);
						}
					}
					else{
						player.sendMessage(ChatColor.DARK_RED+ "You do not have access to battleground operator commands.");
						return true;
					}
				}
			}
			return true;
		}
		return false;
	}
}