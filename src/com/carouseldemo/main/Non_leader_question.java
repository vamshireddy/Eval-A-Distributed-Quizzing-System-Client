package com.carouseldemo.main;



import java.net.DatagramSocket;
import java.util.logging.SocketHandler;

import StaticAttributes.Utilities;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;



public class Non_leader_question extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BrushView view = new BrushView(this);
        setContentView(view);
        /*
         * Non-active group memeber should listen for the Question packet
         * active group members should reject this
         */
        
    }
	@Override
    protected void onPause() {
        super.onPause();
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.scratchpad_activity_main, menu);
		return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.back:
			finish();
			break;
		case R.id.reset:
			com.carouseldemo.main.BrushView.reset_flag=true;
			BrushView view=new BrushView(this);
	        setContentView(view);
			break;
		
		}
			return true;
	}
}

