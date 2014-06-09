package com.example.peerbased;

import java.io.Serializable;
import java.util.ArrayList;

public class LeaderPacket implements Serializable {
	public static final long serialVersionUID = 1242L;
	public String uID;
	public String uName;
	public boolean granted;
	public boolean grpNameRequest;
	public boolean leaderSelection;
	public boolean LeadersListBroadcast;
	public ArrayList<Leader> leaders;
	public String groupName;
	
	public LeaderPacket() {
		granted = false;
		grpNameRequest = false;
		LeadersListBroadcast = false;
		leaderSelection = false;
		leaders = null;
		uID = "";
		uName = "";
		groupName = "";
	}
	public LeaderPacket(boolean t)
	{
		granted = t;
	}
}
