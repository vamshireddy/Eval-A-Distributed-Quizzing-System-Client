package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.example.peerbased.LeaderPacket;
import com.example.peerbased.Packet;

import QuizPackets.QuizInterfacePacket;
import StaticAttributes.PacketSequenceNos;
import StaticAttributes.PacketTypes;
import StaticAttributes.QuizAttributes;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;



public class AnswerResultPage extends Activity{
	private TextView tv;
	private String result;
	public static AnswerResultPage staticVar;
	public void onCreate(Bundle savedInstanceState) 
	{
	    	super.onCreate(savedInstanceState);
	    	staticVar = this;
	        setContentView(R.layout.answer_result_page);
	        tv = (TextView)findViewById(R.id.arp_tv);
	        Intent i = getIntent();
	        result = i.getExtras().getString("result");
	        if( result.equals("correct") )
	        {
	        	tv.setText("You are right!!");
	        }
	        else if( result.equals("wrong"))
	        {
	        	tv.setText("You are wrong. Better luck next time");
	        }
	        /*
	         * Now listen for screen changing packet
	         */
	        QuizStartPacketListener qp = new QuizStartPacketListener(this);
	    	qp.start();
	}
}
