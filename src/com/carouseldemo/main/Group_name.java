package com.carouseldemo.main;



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.example.peerbased.GroupNameSelectionPacket;
import com.example.peerbased.LeaderPacket;
import com.example.peerbased.Packet;

import StaticAttributes.PacketSequenceNos;
import StaticAttributes.QuizAttributes;
import StaticAttributes.SocketHandler;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface.OnClickListener;
import android.view.*;


public class Group_name extends Activity implements View.OnClickListener 
{
	Button btn;
	EditText name;
	DatagramSocket sock;
	TextView error;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_name);
       
        btn=(Button)findViewById(R.id.submit);
        btn.setOnClickListener(this);
        name = (EditText)findViewById(R.id.groupname);
        sock = SocketHandler.normalSocket;
        error = (TextView)findViewById(R.id.errorText);
        error.setVisibility(View.INVISIBLE);
    }
	public void onClick(View v) 
	{
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle("Your Group Name is "+name.getText().toString());
		ad.setMessage("Press OK to confirm");
		ad.setPositiveButton("OK", new OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					/*Toast t = Toast.makeText(getBaseContext(), "Successfully Submitted...", 1000);
					t.show();
					dialog.dismiss();
					
					// Send the group name request packet to the server.*/
					
					GroupNameSelectionPacket gnsp = new GroupNameSelectionPacket(name.getText().toString(), QuizAttributes.studentID, QuizAttributes.studentName);
					
					Packet p = new Packet(PacketSequenceNos.GROUP_REQ_CLIENT_SEND, false, false, false, Utilities.serialize(gnsp),false, true);
					p.group_name_selection_packet = true;
					
					byte[] bytes = Utilities.serialize(p);
					
					DatagramPacket pack = new DatagramPacket(bytes, bytes.length,Utilities.serverIP, Utilities.servPort);
					
					try {
						sock.send(pack);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						sock.setSoTimeout(3000);
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						error.setText("Request timed out!. Try again");
						error.setVisibility(View.VISIBLE);
						e.printStackTrace();
					}
					
					byte[] b = new byte[Utilities.MAX_BUFFER_SIZE];
					DatagramPacket packy  =  new DatagramPacket(b, b.length);
					try {
						sock.receive(packy);
					}
					catch( SocketTimeoutException e1 )
					{
						error.setText("Please try again!");
						error.setVisibility(View.VISIBLE);
						return;
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Packet packet = (Packet)Utilities.deserialize(b);
					if( packet.seq_no == PacketSequenceNos.GROUP_REQ_SERVER_ACK && packet.group_name_selection_packet == true )
					{
						gnsp = (GroupNameSelectionPacket)Utilities.deserialize(packet.data);
						if( gnsp.accepted == true )
						{
							Intent i = new Intent(Group_name.this,Team.class);
							startActivity(i);
						}
						else
						{
							error.setText("Unknown Error!");
							error.setVisibility(View.VISIBLE);
							return;
						}
					}
				}
			});
		
		
		ad.setNegativeButton("Cancel", new OnClickListener()
			{
				
				public void onClick(DialogInterface dialog, int which) 
				{
					Toast t = Toast.makeText(getBaseContext(), "Select the Group Name", 1000);
					t.show();
					dialog.dismiss();
					
				}
			});
		
		ad.show();
		
	}
}