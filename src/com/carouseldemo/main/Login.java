package com.carouseldemo.main;
import StaticAttributes.*;
import com.example.peerbased.*;




import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
		
		// Create a thread to listen for the server's error messages;
		
		
    	userID=(EditText)findViewById(R.id.username);
    	// Set the student name in static attribute class, so that every other class can access it
		passwordBox=(EditText)findViewById(R.id.passwordField);
		login=(Button)findViewById(R.id.login);
		login.setOnClickListener(this);
		errorText = (TextView)findViewById(R.id.errorText);
		// Set the Visibility of errorBox to false
		errorText.setVisibility(View.INVISIBLE);
		socket = SocketHandler.authSocket;
		
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
		  sendAuthPacket();
		  
		  Packet recvd_pack = null;
			  
		  byte[] by = new byte[Utilities.MAX_BUFFER_SIZE];
		  DatagramPacket packy = new DatagramPacket(by, by.length);
		  try
		  {
			  socket.receive(packy);
		  }
		  catch (IOException e) 
		  {
			  errorText.setText("Your request couldn't be processed at this moment. Please try again!");
			  errorText.setVisibility(View.VISIBLE);
			  return;
		  }
			  
		  recvd_pack = (Packet)Utilities.deserialize(by);
		  
		 if( recvd_pack.auth_packet == true && recvd_pack.seq_no == PacketSequenceNos.AUTHENTICATION_SEND_SERVER )
		 {
	    	  AuthPacket auth_pack = (AuthPacket) Utilities.deserialize(recvd_pack.data);
	    	  
	    	  if( auth_pack.grantAccess == true )
	    	  {
	    		    QuizAttributes.studentID = uID; 	// Fetch uid from textBox
	    		    QuizAttributes.studentName = auth_pack.studentName;	// Fetch name from the received packet
	    		    errorText.setText("");
	    		    Intent i = new Intent(this, MainActivity.class);
	    			startActivity(i);	
	    	  }
	    	  else
	    	  {
	    		  	errorText.setVisibility(View.VISIBLE);
	    		  	if( auth_pack.errorCode == Utilities.ALREADY_LOGGED )
	    		  	{
	    		  		errorText.setText("You are already logged in");
	    		  	}
	    		  	else if( auth_pack.errorCode == Utilities.INVALID_FIELDS )
	    		  	{
	    		  		errorText.setText("The entered fields are invalid");
	    		  	}
	    		  	else if( auth_pack.errorCode == Utilities.INVALID_USER_PASS )
	    		  	{
	    		  		errorText.setText("Please check your username and password");
	    		  	}
	    	  }
	      }
	      else
	      {
	    	  errorText.setVisibility(View.VISIBLE);
	    	  errorText.setText("Invalid request format");
	      }
	}
	private void sendAuthPacket()
	{
		AuthPacket ap = new AuthPacket(uID,password,false,false);
	    Packet p = new Packet(PacketSequenceNos.AUTHENTICATION_SEND_CLIENT,true,false,false,Utilities.serialize(ap));
		try
		{
			byte[] packet_buf = Utilities.serialize(p);
		    DatagramPacket packet = new DatagramPacket(packet_buf, packet_buf.length, Utilities.serverIP, Utilities.authServerPort); 
		    socket.send(packet);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

