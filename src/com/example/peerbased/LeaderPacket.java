package com.example.peerbased;

import java.io.Serializable;
import java.util.ArrayList;

public class LeaderPacket implements Serializable {
	public static final long serialVersionUID = 1242L;
	public String uID;
	public String uName;
	public boolean granted;
	
	public LeaderPacket() {
		granted = false;
		uID = null;
        uName = null;
	}
	public LeaderPacket(boolean t)
	{
		granted = t;
	}
}
