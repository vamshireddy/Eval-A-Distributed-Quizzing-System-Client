package com.carouseldemo.main;


import java.io.IOException;

import StaticAttributes.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import com.example.peerbased.Packet;

import QuizPackets.QuestionPacket;
import QuizPackets.ResponsePacket;
import StaticAttributes.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Answer_multiple_choice extends Activity implements OnClickListener 
{
	Button btn;
	TextView question;
	TextView marks;
	RadioButton option1;
	RadioButton option2;
	RadioButton option3;
	RadioButton option4;
	DatagramSocket sock;
	QuizStartPacketListener thread;
	TextView error;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_multiple_choice);
        btn=(Button)findViewById(R.id.submit_ans_mul);
        question = (TextView)findViewById(R.id.ans_mul_question);
        option1 = (RadioButton)findViewById(R.id.radio1);
        option2 = (RadioButton)findViewById(R.id.radio2);
        option3 = (RadioButton)findViewById(R.id.radio3);
        option4 = (RadioButton)findViewById(R.id.radio4);
        marks = (TextView)findViewById(R.id.marksBox);
        error = (TextView)findViewById(R.id.error_ansMul);
        sock = StaticAttributes.SocketHandler.normalSocket;
        /*
         * Fill the details
         */
        question.setText(QuestionAttributes.question);
        option1.setText(QuestionAttributes.options[0]);
        option2.setText(QuestionAttributes.options[1]);
        option3.setText(QuestionAttributes.options[2]);
        option4.setText(QuestionAttributes.options[3]);
        marks.setText("You will get "+QuestionAttributes.level+" marks for the correct answer");
        /*
         * Add level too!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! TODO
         */
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
		thread.running = false; 
		/*
		 * Sleep for 500ms so that the above  listening thread gets killed
		 */
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String answer = null;
		RadioGroup g = (RadioGroup)findViewById(R.id.radioGroup1);
		
		switch( g.getCheckedRadioButtonId() )
		{
			case R.id.radio1 :
				answer = option1.getText().toString();
				break;
			case R.id.radio2 :
				answer = option2.getText().toString();
				break;
			case R.id.radio3 :
				answer = option3.getText().toString();
				break;
			case R.id.radio4 :
				answer = option4.getText().toString();
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
		
		ResponsePacket rp = new ResponsePacket(QuestionAttributes.questionSeqNo, QuizAttributes.studentID,
				 answer, false, false);
		
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
				error.setText("Please try again!");
				thread = new QuizStartPacketListener(this);
			    thread.start();
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
			error.setText("RECVD");
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
					    //finish();
					    Toast t1 = Toast.makeText(this, "Your Answer is right!", 2000);
						t1.show();
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
					    //finish();
					    Toast t1 = Toast.makeText(this, "Your Answer is wrong!", 2000);
						t1.show();
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