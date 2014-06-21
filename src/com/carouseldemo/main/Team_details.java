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

/*
 * There is a scope to clean the client buffer here!!!
 * 
 */
class WaitScreen extends Thread
{
	public void run()
	{
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent i= new Intent(Team_details.staticVar,SimpleQuizStartPage.class);
		Team_details.staticVar.startActivity(i);
		Team_details.staticVar.finish();
	}
}


public class Team_details extends ListActivity{
	public static Team_details staticVar;
	ArrayAdapter<String> leaderAdapter;
	//ArrayList<String> selectedLeaders;
	TextView selection;
	public static TextView timer;
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
        timer = (TextView)findViewById(R.id.timer);
        timer.setText("You will be redirected to the Quiz page in a moment");
        selection.setText("This is your team \""+QuizAttributes.groupName+"\"");
        
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
        WaitScreen qsp = new WaitScreen();
        qsp.start();
    }
}