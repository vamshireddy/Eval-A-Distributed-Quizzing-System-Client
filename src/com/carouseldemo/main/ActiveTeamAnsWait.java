package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.example.peerbased.Packet;

import QuizPackets.QuizInterfacePacket;
import StaticAttributes.PacketSequenceNos;
import StaticAttributes.QuizAttributes;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

class QuizStartPacketListener2 extends Thread
{
	DatagramSocket sock;
	public QuizStartPacketListener2() {
		sock = StaticAttributes.SocketHandler.normalSocket;
	}
	public void run()
	{
		listenQuizStartPacket();
	}
    public void listenQuizStartPacket()
    {
    	try {
			sock.setSoTimeout(00);
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	while( true )
		{
			byte[] b = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket packyy  =  new DatagramPacket(b, b.length);
			
			try
			{
				sock.receive(packyy);
			}
			catch( SocketTimeoutException e1 )
			{
				continue;
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
			
			Packet packet = (Packet)Utilities.deserialize(b);
			
			if( packet.seq_no == PacketSequenceNos.QUIZ_INTERFACE_PACKET_SERVER_SEND && packet.quizPacket == true )
			{
				QuizInterfacePacket qip = (QuizInterfacePacket)Utilities.deserialize(packet.data);
				if( qip.activeGroupName.equals(QuizAttributes.groupName) && qip.activeGroupLeaderID.equals(QuizAttributes.studentID))
				{
					/*
					 * This student is a leader
					 */
					Intent i= new Intent(ActiveTeamAnsWait.staticVar,Leader_question.class);
					ActiveTeamAnsWait.staticVar.startActivity(i);
					break;
					
				}
				else if( qip.activeGroupName.equals(QuizAttributes.groupName) )
				{
					/*
					 * This is a non-leader student of the active group
					 */
					Intent i=new Intent(ActiveTeamAnsWait.staticVar,ActiveTeamQuesWait.class);
					ActiveTeamAnsWait.staticVar.startActivity(i);
					break;
				}
				else
				{
					/*
					 * Other group students
					 */
					Intent i=new Intent(ActiveTeamAnsWait.staticVar,OtherGroupPage.class);
					ActiveTeamAnsWait.staticVar.startActivity(i);
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


public class ActiveTeamAnsWait extends Activity{
	  
	TextView tv;
	public static ActiveTeamAnsWait staticVar;
	public void onCreate(Bundle savedInstanceState) 
	{
	    	super.onCreate(savedInstanceState);
	        setContentView(R.layout.active_team_ans_wait);
	        staticVar = this;
	        tv = (TextView)findViewById(R.id.ataw_tv);
	        tv.setText("Please wait untill other's answer your question");
	        /*
	         * Wait for screen changing packet
	         * Quiz packet
	         * and direct to the leader, team , non team pages accordingly
	         */
	        QuizStartPacketListener qp = new QuizStartPacketListener();
	    	qp.start();
	}
}
