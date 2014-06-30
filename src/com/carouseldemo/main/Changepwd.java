package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import com.example.peerbased.AuthPacket;
import com.example.peerbased.Packet;

import StaticAttributes.PacketSequenceNos;
import StaticAttributes.PacketTypes;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Changepwd extends Activity implements OnClickListener 
{
	Button btn;
	EditText new_password,old_password,confirm_password;
	EditText uID;
	DatagramSocket sock;
	TextView error;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);
        sock = StaticAttributes.SocketHandler.authSocket;
        new_password=(EditText)findViewById(R.id.nwpwd);
        old_password=(EditText)findViewById(R.id.oldpwd); 
        error = (TextView) findViewById(R.id.errorBoxPasswordChange);
        uID = (EditText)findViewById(R.id.uIDinPassword);
        confirm_password=(EditText)findViewById(R.id.cnwpwd);
        btn=(Button)findViewById(R.id.button1);
       
        btn.setOnClickListener(this);
    }
	public void onClick(View v)     //actions performed after change password button is clicked.
	{   
		 String old_pass = old_password.getText().toString();
		 String new_pass = new_password.getText().toString();
		 String confirm_pass = confirm_password.getText().toString();
		 String userID = uID.getText().toString();
		 
		 /*
		  * Validate the entered fields
		  */
		 if( old_pass.equals("") || new_pass.equals("") || confirm_pass.equals("") || userID.equals("" ))
		 {
			 Toast.makeText(this, "One or more fields were blank", 2000).show();
			 return;
		 }
		 
		 if(new_pass.equals(confirm_pass))
	     {
			 /*
			  * Send the packet to the server
			  */
			 AuthPacket ap = new AuthPacket();
			 ap.changePass = true;
			 ap.userID = userID;
			 ap.password = old_pass;
			 ap.new_password = new_pass;
			 
			 int currentSeqNo = Utilities.seqNo;
			 
			 Packet packy = new Packet(currentSeqNo,PacketTypes.AUTHENTICATION_CHANGE_PASS, false, Utilities.serialize(ap));
			 
			 byte[] bytes = Utilities.serialize(packy);
			 
			 DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, Utilities.serverIP, Utilities.authServerPort);
			 try {
				sock.send(sendPacket);
			 } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
			 
			 /*
			  * Now wait for the reply
			  */
			  System.out.println("Waiting for the packet CHANGE+_PASS");
			  byte[] by = new byte[Utilities.MAX_BUFFER_SIZE];
			  DatagramPacket recvpacky = new DatagramPacket(by, by.length);
			  try
			  {
				  sock.receive(recvpacky);
			  }
			  catch (SocketTimeoutException e) 
			  {
				  error.setText("Try again!");
				  return;
			  } catch (IOException e) {
				// TODO Auto-generated catch block
				  e.printStackTrace();
			  }
			  
			  System.out.println("Recvd packet PASS CHANGE");
			  
			  Packet recvdPacket = (Packet)Utilities.deserialize(by);
			  
			  if(recvdPacket.seq_no == currentSeqNo && recvdPacket.type == PacketTypes.AUTHENTICATION_CHANGE_PASS && recvdPacket.ack == true )
			  {
				  AuthPacket recvdAp = (AuthPacket)Utilities.deserialize(recvdPacket.data);
				  
				  if( recvdAp.grantAccess == true )
				  {
					  Toast.makeText(this, "Password changed succesfully", 2000).show();
					  System.out.println("Granted");
					  finish();
				  }
				  else
				  {
					  Toast.makeText(this, "Invalid password", 2000).show();
					  new_password.setText("");
					  old_password.setText("");
					  confirm_password.setText("");
				  }
			  }
	     }
		 else
		 {
			 	Toast.makeText(this, "Entered passwords did not match "+new_password.getText().toString()+" "+confirm_password.getText().toString(), 1000).show();
			 	new_password.setText("");
			 	old_password.setText("");
			 	confirm_password.setText("");
         }
	}
}

