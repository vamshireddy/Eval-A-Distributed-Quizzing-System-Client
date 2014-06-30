package com.carouseldemo.main;
import StaticAttributes.*;

import com.example.peerbased.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View.OnClickListener;
import StaticAttributes.*;
@SuppressLint("NewApi")

public class Login extends Activity implements OnClickListener {
	
	private Button login;
	private EditText passwordBox,userID;
	private TextView errorText;
	private DatagramSocket socket;
	private String uID;
	private String password;
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		StrictMode.enableDefaults();
		
		/*
		 *  Create a thread to listen for the server's error messages;
		 */

		/*
		 * Initialize the socket timeout to 1000
		 */

    	userID=(EditText)findViewById(R.id.username);
    	/*
    	 * Set the student name in static attribute class, so that every other class can access it
    	 */
		passwordBox=(EditText)findViewById(R.id.passwordField);
		login=(Button)findViewById(R.id.login);
		login.setOnClickListener(this);
		errorText = (TextView)findViewById(R.id.errorText);
		// Set the Visibility of errorBox to false
		errorText.setText("");
		socket = SocketHandler.authSocket;
		try {
			socket.setSoTimeout(500);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search :
                openHindi();
                return true;
            case R.id.action_settings:
                 openEnglish();
                return true;
            case R.id.aboutus :
            	openAboutus();
            	return true;
            case R.id.Help :
            	openHelp();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    
	private void openHelp() {
		// TODO Auto-generated method stub
		Intent i;
		i= new Intent(this,Aboutus.class);
		startActivity(i);
		
		
	}


	private void openAboutus() {
		// TODO Auto-generated method stub
		Intent i;
		i= new Intent(this,Aboutus.class);
		startActivity(i);
		   finish();
	}


	private void openEnglish() {
		// TODO Auto-generated method stub
		
	}


	private void openHindi() {
		// TODO Auto-generated method stub
		
	}

	
	
	
	public void onClick(View v) 
	{
		
		  uID = userID.getText().toString();
		  /*
		   * Initialize the Quiz attribute, so that every class could access it
		   */
		  QuizAttributes.studentID = uID;
		  
		  password = passwordBox.getText().toString();
		  
		  if( uID == null || password == null || uID == "" || password == "" )
		  {
			  /*
			   * Invalid inputs
			   */
			  errorText.setVisibility(View.VISIBLE);
	    	  errorText.setText("Please Enter a valid Username or Password");
			  return;
		  }
		  
		  /*
		   * Send out the request
		   */
		  Utilities.seqNo++;
		  
		  int currentSeqNo = Utilities.seqNo;
		  
		  sendAuthPacket(currentSeqNo);
		  
		  /*
		   * Wait for the reply
		   */
		  
		  Packet recvd_pack = null;
			  
		  byte[] by = new byte[Utilities.MAX_BUFFER_SIZE];
		  
		  DatagramPacket packy = new DatagramPacket(by, by.length);
		  
		  while( true )
		  {
			  try
			  {
				  socket.receive(packy);
			  }
			  catch (SocketTimeoutException e)
			  {
				  errorText.setText("Server is busy at the moment!");
				  return;
			  }
			  catch (IOException e) 
			  {
				  errorText.setText("Your request couldn't be processed at this moment. Please try again!");
				  return;
			  }
			  
				  
			  recvd_pack = (Packet)Utilities.deserialize(by);
			  
			  System.out.println("The seq no is "+recvd_pack.seq_no+" and expected is "+currentSeqNo);
			  
			  if( recvd_pack.type == PacketTypes.AUTHENTICATION_LOGIN && recvd_pack.ack == true && recvd_pack.seq_no == currentSeqNo )
			  {
				  /*
				   * Response from server
				   */
				  
		    	  AuthPacket auth_pack = (AuthPacket) Utilities.deserialize(recvd_pack.data);
		    	  
		    	  if( auth_pack.grantAccess == true )
		    	  {
		    		  	System.out.println("I am granted access");
		    		    QuizAttributes.studentID = uID; 	// Fetch uid from textBox
		    		    
		    		    QuizAttributes.standard = auth_pack.standard;
		    		    
		    		    System.out.println("THE STANDARD IS "+QuizAttributes.standard);
		    		    
		    		    QuizAttributes.studentName = auth_pack.studentName;	// Fetch name from the received packet
		    		    
		    		    errorText.setText("");
		    		    
		    		    Intent i = new Intent(this, MainActivity.class);
		    		    startActivity(i);
		    		    break;
		    	  }
		    	  else
		    	  {
		    		  	System.out.println("I am not granted access");
		    		  	if( auth_pack.errorCode == Utilities.ALREADY_LOGGED )
		    		  	{
		    		  		System.out.println("You are already logged in");
		    		  		errorText.setText("You are already logged in");
		    		  	}
		    		  	else if( auth_pack.errorCode == Utilities.INVALID_FIELDS )
		    		  	{
		    		  		System.out.println("Invalid fields");
		    		  		errorText.setText("The entered fields are invalid");
		    		  	}
		    		  	else if( auth_pack.errorCode == Utilities.INVALID_USER_PASS )
		    		  	{
		    		  		System.out.println("Invalid pass");
		    		  		errorText.setText("Please check your username and password");
		    		  	}
		    		  	break;
		    	  }
		      }
		      else
		      {
		    	  errorText.setText("Invalid request format");
		    	  continue;
		      }
		  }
		  
	}
	
	private void sendAuthPacket(int seq_no)
	{
		AuthPacket ap = new AuthPacket(uID,password,false,false);
		
		System.out.println("Ap is"+ap);
		/*
		 * Create a packet and send it with the current seqNo and Ack = false
		 */
		Packet p = new Packet(seq_no,PacketTypes.AUTHENTICATION_LOGIN, false, Utilities.serialize(ap));
		
		System.out.println("data is "+p.data);
		
	    try
		{
			byte[] packet_buf = Utilities.serialize(p);
			DatagramPacket packet = new DatagramPacket(packet_buf, packet_buf.length, Utilities.serverIP, Utilities.authServerPort); 
		    socket.send(packet);
		    
		    System.out.println("\n\nSENT PACKET\n\n");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

