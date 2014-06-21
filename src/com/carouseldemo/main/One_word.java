package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import com.carouseldemo.main.Multiple_choice;
import com.example.peerbased.Packet;

import QuizPackets.QuestionPacket;
import StaticAttributes.PacketSequenceNos;
import StaticAttributes.PacketTypes;
import StaticAttributes.QuizAttributes;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
    public static One_word staticVar; 
	EditText question,answer;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fillup);
        staticVar = this;
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
		 qp.questionSeqNo = Utilities.quesSeqNo;
		 qp.question = question1;
		 
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
				submit.setText("try");
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
					submit.setEnabled(false);
					submit.setBackgroundColor(Color.RED);
					submit.setText("Sent!");
					QuestionListener ql = new QuestionListener(this);
					ql.start();
					return;
				}
			}
			else
			{
				submit.setText("try");
			}
	}
}
