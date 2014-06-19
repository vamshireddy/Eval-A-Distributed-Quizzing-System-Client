package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.SocketHandler;

import com.example.peerbased.Packet;

import QuizPackets.QuestionPacket;
import QuizPackets.QuizInterfacePacket;
import QuizPackets.ResponsePacket;
import StaticAttributes.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

class QuizStartPacketListenerTrueFalse extends Thread
{
	DatagramSocket sock;
	public boolean running;
	public QuizStartPacketListenerTrueFalse() {
		running = true;
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
    	while( running == true )
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
					Intent i= new Intent(Answer_true_false.staticVar,Leader_question.class);
					Answer_true_false.staticVar.startActivity(i);
					Answer_true_false.staticVar.finish();
					break;
					
				}
				else if( qip.activeGroupName.equals(QuizAttributes.groupName) )
				{
					/*
					 * This is a non-leader student of the active group
					 */
					Intent i=new Intent(Answer_true_false.staticVar,ActiveTeamQuesWait.class);
					Answer_true_false.staticVar.startActivity(i);
					Answer_true_false.staticVar.finish();
					break;
				}
				else
				{
					/*
					 * Other group students
					 */
					Intent i=new Intent(Answer_true_false.staticVar,OtherGroupPage.class);
					Answer_true_false.staticVar.startActivity(i);
					Answer_true_false.staticVar.finish();
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


public class Answer_true_false extends Activity implements OnClickListener 
{
	Button btn;
	TextView question;
	DatagramSocket sock;
	TextView error;
	QuizStartPacketListenerTrueFalse thread;
	public static Answer_true_false staticVar;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_true_false);
        staticVar = this;
        question=(TextView)findViewById(R.id.textView1);
        btn=(Button)findViewById(R.id.button2);
        error = (TextView)findViewById(R.id.errorBoxy);
        question.setText(QuestionAttributes.question);
        sock = StaticAttributes.SocketHandler.normalSocket;
        btn.setOnClickListener(this);
        
        /*
         * Start a thread to listen for screen changing packet
         * This will be stopped when the button is pressed ( Question is answered )
         * Else he will be redirected to the appropriate page on the next turn
         */
        thread = new QuizStartPacketListenerTrueFalse();
        thread.start();
        
    }
	public void onClick(View v)     //actions performed after change password button is clicked.
	{   
		thread.running = false; 
		String answer = null;
		
		RadioGroup g = (RadioGroup) findViewById(R.id.radioGroup1);
		 
		      switch (g.getCheckedRadioButtonId())
		       {
		            case R.id.true1 :
	
                          answer="true";
                          Toast t1 = Toast.makeText(this, "Pressed true", 2000);
  						  t1.show();
		                  break;
		 
		            case R.id.false1 :
			              answer="false";
			              Toast t2 = Toast.makeText(this, "Pressed false", 2000);
						  t2.show();
		                  break;
		        }
		
		if( answer == null )
		{
			/*
			 * None of the options are selected
			 */
			error.setText("Please select an Option");
			return;
		}
		      
		System.out.println("Answer is "+answer);
		ResponsePacket rp = new ResponsePacket(QuestionAttributes.questionSeqNo, QuizAttributes.studentID,
				QuestionAttributes.question, answer, false, false);
		
		Packet p = new Packet(PacketSequenceNos.QUIZ_RESPONSE_CLIENT_SEND, false, false, false, Utilities.serialize(rp));
		p.quizPacket = true;
		
		System.out.println("qp is "+p.quizPacket+" p.seq : "+p.seq_no+" rp.ans "+rp.answer);
		
		byte bytes[] = Utilities.serialize(p);
		
		DatagramPacket dpp = new DatagramPacket(bytes, bytes.length, Utilities.serverIP, Utilities.servPort);
		
		try {
			sock.send(dpp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				error.setText("Please try again!");
				break;
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
			Packet rcvPack = (Packet)Utilities.deserialize(byR);
			if( rcvPack.seq_no == PacketSequenceNos.QUIZ_RESPONSE_SERVER_ACK && rcvPack.quizPacket == true )
			{
				ResponsePacket rpack = (ResponsePacket)Utilities.deserialize(rcvPack.data);
				if( rpack.ack == true )
				{
					if( rpack.result == true )
					{
						/*
						 * Correct answer
						 */
						Intent i=new Intent(this,AnswerResultPage.class);
						i.putExtra("result", "correct");
					    startActivity(i);
					    finish();
					    break;
					}
					else if( rpack.result == false )
					{
						/*
						 * Wrong answer
						 */
						Intent i=new Intent(this,AnswerResultPage.class);
						i.putExtra("result", "wrong");
					    startActivity(i);
					    finish();
					    break;
					}
					btn.setEnabled(false);
				}
				else
				{
					System.out.println("Error!!");
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
