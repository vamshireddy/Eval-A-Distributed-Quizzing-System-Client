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
import android.graphics.Color;
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
		
		boolean rcvd = false;
		
		int activityFlag = -1;
		
		while( true )
		{
			System.out.println("Listening for screen changing packet!!!!!");
			
			byte[] b = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket pack  =  new DatagramPacket(b, b.length);
			
			try {
				sock.receive(pack);
			}
			catch( SocketTimeoutException e )
			{
				if( rcvd == true )
				{
					/*
					 * Go to next activity
					 */
					if( activityFlag == 1 )
					{
						Intent i = new Intent(Quiz.staticAct, Group_name.class);
						Quiz.staticAct.startActivity(i);
//						Quiz.staticAct.finish();
					}
					else if ( activityFlag == 2 )
					{
						Intent i = new Intent(Quiz.staticAct, Select_leader.class);
						Quiz.staticAct.startActivity(i);
//						Quiz.staticAct.finish();
					}
					break;
				}
				else
				{
					continue;
				}
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
			
			/*
			 * Packet is received!
			 */
			
			Packet packetRcvd = (Packet)Utilities.deserialize(b);
			
			if( packetRcvd.type == PacketTypes.LEADER_SCREEN_CHANGE && packetRcvd.ack == false )
			{
				if( rcvd == false )
				{
					LeaderPacket lpRecvd = (LeaderPacket)Utilities.deserialize(packetRcvd.data);
					
					if( lpRecvd.grpNameRequest == true )
					{
						System.out.println("Its a grp name");
						activityFlag = 1;
					}
					else if( lpRecvd.LeadersListBroadcast == true )
					{
						QuizAttributes.selectedLeaders = lpRecvd.leaders;
						activityFlag = 2;
					}
					rcvd = true;
				}	
				/*
				 * Now send the ACK back
				 */
				packetRcvd.data = null;
				packetRcvd.ack = true;
				
				byte[] ackPackbytes = Utilities.serialize(packetRcvd);
				DatagramPacket ackPack = new DatagramPacket(ackPackbytes, ackPackbytes.length, Utilities.serverIP, Utilities.servPort);
				try {
					sock.send(ackPack);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
				/*
				 * Now wait for socket timeout seconds and break
				 */
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
	Button skipButton;
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
		errorMsg.setVisibility(View.VISIBLE);
		errorMsg.setText("");
		leader=(Button)findViewById(R.id.leader);
		leader.setBackgroundColor(Color.CYAN);
		leader.setOnClickListener(this);
		skipButton = (Button)findViewById(R.id.skipBut);
		skipButton.setBackgroundColor(Color.GRAY);
		skipButton.setOnClickListener(this);
		//skipButton.setOnClickListener(this);
		setInstructions();
    }
	public void onClick(View v) 
	{ 
		/*
		 * This is for handling the skip button
		 */
		if( v.getId() == R.id.skipBut )
		{
			/*
			 * Skip the leader, So only listen for the screen changing packet
			 */
			skipButton.setBackgroundColor(Color.RED);
			skipButton.setEnabled(false);
			leader.setEnabled(false);
			QuizListen1 q = new QuizListen1();
			q.start();
			return;
		}
		/*
		 * This is for handling the leader button
		 */
		errorMsg.setText("Please wait!");
		
		/*
		 * Create a leader packet and send request to the server
		 */
		LeaderPacket lp = new LeaderPacket();
		lp.uID = QuizAttributes.studentID;
		lp.uName = QuizAttributes.studentName;
		lp.granted  = false;
		
		int currentSeqNo = Utilities.seqNo++;
		System.out.println("Sedning with seq no : "+currentSeqNo);
		/*
		 * Create a packet and encapsulate leader packet
		 */
		Packet p = new Packet(currentSeqNo, PacketTypes.LEADER_REQUEST, false, Utilities.serialize(lp));
		
		byte[] packet_bytes = Utilities.serialize(p);
		
		DatagramPacket leader_pack = new DatagramPacket(packet_bytes, packet_bytes.length,Utilities.serverIP, Utilities.servPort);
		
		try {
			sock.send(leader_pack);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Packet recvd_packet = null;
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
				errorMsg.setText("Your request has been timed out! Try again after 3 seconds");
				errorMsg.setVisibility(View.VISIBLE);
				return;
			}
			catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
			recvd_packet = (Packet)Utilities.deserialize(by);
			if( recvd_packet.ack == true && recvd_packet.seq_no == currentSeqNo && recvd_packet.type == PacketTypes.LEADER_REQUEST )
			{
				break;
			}
			else
			{
				continue;
			}
		}
		
		LeaderPacket lpp = (LeaderPacket)Utilities.deserialize(recvd_packet.data);

		if( lpp.granted == true )
		{
			leader.setText("You are Leader now!");
			leader.setBackgroundColor(Color.RED);
			errorMsg.setText("Please wait untill the leader session expires.");
			leader.setEnabled(false);
			skipButton.setEnabled(false);
		}
		else
		{
			//System.out.println("You have not been selected as a leader :D , Better luck next time :P ");
			errorMsg.setText("Sorry, You have not been selected as a leader.");
			leader.setBackgroundColor(Color.RED);
			leader.setEnabled(false);
			skipButton.setEnabled(false);
		}
		QuizListen1 q = new QuizListen1();
		q.start();
		System.out.println("---------------- I am after the function leader session!!!-------------------");
	}
	
	public void setInstructions()
	{
		instruction1.setText(" * There are "+QuizAttributes.noOfOnlineStudents+" Students online!");
		instruction2.setText(" * The Quiz consists of "+QuizAttributes.noOfRounds+" rounds!");
		instruction3.setText(" * The Subject of this quiz session is "+QuizAttributes.subject);
		instruction4.setText(" * There would be "+QuizAttributes.noOfLeaders+" leaders and Each group has "+
				QuizAttributes.sizeOfGroup+" students");
	}
}



