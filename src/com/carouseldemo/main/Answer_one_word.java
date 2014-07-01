package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import StaticAttributes.*;

import com.example.peerbased.Packet;

import QuizPackets.ResponsePacket;
import StaticAttributes.PacketSequenceNos;
import StaticAttributes.PacketTypes;
import StaticAttributes.QuestionAttributes;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Answer_one_word extends Activity implements OnClickListener 
{
	Button btn;
	TextView question;
	EditText answer;
	TextView error;
	DatagramSocket sock;
	QuizStartPacketListener thread;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_one_word);
        
        question=(TextView)findViewById(R.id.textView1);
        btn=(Button)findViewById(R.id.button2);
        answer=(EditText)findViewById(R.id.answer);
        error = (TextView)findViewById(R.id.error_ans_one_word);
        question.setText(QuestionAttributes.question);
        sock = StaticAttributes.SocketHandler.normalSocket;
        btn.setOnClickListener(this);
        /*
         * Start a thread to listen for screen changing packet
         * This will be stopped when the button is pressed ( Question is answered )
         * Else he will be redirected to the appropriate page on the next turn
         */
        thread = new QuizStartPacketListener(this);
        thread.start();
    }
	public void onClick(View v)     //actions performed after change password button is clicked.
	{
		/*
		 * Stop the listening thread
		 */
		
		thread.Suspend();
		/*
		 * 	Sleep for sometime so that the thread gets suspended
		 */
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*
		 * Get the values
		 */
		
		String ans = answer.getText().toString();
		
		if( answer == null || ans.equals("") )
		{
			/*
			 * None of the options are selected
			 */
			error.setText("Invalid answer field");
			/*
			 * Make the button enabled
			 */
			btn.setEnabled(true);
			return;
		}
		
		int currentSeqNo = Utilities.seqNo++;
		
		ResponsePacket rp = new ResponsePacket(QuestionAttributes.questionSeqNo, QuizAttributes.studentID,
				 ans, false, false);
		
		Packet p = new Packet(currentSeqNo, PacketTypes.QUESTION_RESPONSE,false, Utilities.serialize(rp));
		
		System.out.println("qp is "+p.quizPacket+" p.seq : "+p.seq_no+" rp.ans "+rp.answer);
		
		byte bytes[] = Utilities.serialize(p);
		
		DatagramPacket dpp = new DatagramPacket(bytes, bytes.length, Utilities.serverIP, Utilities.servPort);
		
		try {
			sock.send(dpp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
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
				error.setText("Please try again after 2 seconds!");
				/*
				 * Enable the button
				 */
				btn.setEnabled(true);
				/*
				 * Resume the thread again, so that it will listen for the screen changing packet
				 */
				thread.Resume();
				return;
			}
			catch (IOException e)
			{
				System.out.println("Exception !!");
				e.printStackTrace();
				System.exit(0);
			}
			System.out.println("Packet ques receveived!!!!!!!!");
			/*
			 * Packet is received from Teacher
			 */
			Packet rcvPack = (Packet)Utilities.deserialize(byR);
			if( rcvPack.seq_no == currentSeqNo && rcvPack.type == PacketTypes.QUESTION_RESPONSE && rcvPack.ack == true )
			{
				ResponsePacket rpack = (ResponsePacket)Utilities.deserialize(rcvPack.data);
				if( rpack.ack == true )
				{
					thread.Resume();
					error.setText("You response is recorded. Please wait!");
					btn.setBackgroundColor(Color.RED);
					btn.setEnabled(false);
				    break;
				}
				else
				{
					System.out.println("Error!!");
					error.setText("Aberrant packet received");
					continue;
				}
			}
			else
			{
				error.setText("NON QUIZ PACKET RECVD.. OMGGGG!");
				continue;
			}
		}
		
     }
}
