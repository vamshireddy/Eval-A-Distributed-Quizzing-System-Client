package com.example.peerbased;

import java.io.Serializable;

public class LeaderPacket implements Serializable {
	static final long serialVersionUID = 1242L;
	public String uID;
	public boolean granted;
	public LeaderPacket(String l) {
		granted = false;
		uID = l;
	}
}
