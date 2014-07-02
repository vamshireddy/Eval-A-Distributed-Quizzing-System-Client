package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.example.peerbased.LeaderPacket;
import com.example.peerbased.Packet;

import QuizPackets.QuizInterfacePacket;
import QuizPackets.QuizResultPacket;
import StaticAttributes.PacketSequenceNos;
import StaticAttributes.PacketTypes;
import StaticAttributes.QuizAttributes;
import StaticAttributes.QuizResults;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

class QuizStartPacketListener extends Thread
{
	DatagramSocket sock;
	boolean running;
	Activity act;
	boolean suspended;
	/*
	 * Running will be set only by the answer pages to migrate to next question, on not answering
	 */
	public QuizStartPacketListener(Activity act) {
		this.act = act;
		suspended = false;
		running = true;
		sock = StaticAttributes.SocketHandler.normalSocket;
	}
	
	public void run()
	{
		listenQuizStartPacket();
	}
	
	public void Suspend()
	{
	      suspended = true;
	}
	
	synchronized void Resume()
	{
	      suspended = false;
	      notify();
	}
	
    public void listenQuizStartPacket()
    {
		boolean rcvd = false;
		int activityFlag = -1;
    	try {
			sock.setSoTimeout(1000);
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	while( running )
		{
    		
    		/*
    		 * Check if the thread is suspended or not
    		 * If it is then wait
    		 */
    		
    		synchronized(this) {
    			while(suspended)
    			{
    				try {
    					wait();
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					System.out.println("YYYYAYAYAYAY !! i am awake!!");
    				}
    			}
    		}
    		
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
					System.out.println("I am about to change my screen!!!!!");
					if( activityFlag == 1 )
					{
						Intent i= new Intent(act,Leader_question.class);
						act.startActivity(i);
					}
					else if ( activityFlag == 2 )
					{
						Intent i=new Intent(act,ActiveTeamQuesWait.class);
						act.startActivity(i);
					}
					else if( activityFlag == 3 )
					{
						Intent i=new Intent(act,OtherGroupPage.class);
						act.startActivity(i);
					}
					else if( activityFlag == 4 )
					{
						/*
						 * Quiz has ended
						 * Show the results
						 */
						Intent i = new Intent(act,quizEndPage.class);
						act.startActivity(i);
					}
					return;
				}
				else
				{
					System.out.println("I am in else part!!!!!!!!!!!!!!!!!. Please help me!");
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
			
			System.out.println("I am outside!!!!!!!!!!!!!!! and i got a pakcet");
			
			Packet packetRcvd = (Packet)Utilities.deserialize(b);
			
			if( packetRcvd.type == PacketTypes.QUIZ_INTERFACE_START_PACKET && packetRcvd.ack == false )
			{

				System.out.println("I am quiz interface packet and my ack is false!!!!!!!!!!!!!!!!!");
				if( rcvd == false )
				{

					System.out.println("I am inside rcvd thing !!!!!!!!!!!!!!!11");
					QuizInterfacePacket qip = (QuizInterfacePacket)Utilities.deserialize(packetRcvd.data);
					if(qip.activeGroupName.equals(QuizAttributes.groupName) && qip.activeGroupLeaderID.equals(QuizAttributes.studentID))
					{
						/*
						 * leader
						 */
						System.out.println("I am setting activity!");
						activityFlag = 1;
					}
					else if( qip.activeGroupName.equals(QuizAttributes.groupName) )
					{
						/*
						 * team mates
						 */
						System.out.println("I am setting activity!");
						activityFlag = 2;
					}
					else
					{
						System.out.println("I am setting activity!");
						activityFlag = 3;
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
			else if( packetRcvd.type == PacketTypes.QUIZ_END_PACKET && packetRcvd.ack == false)
			{
				if( rcvd == false )
				{
					QuizResultPacket qrp = (QuizResultPacket)Utilities.deserialize(packetRcvd.data);
					QuizResults.noOfQuesAttempted = qrp.noOfQuesAttempted;
					QuizResults.noOfQuesCorrect = qrp.noOfQuesCorrect;
					QuizResults.marks = qrp.marks;
					activityFlag = 4;
					rcvd = true;
				}	
				/*
				 * Now send ACK back
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
	
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_quiz_start_page);
        sock = StaticAttributes.SocketHandler.normalSocket;
        tv = (TextView)findViewById(R.id.textViewLeaderSCP);
        tv.setText("Please Wait untill the Quiz starts!");
        /*
         * Students are waiting for the Screen Changing packet ( Team )
         */
        QuizStartPacketListener qp = new QuizStartPacketListener(this);
    	qp.start();
    }
}
