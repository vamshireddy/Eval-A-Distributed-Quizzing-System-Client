package com.carouseldemo.main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ActiveTeamAnsWait extends Activity{
	  
	TextView tv;
	public void onCreate(Bundle savedInstanceState) 
	{
	    	super.onCreate(savedInstanceState);
	        setContentView(R.layout.active_team_ans_wait);
	        tv = (TextView)findViewById(R.id.ataw_tv);
	        tv.setText("Please wait untill other's answer your question");
	        /*
	         * Wait for screen changing packet
	         * Quiz packet
	         * and direct to the leader, team , non team pages accordingly
	         */
	}
}
