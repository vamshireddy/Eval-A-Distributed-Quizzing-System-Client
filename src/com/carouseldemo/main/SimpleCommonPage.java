package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.logging.SocketHandler;
import StaticAttributes.*;

import com.example.peerbased.Packet;

import QuizPackets.QuestionPacket;
import StaticAttributes.PacketSequenceNos;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


class Listener extends Thread
{
	DatagramSocket sock;
	
	public Listener() {
		sock = StaticAttributes.SocketHandler.normalSocket;	
	}
	public void run()
	{
		while( true )
		{
			byte[] by = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket packy = new DatagramPacket(by, by.length);
			try
			{
				sock.receive(packy);
			}
			catch( SocketTimeoutException e )
			{
				continue;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.exit(0);
			}
			System.out.println("WAHHH!");
			
			 // Packet is received from Teacher
			 
			Packet recvpack = (Packet)Utilities.deserialize(by);
			if( recvpack.seq_no == PacketSequenceNos.QUIZ_QUESTION_BROADCAST_SERVER_SEND && recvpack.quizPacket == true)
			{
				QuestionPacket qp = (QuestionPacket)Utilities.deserialize(recvpack.data);
				if( !QuizAttributes.groupName.equals(qp.groupName) )
				{
					QuestionAttributes.question = qp.question;
					QuestionAttributes.answer = qp.correctAnswerOption;
					QuestionAttributes.options = qp.options;
					QuestionAttributes.level = qp.level;
					QuestionAttributes.questionType = qp.questionType;
					if(  qp.questionType == 1 )
					{
						Intent i = new Intent(SimpleCommonPage.staticAct, Answer_multiple_choice.class);
						SimpleCommonPage.staticAct.startActivity(i);
					}
					else if(  qp.questionType == 2 )
					{
						Intent i = new Intent(SimpleCommonPage.staticAct, Answer_true_false.class);
						SimpleCommonPage.staticAct.startActivity(i);
					}
					else if(  qp.questionType == 3 )
					{
						Intent i = new Intent(SimpleCommonPage.staticAct, Answer_one_word.class);
						SimpleCommonPage.staticAct.startActivity(i);
					}
					break;
				}
				else
				{
					continue;
				}
			}
			else
			{
				continue;
			}
		} 
	}
}

public class SimpleCommonPage extends Activity{
	
	public static SimpleCommonPage staticAct;
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simplecommonpage);
        staticAct = this;
        Listener l = new Listener();
        l.start();
    }
}
