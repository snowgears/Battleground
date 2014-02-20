package com.snowgears.battleground.voting;

import java.util.Comparator;


public class VoteComparator implements Comparator<VotingWorld> {
   
	@Override 
	public int compare(final VotingWorld a, final VotingWorld b) {
	    int c;
	    c = new Integer(a.getWorldNumber()).compareTo(new Integer(b.getWorldNumber()));
	    if (c == 0)
	       c = new Integer(a.getVotes()).compareTo(new Integer(b.getVotes()));
	    return c;
	}
	
//	public int compare(VotingWorld a, VotingWorld b) {
//        int voteComparison = new Integer(a.getVotes()).compareTo(new Integer(b.getVotes()));
//        return voteComparison == 0 ? new Integer(a.getWorldNumber()).compareTo(new Integer(b.getWorldNumber())) : voteComparison;
//    }
}
