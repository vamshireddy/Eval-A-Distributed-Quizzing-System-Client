package com.carouseldemo.main;
import StaticAttributes.SocketHandler;

import java.net.DatagramSocket;
import java.util.ArrayList;

import com.example.peerbased.*;

import StaticAttributes.QuizAttributes;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ListActivity;
import android.content.DialogInterface.OnClickListener;
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

public class Team_details extends ListActivity{
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
        
        teamMembers = QuizAttributes.groupMembers;
        leader = QuizAttributes.leader;
        selection=(TextView)findViewById(R.id.selection);
        error = (TextView)findViewById(R.id.errorMsg);
        selection.setText("This is you team!");
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
    }

}