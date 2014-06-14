package com.carouseldemo.main;

import StaticAttributes.QuestionAttributes;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Answer_true_false extends Activity implements OnClickListener 
{
	Button btn;
	TextView question;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_true_false);
        
        question=(TextView)findViewById(R.id.textView1);
        btn=(Button)findViewById(R.id.button2);
        
        question.setText(QuestionAttributes.question);
       
        btn.setOnClickListener(this);
    }
	public void onClick(View v)     //actions performed after change password button is clicked.
	{   
	  
		String answer;
		
		RadioGroup g = (RadioGroup) findViewById(R.id.radioGroup1);
		 
		      switch (g.getCheckedRadioButtonId())
		       {
		            case R.id.true1 :
	
                          answer="true";
		                  break;
		 
		            case R.id.false1 :
			              answer="false";
		                  break;
		
		             
		        }
		
     }
}
