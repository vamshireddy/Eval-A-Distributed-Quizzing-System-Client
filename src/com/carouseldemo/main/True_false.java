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
import StaticAttributes.PacketTypes;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
	EditText question_tf;
	DatagramSocket sock;
	TextView error;
	public static True_false staticVar;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.true_false);
        staticVar = this;
        t1=(RadioButton)findViewById(R.id.radioButton1);
        f1=(RadioButton)findViewById(R.id.radioButton2);
        error = (TextView)findViewById(R.id.errorTextinTrueAndFalse);
        question_tf = (EditText)findViewById(R.id.questiontf);
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
		 ques = question_tf.getText().toString();
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
		 qp.questionSeqNo = Utilities.quesSeqNo;
		 qp.question = ques;
		 
		 Packet p = new Packet(PacketSequenceNos.QUIZ_QUESTION_PACKET_CLIENT_SEND, false, false, false, Utilities.serialize(qp));
		 p.quizPacket = true;
		 
		 byte[] bytes = Utilities.serialize(p);
			
			DatagramPacket sendPacky = new DatagramPacket(bytes, bytes.length, Utilities.serverIP, Utilities.servPort);
			try {
				sock.send(sendPacky);
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
				error.setText("Try again!");
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			 * ACK received
			 */
			Packet ackPack = (Packet)Utilities.deserialize(b);
			if( ackPack.ack == true && ackPack.type == PacketTypes.QUESTION_ACK)
			{
				QuestionPacket qprecvd = (QuestionPacket)Utilities.deserialize(ackPack.data);
				
				if( qprecvd.questionSeqNo == Utilities.quesSeqNo )
				{
					/*
					 * Check is the sequence number is same
					 */
					t1.setEnabled(false);
					f1.setEnabled(false);
					t1.setBackgroundColor(Color.RED);
					f1.setBackgroundColor(Color.RED);
					error.setText("sent!");
					QuestionListener ql = new QuestionListener(this);
					ql.start();
					return;	
				}
			}
			else
			{
				error.setText("Try again!");
			}
	}
}
