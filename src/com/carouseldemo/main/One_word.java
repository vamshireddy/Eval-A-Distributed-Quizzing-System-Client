package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.example.peerbased.Packet;

import QuizPackets.QuestionPacket;
import StaticAttributes.PacketSequenceNos;
import StaticAttributes.QuizAttributes;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class One_word extends Activity implements OnClickListener 
{
	
    Button submit;
    DatagramSocket sock;
  
	EditText question,answer;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fillup);
        submit=(Button)findViewById(R.id.button1);
        question=(EditText)findViewById(R.id.fillupq);
        answer=(EditText)findViewById(R.id.fillupa);
        sock = StaticAttributes.SocketHandler.normalSocket;
        submit.setOnClickListener(this);
    }
	public void onClick(View v)     //actions performed after change password button is clicked.
	{   
	     String question1,answer1;
		 question1 = question.getText().toString();
		 answer1 = answer.getText().toString();
		
		 QuestionPacket qp = new QuestionPacket(QuizAttributes.groupName, (byte)3);
		 qp.correctAnswerOption = answer1;
		 qp.options = null;
		 qp.question = question1;
		 
		 Packet p = new Packet(PacketSequenceNos.QUIZ_QUESTION_PACKET_CLIENT_SEND, false, false, false, Utilities.serialize(qp));
		 p.quizPacket = true;
		 
		 byte[] bytes = Utilities.serialize(p);
			
			DatagramPacket pack = new DatagramPacket(bytes, bytes.length, Utilities.serverIP, Utilities.servPort);
			try {
				sock.send(pack);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("APcket sendtTT !!");
			System.out.println("Waiting for packy! - bahar");
			int aa;
			try {
				aa = sock.getSoTimeout();
				System.out.println("TImeout : "+aa);
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while( true )
			{
				/*
				 * Now wait for the authentication of the packet
				 */
				System.out.println("Waiting for packy!");
				byte[] byR = new byte[Utilities.MAX_BUFFER_SIZE];
				DatagramPacket packyR = new DatagramPacket(byR, byR.length);
				try
				{
					sock.receive(packyR);
				}
				catch( SocketTimeoutException e )
				{
					System.out.println("Timeout!~");
					continue;
				}
				catch (IOException e)
				{
					System.out.println("Expecpton !!");
					e.printStackTrace();
					System.exit(0);
				}
				System.out.println("Packet ques receveived!!!!!!!!");
				/*
				 * Packet is received from Teacher
				 */
				Packet recvpack = (Packet)Utilities.deserialize(byR);
				if( recvpack.seq_no == PacketSequenceNos.QUIZ_QUESTION_PACKET_SERVER_ACK && recvpack.quizPacket == true )
				{
					System.out.println("Its a ques packet");
					QuestionPacket qpack = (QuestionPacket) Utilities.deserialize(recvpack.data);
					
					if( qpack.questionAuthenticated == true )
					{
						 System.out.println("ITS CORRECT");
						/*
						 * Question is accepted by teacher
						 */
						Toast t1 = Toast.makeText(this, "Question Accepted by teacher", 2000);
						t1.show();
					    Intent i=new Intent(this,ActiveTeamAnsWait.class);
					    startActivity(i);
					    finish();
					    break;
					}
					else
					{
						/*
						 * Question is rejected by teacher
						 */
						 System.out.println("ITS NOT CORRECT");
						Toast t1 = Toast.makeText(this, "Question rejected by teacher", 2000);
						t1.show();
						Intent i=new Intent(this,Leader_question.class);
					    startActivity(i);
					    finish();
					    break;
					}
				}
				else
				{
					 System.out.println("Noooooo!");
					continue;
				}
			}
	}
}
