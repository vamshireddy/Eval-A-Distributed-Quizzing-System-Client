package com.carouseldemo.main;


import java.util.ArrayList;

import com.example.peerbased.Leader;

import StaticAttributes.QuizAttributes;
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
import android.widget.Toast;

public class Select_leader extends ListActivity  implements OnClickListener {
	
	
	ArrayAdapter<String> leaderAdapter;
	//ArrayList<String> selectedLeaders;
	String subject[];
	ProgressDialog pd1;
	int counter1 = 0;
	Handler h1;
	
	
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        // Get the leaders list from the static class
        ArrayList<Leader> leaders = QuizAttributes.selectedLeaders;
        subject = new String[leaders.size()];
        
        // Iterate through the leader list and make a String array of leader names
        for(int i=0;i<subject.length;i++)
        {
        	subject[i] = new String(leaders.get(i).name);
        }
        leaderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subject);
		setListAdapter(leaderAdapter);
		
		 pd1 = new ProgressDialog(this);
	        pd1.setProgress(0);
	    
	    h1 = new Handler()
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
	    	
	    };
      
    }
 
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		
		
				
		
		pd1.setTitle("Please Wait for few moments . . . ");
		pd1.setMessage("After this you will get your team.");
		pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		h1.sendEmptyMessage(0);
		pd1.show();
		
			
		
		
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}

	










	
	
    