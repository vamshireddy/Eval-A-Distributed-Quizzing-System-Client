package com.carouseldemo.main;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import com.example.peerbased.Packet;
import QuizPackets.QuestionPacket;
import StaticAttributes.*;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class Answer_multiple_choice extends Activity implements OnClickListener 
{
	Button btn;
	EditText question;
	EditText option1;
	EditText option2;
	EditText option3;
	EditText option4;
	DatagramSocket sock;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_multiple_choice);
        btn=(Button)findViewById(R.id.button1);
        question = (EditText)findViewById(R.id.question);
        option1 = (EditText)findViewById(R.id.option1);
        option2 = (EditText)findViewById(R.id.option2);
        option3 = (EditText)findViewById(R.id.option3);
        option4 = (EditText)findViewById(R.id.option4);
        sock = StaticAttributes.SocketHandler.normalSocket;
        btn.setOnClickListener(this);
    }
	public void onClick(View v)     //actions performed after change password button is clicked.
	{   
	  
		String[] options = new String[4];
		String ques = question.getText().toString();
		String answer = "";
		
		options[0] = option1.getText().toString();
		options[1] = option2.getText().toString();
		options[2] = option3.getText().toString();
		options[3] = option4.getText().toString();

		RadioGroup g = (RadioGroup) findViewById(R.id.radioGroup1);
		 
		      switch (g.getCheckedRadioButtonId())
		       {
		            case R.id.radio1 :
		            	  answer = options[0];
		                  break;
		 
		            case R.id.radio2 :
		            	 answer = options[1];
		                  break;
		
		             case R.id.radio3 :
		            	 answer = options[2];
			              break;
			
		            case R.id.radio4 :
		            	 answer = options[3];
			              break;
		        }
		/*
		 * Now send the question to teacher
		 */

		
     }
}