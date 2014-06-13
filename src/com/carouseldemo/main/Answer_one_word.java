package com.carouseldemo.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Answer_one_word extends Activity implements OnClickListener 
{
	Button btn;
	TextView question;
	EditText answer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_one_word);
        
        question=(TextView)findViewById(R.id.textView1);
        btn=(Button)findViewById(R.id.button2);
        answer=(EditText)findViewById(R.id.answer);
        
       
        btn.setOnClickListener(this);
    }
	public void onClick(View v)     //actions performed after change password button is clicked.
	{   
	  
		String answer;
		
		
     }
}
