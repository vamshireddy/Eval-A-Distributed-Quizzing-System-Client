package com.carouseldemo.main;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import StaticAttributes.*;

import com.example.peerbased.LeaderPacket;
import com.example.peerbased.Packet;
import com.example.peerbased.ParameterPacket;

import StaticAttributes.QuizAttributes;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint.Join;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

class QuizListen1 extends Thread
{
	DatagramSocket sock;
	public QuizListen1() {
		sock = SocketHandler.normalSocket;
	}
	public void run()
	{
		while( true )
		{
			System.out.println("Listening for screen changing packet!!!!!");
			byte[] b = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket pack  =  new DatagramPacket(b, b.length);
			try {
				sock.receive(pack);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("recvd...yay!!!");
			Packet packetRcvd = (Packet)Utilities.deserialize(b);
			if( packetRcvd.leader_req_packet == true )
			{
				System.out.println("Its a leader packet");
				LeaderPacket lpRecvd = (LeaderPacket)Utilities.deserialize(packetRcvd.data);
				if( packetRcvd.seq_no == PacketSequenceNos.GROUP_SERVER_SEND && lpRecvd.grpNameRequest == true )
				{
					System.out.println("Its a grp name");
					// Go to the leaders display page
					// Interface for the non-leader students
					Intent i = new Intent(Quiz.staticAct, Group_name.class);
					Quiz.staticAct.startActivity(i);
					Quiz.staticAct.finish();
					break;
				}
				else if(  packetRcvd.seq_no == PacketSequenceNos.SELECTED_LEADERS_SERVER_SEND && lpRecvd.LeadersListBroadcast == true )
				{
					System.out.println("Its an online aaaa");
					QuizAttributes.selectedLeaders = lpRecvd.leaders;
					// Go to the leader group request page
					// Interface for the leader students
					Intent i = new Intent(Quiz.staticAct, Select_leader.class);
					Quiz.staticAct.startActivity(i);
					Quiz.staticAct.finish();
					break;
				}
			}
			else
			{
				continue;
			}
		}
	}
}


public class Quiz extends Activity implements OnClickListener {
	public static Quiz staticAct;
	//Button skipButton;
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
		staticAct = this;
		instruction1 = (TextView)findViewById(R.id.Instr1);
		instruction2 = (TextView)findViewById(R.id.Instr2);
		instruction3 = (TextView)findViewById(R.id.Instr3);
		instruction4 = (TextView)findViewById(R.id.Instr4);
//		skipButton = (Button)findViewById(R.id.skipBut);
		errorMsg = (TextView)findViewById(R.id.errorBox);
		errorMsg.setVisibility(View.INVISIBLE);
		leader=(Button)findViewById(R.id.leader);
		leader.setOnClickListener(this);
		//skipButton.setOnClickListener(this);
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
		/*if( v.getId() == R.id.skipBut )
		{
			leader.setEnabled(false);
			skipButton.setEnabled(false);
			sock.close();
			QuizListen1 q = new QuizListen1();
			q.run();// Normal method call
			return;
		}*/
		System.out.println("I am clicked!!");
		// TODO Clear the socket timeout before going to the next activity
		LeaderPacket lp = new LeaderPacket();
		lp.uID = QuizAttributes.studentID;
		lp.uName = QuizAttributes.studentName;
		lp.granted  = false;
		Packet p = new Packet(PacketSequenceNos.LEADER_REQ_CLIENT_SEND, false, false, false,Utilities.serialize(lp),false, true);
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
				errorMsg.setText("Your request has been timed out! Try again");
				errorMsg.setVisibility(View.VISIBLE);
				return;
			}
			catch (IOException e) {
				e.printStackTrace();
				return;
			}
			pyy = (Packet)Utilities.deserialize(by);
			if( pyy.leader_req_packet == true && pyy.seq_no == PacketSequenceNos.LEADER_REQ_SERVER_SEND )
			{
				leader.setEnabled(false);
				break;
			}
			else
			{
				continue;
			}
		}
		
		LeaderPacket lpp = (LeaderPacket)Utilities.deserialize(pyy.data);

		if( lpp.granted == true )
		{
			System.out.println("You are Leader now!");
			leader.setText("You are Leader now!");
			errorMsg.setText("Please wait untill the leader session expires.");
			leader.setEnabled(false);
			//skipButton.setEnabled(false);
		}
		else
		{
			//System.out.println("You have not been selected as a leader :D , Better luck next time :P ");
			errorMsg.setText("Sorry, You have not been selected as a leader.");
			errorMsg.setVisibility(View.VISIBLE);
			leader.setEnabled(false);
			//skipButton.setEnabled(false);
		}
		try {
			sock.setSoTimeout(0);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		QuizListen1 q = new QuizListen1();
		q.start();
		System.out.println("---------------- I am after the function!!!-------------------");

		/*while( true )
		{
			byte[] b = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket pack  =  new DatagramPacket(b, b.length);
			try {
				sock.receive(pack);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Packet packetRcvd = (Packet)Utilities.deserialize(b);
			if( packetRcvd.leader_req_packet == true )
			{
<<<<<<< HEAD
				LeaderPacket lpRecvd = (LeaderPacket)Utilities.deserialize(packetRcvd.data);
				if( packetRcvd.seq_no == 121441 && lpRecvd.grpNameRequest == true )
				{
					// Go to the leaders display page
					// Interface for the non-leader students
					Intent i = new Intent(Quiz.act, Select_leader.class);
					Quiz.act.startActivity(i);
					Quiz.act.finish();
					break;
				}
				else if(  packetRcvd.seq_no == 121221 && lpRecvd.selectedLeadersList == true )
				{
					QuizAttributes.selectedLeaders = lpRecvd.leaders;
					// Go to the leader group request page
					// Interface for the leader students
					Intent i = new Intent(Quiz.act, Group_name.class);
					Quiz.act.startActivity(i);
					Quiz.act.finish();
					break;
				}
			}
			else
			{
				continue;
=======
				leader.setEnabled(false);
				leader.setText("You are Leader now!");
			}
			else
			{
				leader.setEnabled(false);
				errorMsg.setText("You have not been selected as a leader :D , Better luck next time :P ");
				errorMsg.setVisibility(View.VISIBLE);
>>>>>>> 3d5ab580ad7d05264640c490c144d6c12787b856
			}
		}*/
	}
	public void setInstructions()
	{
		instruction1.setText("--->There are "+QuizAttributes.noOfOnlineStudents+" Students online!");
		instruction2.setText("--->The Quiz consists of "+QuizAttributes.noOfRounds+" rounds!");
		instruction3.setText("--->The Subject of this quiz session is "+QuizAttributes.subject);
		instruction4.setText("--->There would be "+QuizAttributes.noOfLeaders+" leaders and Each group has "+
				QuizAttributes.sizeOfGroup+" students");
	}
}



