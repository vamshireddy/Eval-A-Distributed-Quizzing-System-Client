package com.carouseldemo.main;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import com.example.peerbased.GroupNameSelectionPacket;
import com.example.peerbased.Leader;
import com.example.peerbased.LeaderPacket;
import com.example.peerbased.Packet;
import com.example.peerbased.TeamSelectPacket;

import StaticAttributes.PacketSequenceNos;
import StaticAttributes.QuizAttributes;
import StaticAttributes.SocketHandler;
import StaticAttributes.Utilities;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Select_leader extends ListActivity  implements OnClickListener {
	
	
	ArrayAdapter<String> leaderAdapter;
	ArrayList<Leader> leaders;
	//ArrayList<String> selectedLeaders;
	TextView selection;
	TextView error;
	String subject[];
	DatagramSocket sock;
	ProgressDialog pd1;
	int counter1 = 0;
	Handler h1;
	
	
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectleader);
        // Get the leaders list from the static class
        leaders = QuizAttributes.selectedLeaders;
        selection=(TextView)findViewById(R.id.selection);
        error = (TextView)findViewById(R.id.errorMsg);
  		selection.setText("Select your Team");
  		error.setVisibility(View.INVISIBLE);
  		
        subject = new String[leaders.size()];
        sock = SocketHandler.normalSocket;
        // Iterate through the leader list and make a String array of leader names
        for(int i=0;i<subject.length;i++)
        {
        	subject[i] = new String(leaders.get(i).name);
        }
        leaderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subject);
		setListAdapter(leaderAdapter);
		
		pd1 = new ProgressDialog(this);
	    pd1.setProgress(0);
	    
	    /*h1 = new Handler()
	    {

			@Override
			public void handleMessage(Message msg) 
			{
				super.handleMessage(msg);
				if(counter1>10)
				{
					pd1.dismiss();
					Intent x = new Intent(getApplicationContext(), Team.class);
					startActivity(x);
				}
				else
				{
					counter1++;
					pd1.incrementProgressBy(1);
					h1.sendEmptyMessageDelayed(0, 200);
				}
				
			}
	    	
	    };*/
      
    }
 
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		String selectedLeaderName =(String) l.getItemAtPosition(position);
		
		String selectedLeaderID = null;
		
		for(int i=0;i<leaders.size();i++)
		{
			Leader lead = leaders.get(i);
			if( lead.name.equals(selectedLeaderName) )
			{
				selectedLeaderID = lead.id;
			}
		}
		
		TeamSelectPacket tsp = new TeamSelectPacket(selectedLeaderID, QuizAttributes.studentName, QuizAttributes.studentID);
		
		Packet pack = new Packet(PacketSequenceNos.TEAM_REQ_CLIENT_SEND, false, false, false, Utilities.serialize(tsp), false, true);
		pack.team_selection_packet = true;
		
		byte[] bytes = Utilities.serialize(pack);
		
		DatagramPacket dp = new DatagramPacket(bytes, bytes.length,Utilities.serverIP, Utilities.servPort);
		
		try {
			sock.send(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			sock.setSoTimeout(3000);
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		byte[] b = new byte[Utilities.MAX_BUFFER_SIZE];
		DatagramPacket packy  =  new DatagramPacket(b, b.length);
		
		try
		{
			sock.receive(packy);
		}
		catch( SocketTimeoutException e1 )
		{
			return;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		
		Packet packet = (Packet)Utilities.deserialize(b);
		if( packet.seq_no == PacketSequenceNos.TEAM_REQ_SERVER_ACK && packet.team_selection_packet == true )
		{
			TeamSelectPacket tspReply = (TeamSelectPacket)Utilities.deserialize(packet.data);
			if( tspReply.accepted == true )
			{
				Intent i = new Intent(getApplicationContext(),Team.class);
				startActivity(i);
			}
			else if( tsp.accepted == false )
			{
				error.setText("The Selected group is full, Please try another one.");
				error.setVisibility(View.VISIBLE);
				return;
			}
		}
		
		/*pd1.setTitle("Please Wait for few moments . . . ");
		pd1.setMessage("After this you will get your team.");
		pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		h1.sendEmptyMessage(0);
		pd1.show();*/	
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}

	










	
	
    