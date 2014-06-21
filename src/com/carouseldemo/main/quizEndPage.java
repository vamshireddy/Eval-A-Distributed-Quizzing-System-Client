package com.carouseldemo.main;

import StaticAttributes.QuizResults;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

class ResultsWaitScreen extends Thread
{
	Activity act;
	public ResultsWaitScreen(Activity act)
	{
		this.act = act;
	}
	public void run()
	{
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent i= new Intent(act,MainActivity.class);
		act.startActivity(i);
		act.finish();
	}
}

public class quizEndPage extends Activity{

	private TextView title;
	private TextView attempted;
	private TextView correct;
	private TextView marks;
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_end_page);
        
        title = (TextView)findViewById(R.id.titleView);
        attempted = (TextView)findViewById(R.id.attemptedView);
        correct = (TextView)findViewById(R.id.correctView);
        marks = (TextView)findViewById(R.id.marksView);
        if( QuizResults.marks < 30 )
        {
        	title.setText("You scored very low in this Quiz. Better luck next time.");
        }
        else
        {
        	title.setText("Congrats! Your score is...");
        }
        System.out.println("Params are "+QuizResults.noOfQuesAttempted+" "+QuizResults.noOfQuesCorrect+" "+QuizResults.marks);
        attempted.setText("Attempted Questions : "+QuizResults.noOfQuesAttempted+"");
        correct.setText("Correct answers: "+QuizResults.noOfQuesCorrect+"");
        marks.setText("Total Marks: "+QuizResults.marks+"");
        new ResultsWaitScreen(this).start();
    }
}
