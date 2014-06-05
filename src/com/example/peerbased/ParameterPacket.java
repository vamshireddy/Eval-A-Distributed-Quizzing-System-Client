package com.example.peerbased;

import java.io.Serializable;

public class ParameterPacket implements Serializable {
	static final long serialVersionUID = 422L;
	public byte noOfOnlineStudents;
	public byte noOfLeaders;
	public byte sizeOfGroup;
	public byte noOfRounds;
	public String subject;
	public ParameterPacket(byte no_studs, byte no_grps, byte size_grp, byte noOfRounds, String subject) {
		noOfOnlineStudents = no_studs;
		noOfLeaders = no_grps;
		sizeOfGroup = size_grp;
		this.noOfRounds = noOfRounds;
	}
}