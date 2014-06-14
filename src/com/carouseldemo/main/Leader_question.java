package com.carouseldemo.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class Leader_question extends Activity  implements OnClickListener 
 {
	
	Button mcq,truefalse,fill;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leader_question);
        
        mcq=(Button)findViewById(R.id.button1);
        truefalse=(Button)findViewById(R.id.button2);   
        fill=(Button)findViewById(R.id.button3);
        
       
        truefalse.setOnClickListener(this);
        mcq.setOnClickListener(this);
        fill.setOnClickListener(this);
    }
	public void onClick(View v)     //actions performed after  buttons are clicked.
	{   
		 Intent i;
			switch (v.getId())
			{
		    case R.id.button1:
		    	Toast.makeText(this, "You clicked multiple choice.", 1000).show();
		    	 i = new Intent(this,Multiple_choice.class);
				startActivity(i);
		        break;
		    case R.id.button2:
		    	Toast.makeText(this, "You clicked true false questions.", 1000).show();
		    	 i = new Intent(this,True_false.class);
				startActivity(i);
				break;
		    case R.id.button3:
		    	Toast.makeText(this, "You clicked one word questions.", 1000).show();
		    	 i = new Intent(this,One_word.class);
				startActivity(i);
				break;
			}
	}

}
