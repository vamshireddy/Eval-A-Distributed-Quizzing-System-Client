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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Multiple_choice extends Activity implements android.view.View.OnClickListener
{
	
	EditText c1,c2,c3,c4,question;
	Button btn;
	String options[] = {"option1", "option2", "option3", "option4"};
	boolean status[] = new boolean[options.length];
    String correctoption;
    DatagramSocket sock;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_choice);
        c1=(EditText)findViewById(R.id.option1);
        c2=(EditText)findViewById(R.id.option2);
        c3=(EditText)findViewById(R.id.option3);
        c4=(EditText)findViewById(R.id.option4);
        btn=(Button)findViewById(R.id.submit);
        sock = StaticAttributes.SocketHandler.normalSocket;
        question=(EditText)findViewById(R.id.question);
        btn.setOnClickListener(this);
        
    }
	public void onClick(View v)     //actions performed after change password button is clicked.
	{   
		final String question1,choice1,choice2,choice3,choice4;
		 
		
		 
		 question1 = question.getText().toString(); //obtaining the questions.
		 choice1=c1.getText().toString();           //choices given by the leader.
		 choice2=c2.getText().toString();
		 choice3=c3.getText().toString();
		 choice4=c4.getText().toString();
		 
		 options[0]=choice1;
		 options[1]=choice2;
		 options[2]=choice3;
		 options[3]=choice4;
		 
		 
		 String correctOption=null;
		 RadioGroup g = (RadioGroup) findViewById(R.id.correctOption_Rg);
		 
	     switch (g.getCheckedRadioButtonId())
	     {
	            case R.id.radio1 :
	            	correctOption = options[0];
	                  break;
	            case R.id.radio2 :
	            	correctOption = options[1];
	                  break;
	            case R.id.radio3 :
	            	correctOption = options[2];
	                  break;
	            case R.id.radio4 :
	            	correctOption = options[3];
	                  break;
	     }
		 
		 QuestionPacket qp = new QuestionPacket(QuizAttributes.groupName, (byte)1);
		 qp.correctAnswerOption = correctOption;
		 qp.options = options;
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
