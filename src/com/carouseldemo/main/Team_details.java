package com.carouseldemo.main;
import StaticAttributes.*;
import QuizPackets.*;
import StaticAttributes.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import com.example.peerbased.*;

import StaticAttributes.QuizAttributes;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ListActivity;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.MailTo;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.os.Build;


class QuizStartPacketListener extends Thread
{
	DatagramSocket sock;
	public QuizStartPacketListener() {
		sock = StaticAttributes.SocketHandler.normalSocket;
	}
	public void run()
	{
		listenQuizStartPacket();
	}
    public void listenQuizStartPacket()
    {
    	try {
			sock.setSoTimeout(1000);
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	while( true )
		{
			byte[] b = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket packyy  =  new DatagramPacket(b, b.length);
			
			try
			{
				sock.receive(packyy);
			}
			catch( SocketTimeoutException e1 )
			{
				continue;
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
			
			Packet packet = (Packet)Utilities.deserialize(b);
			
			if( packet.seq_no == PacketSequenceNos.QUIZ_INTERFACE_PACKET_SERVER_SEND && packet.quizPacket == true )
			{
				QuizInterfacePacket qip = (QuizInterfacePacket)Utilities.deserialize(packet.data);
				if( qip.activeGroupName.equals(QuizAttributes.groupName) && qip.activeGroupLeaderID.equals(QuizAttributes.studentID))
				{
					/*
					 * This student is a leader
					 */
					Intent i= new Intent(Team_details.staticVar,Leader_question.class);
					Team_details.staticVar.startActivity(i);
					break;
					
				}
				else if( qip.activeGroupName.equals(QuizAttributes.groupName) )
				{
					/*
					 * This is a non-leader student of the active group
					 */
					Intent i=new Intent(Team_details.staticVar,SimpleCommonPage.class);
					Team_details.staticVar.startActivity(i);
					break;
				}
				else
				{
					/*
					 * Other group students
					 */
					Intent i=new Intent(Team_details.staticVar,SimpleCommonPage.class);
					Team_details.staticVar.startActivity(i);
					break;
				}
			}
			else
			{
				continue;
			}
		}
    }
}

public class Team_details extends ListActivity{
	public static Team_details staticVar;
	ArrayAdapter<String> leaderAdapter;
	//ArrayList<String> selectedLeaders;
	TextView selection;
	TextView error;
	String teamNames[];
	DatagramSocket sock;
	Student leader;
	ArrayList<Student> teamMembers;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_details);
        
        staticVar = this;
        
        teamMembers = QuizAttributes.groupMembers;
        leader = QuizAttributes.leader;
        selection=(TextView)findViewById(R.id.selection);
        error = (TextView)findViewById(R.id.errorMsg);
        selection.setText("This is your team \""+QuizAttributes.groupName+"\"");
        error.setVisibility(View.INVISIBLE);
        
        teamNames = new String[teamMembers.size()+1];
        sock = SocketHandler.normalSocket;
        
        System.out.println("Team Names size : "+(teamMembers.size()+1));
        System.out.println("leader Name : "+leader.name);
        
        teamNames[0] = leader.name;
        		
        for(int i=0;i<teamMembers.size();i++)
        {
        	Student s = teamMembers.get(i);
        	teamNames[i+1] = s.name;
        }
        leaderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, teamNames);
        setListAdapter(leaderAdapter);
        
        /*
         * Listen for Quiz start packet
         */
        //Utilities.cleanBuffer(sock);
        QuizStartPacketListener qsp = new QuizStartPacketListener();
        qsp.start();
    }
}