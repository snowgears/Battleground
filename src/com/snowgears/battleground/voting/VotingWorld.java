package com.snowgears.battleground.voting;

import com.snowgears.battleground.Battleground;


public class VotingWorld{

	private String worldName = "";
	private String cleanName = "";
	private int worldNumber = 0;
	private int numberOfVotes = 0;
	
	public VotingWorld(String name, int number){
		worldName = name;
		worldNumber = number;
		cleanName = Battleground.plugin.worldHandler.getCleanWorldName(name);
	}
	
	public void addVote(){
		numberOfVotes++;
	}
	
	public int getVotes(){
		return numberOfVotes;
	}
	
	public String getWorldName(){
		return worldName;
	}
	
	public String getCleanWorldName(){
		return cleanName;
	}
	
	public int getWorldNumber(){
		return worldNumber;
	}
	
	public String getBattleType(){
		return Battleground.plugin.worldHandler.getBattleTypeString(worldName);
	}
//
//	@Override
//	public int compareTo(VotingWorld other) {
//		 if(this.numberOfVotes ==  other.numberOfVotes){
//			 if(this.worldNumber > other.worldNumber)
//				 return 1;
//			 else
//				 return -1;
//		 }
//		 else if(this.numberOfVotes > other.numberOfVotes)
//			 return 1;
//		 else
//			 return -1;
//	}
	
}
