package com.example.peerbased;

import java.io.Serializable;
import java.util.ArrayList;

public class LeaderPacket implements Serializable {
	static final long serialVersionUID = 1242L;
	public String uID;
	public boolean granted;
	public boolean grpNameRequest;
	public boolean selectedLeadersList;
	public ArrayList<String> leaders;
	public String groupName;
	public LeaderPacket(String l) {
		granted = false;
		uID = l;
	}
}