package com.carouseldemo.main;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.example.peerbased.GroupNameSelectionPacket;

import com.example.peerbased.Packet;
import com.example.peerbased.SelectedGroupPacket;

import StaticAttributes.PacketSequenceNos;
import StaticAttributes.PacketTypes;
import StaticAttributes.QuizAttributes;
import StaticAttributes.SocketHandler;
import StaticAttributes.Utilities;
import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.graphics.Color;
import android.view.*;

class GroupNameListener extends Thread
{
	Group_name grpActivity;
	DatagramSocket sock;
	public GroupNameListener(DatagramSocket sock) {
		this.sock = sock;
	}
	public void run()
	{
		grpActivity = Group_name.staticVar;
		
		boolean rcvd = false;

		while( true )
		{
			System.out.println("Listening for screen changing packet!!!!!");
			
			byte[] b = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket pack  =  new DatagramPacket(b, b.length);
			
			try {
				sock.receive(pack);
			}
			catch( SocketTimeoutException e )
			{
				if( rcvd == true )
				{
					/*
					 * Go to next activity
					 */
					Intent i = new Intent(grpActivity,GroupWelcome.class);
					grpActivity.startActivity(i);
//					grpActivity.finish();
					break;
				}
				else
				{
					continue;
				}
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
			
			/*
			 * Packet is received!
			 */
			
			Packet packetRcvd = (Packet)Utilities.deserialize(b);
			
			if( packetRcvd.type == PacketTypes.GROUP_DETAILS_MESSAGE && packetRcvd.ack == false )
			{
				if( rcvd == false )
				{
					QuizAttributes.groupName = new String(packetRcvd.data);
					rcvd = true;
				}
				/*
				 * Now send the ACK back
				 */
				packetRcvd.data = null;
				packetRcvd.ack = true;
				
				byte[] ackPackbytes = Utilities.serialize(packetRcvd);
				DatagramPacket ackPack = new DatagramPacket(ackPackbytes, ackPackbytes.length, Utilities.serverIP, Utilities.servPort);
				try {
					sock.send(ackPack);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
				/*
				 * Now wait for socket timeout seconds and break
				 */
			}
			else
			{
				continue;
			}
		}
	}
//		while( true )
//		{
//			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			byte[] by = new byte[Utilities.MAX_BUFFER_SIZE];
//			DatagramPacket packy = new DatagramPacket(by, by.length);
//			try
//			{
//				sock.receive(packy);
//			}
//			catch( SocketTimeoutException e1)
//			{
//				continue;
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//				continue;
//			}
//			Packet pyy = (Packet)Utilities.deserialize(by);
//			
//			if( pyy.team_selection_packet == true && pyy.seq_no == PacketSequenceNos.FORMED_GROUP_SERVER_SEND )
//			{
//				System.out.println("RD!!!!!!!!!!!!!");
//				
//				SelectedGroupPacket sgp = (SelectedGroupPacket)Utilities.deserialize(pyy.data);
//				if( sgp.leader == null || sgp.team == null || sgp.groupName == null )
//				{
//					return;
//				}
//				QuizAttributes.leader = sgp.leader;
//				QuizAttributes.groupMembers = sgp.team;
//				QuizAttributes.groupName = sgp.groupName;
//				Intent i = new Intent(grpActivity,Team_details.class);
//				grpActivity.startActivity(i);
//				grpActivity.finish();
//				break;
//			}
//			else
//			{
//				continue;
//			}
//		}
}

public class Group_name extends Activity implements View.OnClickListener
{
	private Button btn;
	private EditText name;
	private DatagramSocket sock;
	public TextView error;
	public static Group_name staticVar;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_name);
        staticVar = this;
        btn=(Button)findViewById(R.id.submitGrpName);
        btn.setBackgroundColor(Color.RED);
        btn.setOnClickListener(this);
        name = (EditText)findViewById(R.id.groupname);
        sock = SocketHandler.normalSocket;
        error = (TextView)findViewById(R.id.errorText);
		error.setText("");
		
    }
	public void onClick(View v) 
	{
		/*
		 * Make the button disabled
		 */
		btn.setEnabled(false);
		error.setText("Please wait. Dont press it again!");
		/*Toast t = Toast.makeText(getBaseContext(), "Successfully Submitted...", 1000);
		t.show();
		dialog.dismiss();
		
		// Send the group name request packet to the server.*/
		
		System.out.println("CLICKED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		int currentSeqNo = Utilities.seqNo++;
		
		GroupNameSelectionPacket gnsp = new GroupNameSelectionPacket(name.getText().toString(), QuizAttributes.studentID, QuizAttributes.studentName);
		
		Packet p = new Packet(currentSeqNo, PacketTypes.GROUP_NAME_SELECTION, false, Utilities.serialize(gnsp));
		
		byte[] bytes = Utilities.serialize(p);
		
		DatagramPacket pack = new DatagramPacket(bytes, bytes.length,Utilities.serverIP, Utilities.servPort);
		
		/*
		 * Send requests
		 */
		
		try {
			sock.send(pack);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		/*
		 * Receive Requests
		 */
		
		while( true )	
		{
			byte[] b = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket packy  =  new DatagramPacket(b, b.length);
			try
			{
					sock.receive(packy);
			}
			catch( SocketTimeoutException e1 )
			{
				error.setText("Please try again!");
				error.setVisibility(View.VISIBLE);
				/*
				 * Enable the button
				 */
				btn.setEnabled(true);
				return;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Packet packet = (Packet)Utilities.deserialize(b);
			
			if( packet.ack == true && packet.type == PacketTypes.GROUP_NAME_SELECTION )
			{
				gnsp = (GroupNameSelectionPacket)Utilities.deserialize(packet.data);
				if( gnsp.accepted == true )
				{
					    error.setText("Thanks.Please wait.");
					    btn.setBackgroundColor(Color.BLUE);
					    /* 
					     * Now listen for screen change packet
					     */
					    new GroupNameListener(sock).start();
					    /*
					     * Request is received by server. No need of button now
					     */
					    btn.setEnabled(false);
						return;
				}
				else
				{
						error.setText("There is a problem with your groupName!");
						/* EXIT POINT
						 * Enable the button
						 */
						btn.setEnabled(true);
						return;
				}
			}
			else
			{
				continue;
			}
		}
		
	}
	
	
}