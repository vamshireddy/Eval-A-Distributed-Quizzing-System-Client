package com.carouseldemo.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AnswerResultPage extends Activity{
	TextView tv;
	String result;
	public void onCreate(Bundle savedInstanceState) 
	{
	    	super.onCreate(savedInstanceState);
	        setContentView(R.layout.answer_result_page);
	        tv = (TextView)findViewById(R.id.arp_tv);
	        Intent i = getIntent();
	        result = i.getExtras().getString("result");
	        if( result.equals("correct") )
	        {
	        	tv.setText("You are right!!");
	        }
	        else if( result.equals("wrong"))
	        {
	        	tv.setText("You are wrong. Better luck next time");
	        }
	        /*
	         * Now listen for screen changing packet
	         */
	}
}
