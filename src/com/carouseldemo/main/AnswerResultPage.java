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



class QuizStartPacketListener1 extends Thread
{
	DatagramSocket sock;
	public QuizStartPacketListener1() {
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
					Intent i= new Intent(AnswerResultPage.staticVar,Leader_question.class);
					AnswerResultPage.staticVar.startActivity(i);
					AnswerResultPage.staticVar.finish();
					break;
					
				}
				else if( qip.activeGroupName.equals(QuizAttributes.groupName) )
				{
					/*
					 * This is a non-leader student of the active group
					 */
					Intent i=new Intent(AnswerResultPage.staticVar,ActiveTeamQuesWait.class);
					AnswerResultPage.staticVar.startActivity(i);
					AnswerResultPage.staticVar.finish();
					break;
				}
				else
				{
					/*
					 * Other group students
					 */
					Intent i=new Intent(AnswerResultPage.staticVar,OtherGroupPage.class);
					AnswerResultPage.staticVar.startActivity(i);
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


public class AnswerResultPage extends Activity{
	private TextView tv;
	private String result;
	public static AnswerResultPage staticVar;
	public void onCreate(Bundle savedInstanceState) 
	{
	    	super.onCreate(savedInstanceState);
	    	staticVar = this;
	        setContentView(R.layout.answer_result_page);
	        tv = (TextView)findViewById(R.id.arp_tv);
	        Intent i = getIntent();
	        result = i.getExtras().getString("result");
	        if( result.equals("correct") )
	        {
	        	tv.setText("You are right!!");
	        }
	        else if( result.equals("wrong"))
	        {
	        	tv.setText("You are wrong. Better luck next time");
	        }
	        /*
	         * Now listen for screen changing packet
	         */
	        QuizStartPacketListener qp = new QuizStartPacketListener();
	    	qp.start();
	}
}
