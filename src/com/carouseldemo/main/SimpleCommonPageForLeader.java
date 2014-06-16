package com.carouseldemo.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


public class SimpleCommonPageForLeader extends Activity {

	TextView tv;
	
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.simplecommonpageforleader);
        tv = (TextView)findViewById(R.id.textViewLeaderSCP);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if( extras == null )
        {
        	/*
        	 * For leader
        	 */
        	tv.setText("Please wait leader!");
        	return;
        }
        
        
        String result = extras.getString("result");
        if( result.equals("correct"))
        {
        	/* 
        	 * When students give responses
        	 */
        	tv.setText("Your answer is correct!!");
        }
        else if( result.equals("wrong"))
        {
        	/*
        	 * When students give responses
        	 */
        	tv.setText("Your answer is wrong!!");
        }
    }
}
