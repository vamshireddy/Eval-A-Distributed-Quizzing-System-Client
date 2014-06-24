package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.SocketHandler;

import StaticAttributes.Utilities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

class CleanBuffers extends Thread
{
	DatagramSocket sock;
	public CleanBuffers() {
		sock = StaticAttributes.SocketHandler.normalSocket;
	}
	public void run()
	{
		int initTimeout = 1;
		try {
			initTimeout = sock.getSoTimeout();
			// 1 second
			sock.setSoTimeout(500);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		while(true)
		{
			byte[] b  = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket p = new DatagramPacket(b, b.length);
			try {
				sock.receive(p);
			}
			catch( SocketTimeoutException e1)
			{
				/*
				 * This exception occurs when there are no packets for the specified timeout period.
				 * Buffer is clean!!
				 */
				try {
					sock.setSoTimeout(initTimeout);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}


public class Leader_question extends Activity  implements OnClickListener 
{
	
	Button mcq,truefalse,fill;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leader_question);
        
        mcq=(Button)findViewById(R.id.button1);
        truefalse=(Button)findViewById(R.id.button2);   
        fill=(Button)findViewById(R.id.button3);
        
       
        truefalse.setOnClickListener(this);
        mcq.setOnClickListener(this);
        fill.setOnClickListener(this);
        new CleanBuffers().start();
    }
	public void onClick(View v)     //actions performed after  buttons are clicked.
	{   
		    Intent i;
			switch (v.getId())
			{
		    case R.id.button1:
		    	//Toast.makeText(this, "You clicked multiple choice.", 1000).show();
		    	 i = new Intent(this,Multiple_choice.class);
				startActivity(i);
//				finish();
		        break;
		    case R.id.button2:
		    //	Toast.makeText(this, "You clicked true false questions.", 1000).show();
		    	 i = new Intent(this,True_false.class);
				startActivity(i);
//				finish();
				break;
		    case R.id.button3:
		    	//Toast.makeText(this, "You clicked one word questions.", 1000).show();
		    	 i = new Intent(this,One_word.class);
				startActivity(i);
//				finish();
				break;
			}
	}

}
