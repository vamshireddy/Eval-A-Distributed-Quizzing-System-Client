package com.carouseldemo.main;

import java.io.IOException;

import StaticAttributes.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class True_false extends Activity  implements OnClickListener 
{
	
	RadioButton t1,f1;
	EditText question;
	DatagramSocket sock;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.true_false);
        t1=(RadioButton)findViewById(R.id.radioButton1);
        f1=(RadioButton)findViewById(R.id.radioButton2);
        question = (EditText)findViewById(R.id.question);
        sock = StaticAttributes.SocketHandler.normalSocket;
        t1.setOnClickListener(this);
        f1.setOnClickListener(this);     
    }
	public void onClick(View v)     //actions performed after change password button is clicked.
	{
		 String ques = "";
		 String ans = "";
		 String[] options = {"true","false"};
		 
		 // Get question from the question textbox
		 ques = question.getText().toString();
		 /*
		  * Check which one is pressed ( true or false )
		  */
		 
		 if( v.getId() == t1.getId() )
		 {
			 /*
			  * true is selected
			  */
			 System.out.println("-----------True selected");
			 ans = "true";
		 }
		 else if( v.getId() == f1.getId() )
		 {
			 /*
			  * False is selected
			  */
			 System.out.println("false selected");
			 ans = "false";
		 }
		 System.out.println("--------------------------------");
		 QuestionPacket qp = new QuestionPacket(QuizAttributes.groupName, (byte)2);
		 qp.correctAnswerOption = ans;
		 qp.options = options;
		 qp.question = ques;
		 
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
					    Intent i=new Intent(this,SimpleCommonPageForLeader.class);
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
