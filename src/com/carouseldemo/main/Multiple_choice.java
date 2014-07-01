package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.SocketHandler;

import com.example.peerbased.LeaderPacket;
import com.example.peerbased.Packet;

import QuizPackets.QuestionPacket;
import StaticAttributes.PacketSequenceNos;
import StaticAttributes.PacketTypes;
import StaticAttributes.QuizAttributes;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.Toast;


class QuestionListener extends Thread
{
	private DatagramSocket sock;
	Activity act;
	public QuestionListener(Activity act) {
		this.act = act;
		sock = StaticAttributes.SocketHandler.normalSocket;
	}
	public void run()
	{
		try{
			sock.setSoTimeout(Utilities.SCREEN_CHANGE_TIMEOUT);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		boolean rcvd = false;
		int activityFlag = -1;
		
		while( true )
		{
			System.out.println("Listening for screen changing packet in the listnere!!!!!");
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
					System.out.println("i am going out nowWW1.. changing act");
					if( activityFlag == 1 )
					{
						Intent i=new Intent(act,ActiveTeamAnsWait.class);
						act.startActivity(i);
//						act.finish();
					}
					else if ( activityFlag == 2 )
					{
						Intent i=new Intent(act,Leader_question.class);
						act.startActivity(i);
//						act.finish();
					}
					Utilities.quesSeqNo++;
					break;
				}
				else
				{
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
			System.out.println("Packet is receviedd");
			Packet packetRcvd = (Packet)Utilities.deserialize(b);
			
			if( packetRcvd.type == PacketTypes.QUESTION_VALIDITY && packetRcvd.ack == false )
			{
				System.out.println("I am just inside!");
				if( rcvd == false )
				{
					QuestionPacket rcvdQpack = (QuestionPacket)Utilities.deserialize(packetRcvd.data);
					
					if( rcvdQpack.questionSeqNo == Utilities.quesSeqNo )
					{
						System.out.println("question is fine. and received and seqno is also correct!");
						if( rcvdQpack.questionAuthenticated == true )
						{
							activityFlag = 1;
						}
						else
						{
							activityFlag = 2;
						}
						rcvd = true;
					}
					else
					{
						continue;
					}
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
			else
			{
				continue;
			}
		}
	}
}


public class Multiple_choice extends Activity implements android.view.View.OnClickListener
{
	EditText c1,c2,c3,c4,question;
	Button btn;
	boolean wait;
	ProgressDialog pd1;
	Handler h1;
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
        wait = true;
        c1=(EditText)findViewById(R.id.option1);
        c2=(EditText)findViewById(R.id.option2);
        c3=(EditText)findViewById(R.id.option3);
        c4=(EditText)findViewById(R.id.option4);
        btn=(Button)findViewById(R.id.submit);
        sock = StaticAttributes.SocketHandler.normalSocket;
        question=(EditText)findViewById(R.id.question);
        btn.setOnClickListener(this);
	    
	    h1 = new Handler()
	    {

			@Override
			public void handleMessage(Message msg) 
			{
				super.handleMessage(msg);
				if( wait == false )
				{
					pd1.dismiss();
				}
				else
				{
					pd1.incrementProgressBy(1);
					h1.sendEmptyMessageDelayed(0, 200);
				}
				
			}
	    	
	    };
    }
	public void onClick(View v)     //actions performed after change password button is clicked.
	{   
		wait = true;
        pd1 = new ProgressDialog(this);
	    pd1.setProgress(0);
		pd1.setTitle("Please wait!");
	    pd1.setMessage("Contacting Server");
	    h1.sendEmptyMessage(0);
	    pd1.show();
		
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
		 qp.questionSeqNo = Utilities.quesSeqNo;
		 qp.options = options;
		 qp.question = question1;
		 
		 int currentSeqNo = Utilities.seqNo++;
		 
		 Packet p = new Packet(currentSeqNo, PacketTypes.QUESTION_SEND , false, Utilities.serialize(qp));

		 System.out.println("The packet param are : "+currentSeqNo+" : "+p.type+" ack : "+p.ack);
		 
		 byte[] bytes = Utilities.serialize(p);
			
			DatagramPacket packBytes = new DatagramPacket(bytes, bytes.length, Utilities.serverIP, Utilities.servPort);
			try {
				sock.send(packBytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			 * Now get an ack from the sever
			 */

			byte[] b = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket pack  =  new DatagramPacket(b, b.length);
			
			try {
				sock.receive(pack);
			}
			catch( SocketTimeoutException e )
			{
				wait = false;
				btn.setText("TRY");
				btn.setBackgroundColor(Color.GREEN);
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * ACK received
			 */
			Packet ackPack = (Packet)Utilities.deserialize(b);
			if( ackPack.seq_no == currentSeqNo && ackPack.ack == true && ackPack.type == PacketTypes.QUESTION_SEND )
			{
				btn.setEnabled(false);
				btn.setBackgroundColor(Color.RED);
				btn.setText("Sent!");
				QuestionListener ql = new QuestionListener(this);
				ql.start();
				return;
			}
			else
			{
				wait = false;
				btn.setText("TRY");
				btn.setBackgroundColor(Color.GREEN);
			}
			wait = false;
	}
}
