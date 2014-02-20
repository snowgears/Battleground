package com.snowgears.battleground.voting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.snowgears.battleground.BattleType;
import com.snowgears.battleground.Battleground;

public class VotingManager {

	private Battleground plugin = Battleground.plugin;

	private ArrayList<VotingWorld> worldsVotingOn = new ArrayList<VotingWorld>();
	private boolean votingIsOpen = false;
	private int delayedTaskID = 0;
	
	private ScoreboardManager manager = Bukkit.getScoreboardManager();
	private Scoreboard board = manager.getNewScoreboard();
	private Objective objective = board.registerNewObjective("test", "dummy");
	
	private ArrayList<String> playersWhoVoted = new ArrayList<String>();
	
	public VotingManager(Battleground instance){
		plugin = instance;
	}
	
	public void selectNewWorldsAndRun(){
		reset();
		votingIsOpen = true;
		randomlyChooseWorlds();
		
		//only 1 world to play on, just use that one
		if(plugin.worldHandler.getAllBattleWorlds().size() < 2){
			startGameOnWorld(worldsVotingOn.get(0).getWorldName());
		}
		
		setupScoreboard();
		
		addScoreboardForAllPlayersInQueue();
		
		for(String s : plugin.alisten.plugin.playerManager.getQueue()){
			Player p = Bukkit.getPlayer(s);
			if(p != null){
				p.sendMessage(ChatColor.YELLOW+"Voting has begun! Type "+ChatColor.GOLD+"/bg vote <#>"+ChatColor.YELLOW+" to vote for the battleground!");
				p.sendMessage(ChatColor.GRAY+"Voting will end in 30 seconds.");
			}
		}
		
		delayedTaskID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { 
			public void run() { 
				startGameOnWorld(getWorldWithMostVotes());
				votingIsOpen = false;
				removeScoreboardForAllPlayersInQueue();
				resetScoreboard();
			} 
		}, 600L); //30 seconds to vote
	}
	
	private void resetScoreboard(){
		//reset the scoreboards
		for(VotingWorld vw : worldsVotingOn){
			//this will show up like this:            1) Arathi Basin:    4
			String cleanedName = vw.getCleanWorldName();
			if(cleanedName.length() > 12)
				cleanedName = cleanedName.substring(0, 12);
			board.resetScores(Bukkit.getOfflinePlayer(""+ vw.getWorldNumber()+") "+ cleanedName+":")); //Get a fake offline player
		}
	}
		
	public void addScoreboardForPlayer(Player player){
		player.setScoreboard(board);
	}
	
	public void removeScoreboardForPlayer(Player player){
		player.setScoreboard(manager.getNewScoreboard()); //manager.getNewScoreboard() will return a blank scoreboard
	}
	
	private void addScoreboardForAllPlayersInQueue(){
		for(String stringP : plugin.alisten.plugin.playerManager.getQueue()){
			Player p = Bukkit.getPlayer(stringP);
			if(p != null)
				addScoreboardForPlayer(p);
		}
	}
	
	private void removeScoreboardForAllPlayersInQueue(){
		for(String stringP : plugin.alisten.plugin.playerManager.getQueue()){
			Player p = Bukkit.getPlayer(stringP);
			if(p != null)
				removeScoreboardForPlayer(p);
		}
	}
	
	private void randomlyChooseWorlds(){
		worldsVotingOn.clear();
		ArrayList<String> allWorlds = new ArrayList<String>();
		allWorlds = plugin.worldHandler.getAllBattleWorlds();
		
		Collections.shuffle(allWorlds);
		for(int i=0; i<allWorlds.size(); i++){
			VotingWorld vw = new VotingWorld(allWorlds.get(i), (i+1)); //i will either be 1,2,or 3
			worldsVotingOn.add(vw);
			//no more than three worlds
			if(i > 2)
				return;
		}
	}
	
	private void setupScoreboard(){
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.GOLD+"Votes");
		
		Collections.sort(worldsVotingOn, new VoteComparator());
		for(VotingWorld vw : worldsVotingOn){
			System.out.println("World: "+vw.getCleanWorldName()+", Number: "+vw.getWorldNumber());
			//this will show up like this:            1) Arathi Basin:    4
			String cleanedName = vw.getCleanWorldName();
			if(cleanedName.length() > 12)
				cleanedName = cleanedName.substring(0, 12);
			Score score = objective.getScore(Bukkit.getOfflinePlayer(""+ vw.getWorldNumber()+") "+ cleanedName+":")); //Get a fake offline player
			score.setScore(0);
		}
	}
	
	private void refreshScoreboard(){
		Collections.sort(worldsVotingOn, new VoteComparator());
		for(VotingWorld vw : worldsVotingOn){
			//this will show up like this:            1) Arathi Basin:    4
			String cleanedName = vw.getCleanWorldName();
			if(cleanedName.length() > 12)
				cleanedName = cleanedName.substring(0, 12);
			Score score = objective.getScore(Bukkit.getOfflinePlayer(""+ vw.getWorldNumber()+") "+ cleanedName+":")); //Get a fake offline player
			score.setScore(vw.getVotes());
		}
	}

	private String getWorldWithMostVotes(){	
//		Collections.sort(worldsVotingOn, new VoteComparator());
		VotingWorld mostVotes = worldsVotingOn.get(0);
		for(VotingWorld vw : worldsVotingOn){
			if(vw.getVotes() > mostVotes.getVotes())
				mostVotes = vw;
		}
		return mostVotes.getWorldName();
	}
	
	public void castVote(Player player, int voteNumber){
		String message = null;
		if(votingIsOpen==false)
			message = ChatColor.RED+"Voting is currently closed.";
		if(playersWhoVoted.contains(player.getName()))
			message = ChatColor.RED+"You have already voted this round.";
		VotingWorld worldVotedOn = null;
		for(VotingWorld vw : worldsVotingOn){
			if(vw.getWorldNumber() == voteNumber){
				worldVotedOn = vw;
				vw.addVote();
				break;
			}
		}
		if(worldVotedOn == null)
			message = ChatColor.RED+"That number was not one of the options.";
		
		if(message != null){
			player.sendMessage(message);
			return;
		}
		
		player.sendMessage(ChatColor.GRAY+"You have voted for "+worldVotedOn.getCleanWorldName()+" ("+worldVotedOn.getBattleType()+").");
		
		if(!playersWhoVoted.contains(player.getName()))
			playersWhoVoted.add(player.getName());
		
		refreshScoreboard();
	}
	
	private void startGameOnWorld(String world){
		plugin.getServer().getScheduler().cancelTask(delayedTaskID);
		plugin.worldHandler.setCurrentWorld(world);
		
		if(plugin.worldHandler.getBattleType(world) == BattleType.DOMINATION){
			plugin.dominationGame.promptAllPlayersToEnterBattleground();
		}
	}
	
	public ArrayList<VotingWorld> getWorldsBeingVotedOn(){
		return worldsVotingOn;
	}
	
	public boolean getIfVotingIsOpen(){
		return votingIsOpen;
	}
	
	private void reset(){
		worldsVotingOn.clear();
		playersWhoVoted.clear();
	}
}
