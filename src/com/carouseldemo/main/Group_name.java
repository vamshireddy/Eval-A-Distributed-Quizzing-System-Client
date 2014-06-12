package com.carouseldemo.main;



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.example.peerbased.GroupNameSelectionPacket;
import com.example.peerbased.LeaderPacket;
import com.example.peerbased.Packet;
import com.example.peerbased.SelectedGroupPacket;

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
		error.setVisibility(View.VISIBLE);
		/*Toast t = Toast.makeText(getBaseContext(), "Successfully Submitted...", 1000);
		t.show();
		dialog.dismiss();
		
		// Send the group name request packet to the server.*/
		
		System.out.println("CLICKED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
			sock.setSoTimeout(1000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			error.setVisibility(View.VISIBLE);
			e.printStackTrace();
		}	
		
		while( true )	
		{
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Packet packet = (Packet)Utilities.deserialize(b);
			
			error.setText(""+packet.seq_no+" "+packet.group_name_selection_packet);
			
			if( packet.seq_no == PacketSequenceNos.GROUP_REQ_SERVER_ACK && packet.group_name_selection_packet == true )
			{
				gnsp = (GroupNameSelectionPacket)Utilities.deserialize(packet.data);
				if( gnsp.accepted == true )
				{
					System.out.println("I got the reply from server!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						run();
						return;
				}
				else
				{
						error.setText("Unknown Error!");
						return;
				}
			}
			else
			{
				continue;
			}
		}
		
	}
	
	public void run()
	{
		
		while( true )
		{
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			byte[] by = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket packy = new DatagramPacket(by, by.length);
			System.out.println("");
			error.setText("WWWWWWWWWWAITTING!!!!!!!!!!!!");
			try
			{
				sock.receive(packy);
			}
			catch( SocketTimeoutException e1)
			{
				continue;
			}
			catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			error.setText("RECEIVED!!!!!!!!!!!!!");
			error.setVisibility(View.VISIBLE);
			Packet pyy = (Packet)Utilities.deserialize(by);
			
			if( pyy.team_selection_packet == true && pyy.seq_no == PacketSequenceNos.FORMED_GROUP_SERVER_SEND )
			{
				System.out.println("RECEIVED!!!!!!!!!!!!!");
				SelectedGroupPacket sgp = (SelectedGroupPacket)Utilities.deserialize(pyy.data);
				QuizAttributes.leader = sgp.leader;
				QuizAttributes.groupMembers = sgp.team;
				QuizAttributes.groupName = sgp.groupName;
				Intent i = new Intent(this,Team_details.class);
				startActivity(i);
				break;
			}
			else
			{
				continue;
			}
		}
	}
}