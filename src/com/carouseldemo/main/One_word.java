package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import com.example.peerbased.Packet;

import QuizPackets.QuestionPacket;
import StaticAttributes.PacketSequenceNos;
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
		
		 QuestionPacket qp = new QuestionPacket(StaticAttributes.QuizAttributes.groupName, (byte)3);
		 qp.correctAnswerOption = answer1;
		 qp.options = null;
		 qp.question = question1;
		 Packet p = new Packet(PacketSequenceNos.QUIZ_QUESTION_PACKET_CLIENT_SEND, false, false, false, Utilities.serialize(qp));
			
		 p.quizPacket = true;
			
			
		 byte[] bytes = Utilities.serialize(p);
			
			
		 DatagramPacket pack = new DatagramPacket(bytes, bytes.length, Utilities.serverIP, Utilities.servPort);
			
			
		 while( true )
		 {
			 	try 
			 	{
					sock.send(pack);
				} 
			 	catch (IOException e) 
			 	{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * Now wait for the authentication of the packet
				 */
				byte[] by = new byte[Utilities.MAX_BUFFER_SIZE];
				DatagramPacket packy = new DatagramPacket(by, by.length);
				try
				{
					sock.receive(packy);
				}
				catch( SocketTimeoutException e )
				{
					break;
				}
				catch (IOException e)
				{
					e.printStackTrace();
					System.exit(0);
				}
				/*
				 * Packet is received from Teacher
				 */
				Packet recvpack = (Packet)Utilities.deserialize(bytes);
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
					    Intent i=new Intent(this,SimpleCommonPage.class);
					    startActivity(i);
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
