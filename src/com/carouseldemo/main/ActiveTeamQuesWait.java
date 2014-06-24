package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.example.peerbased.LeaderPacket;
import com.example.peerbased.Packet;

import QuizPackets.QuestionPacket;
import StaticAttributes.PacketSequenceNos;
import StaticAttributes.PacketTypes;
import StaticAttributes.QuestionAttributes;
import StaticAttributes.QuizAttributes;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


class ActiveGroupWaiter extends Thread
{
	DatagramSocket sock;
	
	public ActiveGroupWaiter () {
		sock = StaticAttributes.SocketHandler.normalSocket;	
	}
	public void run()
	{	
		boolean rcvd = false;
		
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
					System.out.println("I should be never here ");
					Intent i= new Intent(ActiveTeamQuesWait.staticVar,ActiveTeamAnsWait.class);
					ActiveTeamQuesWait.staticVar.startActivity(i);
					//ActiveTeamQuesWait.staticVar.finish();
					break;
				}
				else
				{
					System.out.println("I am continuing ");
					continue;
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
			/*
			 * Packet is received!
			 */
			Packet packetRcvd = (Packet)Utilities.deserialize(b);
			
			if( packetRcvd.type == PacketTypes.QUESTION_VALIDITY && packetRcvd.ack == false )
			{
				if( rcvd == false )
				{
					QuestionPacket qp = (QuestionPacket)Utilities.deserialize(packetRcvd.data);
					if( qp.groupName.equals(QuizAttributes.groupName) && qp.questionAuthenticated == true )
					{
						System.out.println("I am inside and i made rcvd = true ");
						rcvd = true;
					}
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
				System.out.println("I have sent an ack to that idiot");
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
//		while( true )
//		{
//			byte[] by = new byte[Utilities.MAX_BUFFER_SIZE];
//			DatagramPacket packy = new DatagramPacket(by, by.length);
//			try
//			{
//				sock.receive(packy);
//			}
//			catch( SocketTimeoutException e )
//			{
//				continue;
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//				System.exit(0);
//			}
//			System.out.println("WAHHH!");
//			
//			 /*
//			  *  Packet is received from Teacher (Server)
//			  */
//			System.out.println("I got a packet");
//			Packet recvpack = (Packet)Utilities.deserialize(by);
//			if( recvpack.seq_no == PacketSequenceNos.QUIZ_QUESTION_BROADCAST_SERVER_SEND && recvpack.quizPacket == true)
//			{
//				Intent i= new Intent(ActiveTeamQuesWait.staticVar,ActiveTeamAnsWait.class);
//				ActiveTeamQuesWait.staticVar.startActivity(i);
//				ActiveTeamQuesWait.staticVar.finish();
//				return;
//			}
//			else
//			{
//				continue;
//			}
//		} 
}


public class ActiveTeamQuesWait extends Activity{
	TextView tv;
	public static ActiveTeamQuesWait staticVar;
	public void onCreate(Bundle savedInstanceState) 
	{
	    	super.onCreate(savedInstanceState);
	        setContentView(R.layout.active_team_ques_wait);
	        staticVar = this;
	        tv = (TextView)findViewById(R.id.atqw_tv);
	        tv.setText("Please wait untill your leader forms the question");
	        /*
	         * If question broadcast packet is received, then change the page to ActiveTeamAnswait page
	         */
	        new ActiveGroupWaiter().start();        
	}
}
