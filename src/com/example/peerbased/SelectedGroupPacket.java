package com.example.peerbased;

import java.io.Serializable;
import java.util.ArrayList;

public class SelectedGroupPacket implements Serializable{
	
	public static final long serialVersionUID = 124132L;
	public String groupName;
	public Student leader;
	public ArrayList<Student> team;
	public SelectedGroupPacket(Student l, ArrayList<Student> team)
	{
		this.leader = l;
		this.team = team;
	}
}
