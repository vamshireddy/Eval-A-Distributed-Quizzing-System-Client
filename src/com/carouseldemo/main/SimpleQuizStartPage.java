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

class QuizStartPacketListener extends Thread
{
	DatagramSocket sock;
	public QuizStartPacketListener() {
		sock = StaticAttributes.SocketHandler.normalSocket;
	}
	public void run()
	{
		listenQuizStartPacket();
	}
    public void listenQuizStartPacket()
    {
    	try {
			sock.setSoTimeout(1000);
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
					Intent i= new Intent(SimpleQuizStartPage.staticVar,Leader_question.class);
					SimpleQuizStartPage.staticVar.startActivity(i);
					break;
					
				}
				else if( qip.activeGroupName.equals(QuizAttributes.groupName) )
				{
					/*
					 * This is a non-leader student of the active group
					 */
					Intent i=new Intent(SimpleQuizStartPage.staticVar,ActiveTeamQuesWait.class);
					SimpleQuizStartPage.staticVar.startActivity(i);
					break;
				}
				else
				{
					/*
					 * Other group students
					 */
					Intent i=new Intent(SimpleQuizStartPage.staticVar,OtherGroupPage.class);
					SimpleQuizStartPage.staticVar.startActivity(i);
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

public class SimpleQuizStartPage extends Activity {

	TextView tv;
	DatagramSocket sock;
	public static SimpleQuizStartPage staticVar;
	
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_quiz_start_page);
        staticVar = this;
        sock = StaticAttributes.SocketHandler.normalSocket;
        tv = (TextView)findViewById(R.id.textViewLeaderSCP);
        tv.setText("Please Wait untill the Quiz starts!");
        /*
         * Students are waiting for the Screen Changing packet ( Team )
         */
        QuizStartPacketListener qp = new QuizStartPacketListener();
    	qp.start();
    }
}
