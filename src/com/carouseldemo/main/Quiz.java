package com.carouseldemo.main;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import StaticAttributes.*;

import com.example.peerbased.LeaderPacket;
import com.example.peerbased.Packet;

import StaticAttributes.QuizAttributes;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class Quiz extends Activity implements OnClickListener {
	Button leader;
	TextView instruction1,instruction2,instruction3,instruction4;
	DatagramSocket sock;
	TextView errorMsg;
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz);
		sock = SocketHandler.normalSocket;
		instruction1 = (TextView)findViewById(R.id.Instr1);
		instruction2 = (TextView)findViewById(R.id.Instr2);
		instruction3 = (TextView)findViewById(R.id.Instr3);
		instruction4 = (TextView)findViewById(R.id.Instr4);
		errorMsg = (TextView)findViewById(R.id.errorBox);
		errorMsg.setVisibility(View.INVISIBLE);
		leader=(Button)findViewById(R.id.leader);
		leader.setOnClickListener(this);
		setInstructions();
		try {
			sock.setSoTimeout(1000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public void onClick(View v) 
	{   
		System.out.println("I am clicked!!");
		// TODO Clear the socket timeout before going to the next activity
		LeaderPacket lp = new LeaderPacket(QuizAttributes.studentID);
		lp.granted  = false;
		Packet p = new Packet(111222, false, false, false,Utilities.serialize(lp),false, true);
		byte[] packet_bytes = Utilities.serialize(p);
		DatagramPacket leader_pack = new DatagramPacket(packet_bytes, packet_bytes.length,Utilities.serverIP, Utilities.servPort);
		
		try {
			sock.send(leader_pack);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Packet pyy = null;
		while( true )
		{
			byte[] by = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket packy = new DatagramPacket(by, by.length);
			try
			{
				sock.receive(packy);
			}
			catch( SocketTimeoutException e1)
			{
				System.out.println("HEYYYYYYYYYYYYYYY!!!!!!!!!!!!!!!!!!!!!!!!!!. I am timed out!!!");
				return;
			}
			catch (IOException e) {
				e.printStackTrace();
				return;
			}
			pyy = (Packet)Utilities.deserialize(by);
			if( pyy.seq_no == 121441 )
			{
				break;
			}
			else
			{
				continue;
			}
		}
		
		System.out.println("Packet received!!!"+"  Leader packet is "+pyy.leader_req_packet);
		System.out.println("Packet received!!!"+"  auth pack is "+pyy.auth_packet);
		System.out.println("Packet received!!!"+" seq no is "+pyy.seq_no);
		System.out.println("Packet received!!!"+" probe is is "+pyy.probe_packet);
		if( pyy.leader_req_packet == true )
		{
			LeaderPacket lpp = (LeaderPacket)Utilities.deserialize(pyy.data);
			System.out.println("Packet received!!!"+" granted is "+lpp.granted);
			System.out.println("Packet received!!!"+" group name is "+lpp.groupName);
			System.out.println("Packet received!!!"+" grp req is "+lpp.grpNameRequest);
			if( lpp.granted == true )
			{
				leader.setText("You are Leader now!");
			}
			else
			{
				errorMsg.setText("You have not been selected as a leader :D , Better luck next time :P ");
				errorMsg.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			return;
		}
//		Intent i = new Intent(this, MainActivity.class);
//		startActivity(i);	
	}
	public void setInstructions()
	{
		instruction1.setText("There are "+QuizAttributes.noOfOnlineStudents+" Students online!");
		instruction2.setText("The Quiz consists of "+QuizAttributes.noOfRounds+" rounds!");
		instruction3.setText("The Subject of this quiz session is "+QuizAttributes.subject);
		instruction4.setText("There would be "+QuizAttributes.noOfLeaders+" leaders and Each group has "+
				QuizAttributes.sizeOfGroup+" students");
	}
}



