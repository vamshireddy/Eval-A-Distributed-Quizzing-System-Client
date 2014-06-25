package com.carouseldemo.main;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;

import StaticAttributes.QuizAttributes;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

class LastTestPerformanceFetchThread extends Thread
{
	QuizPerformance qpObj;
	String subject;
	public LastTestPerformanceFetchThread(QuizPerformance qp, String sub)
	{
		qpObj = qp;
		subject = sub;
	}
	public void run()
	{
		JSONObject obj = new JSONObject();
		obj.put("queryType","Performance");
		obj.put("perfType","LastTest");
		obj.put("subject", subject);
		obj.put("studentID", QuizAttributes.studentID);
		/*
		 * TODO
		 * Standard should also be used
		 */

		String jsonString = obj.toJSONString();
		
		Map<String, String> hashMap = fetchHashMap(jsonString);
		
		/*
		 * Check if the hashmap is created
		 */
		if( hashMap != null )
		{
			String correct =  hashMap.get("correct");
			String marks = hashMap.get("marks");
			String quesAttempted = hashMap.get("quesAttempted");
			String date = hashMap.get("date");
			
			System.out.println("Correct:"+correct+" "+"Marks:"+marks+"Questions at:"+quesAttempted);
			
			try
			{
				int cor  = Integer.parseInt(correct);
				int quesAt = Integer.parseInt(quesAttempted);
				int wrong = quesAt - cor;
				int mark = Integer.parseInt(marks);
				qpObj.drawview = new DrawView(qpObj,cor, wrong, quesAt,subject+" Quiz on "+date+" - Marks: "+marks);
				//qpObj.setContentView(drawObj);
			}
			catch( NumberFormatException e)
			{
				System.out.println("Exception!");
				qpObj.drawview = new DrawView(qpObj,0, 0, 0, "No recent Quiz found!");
				//qpObj.setContentView(drawObj);
			}
		}
		else
		{
			qpObj.drawview = new DrawView(qpObj,0, 0, 0, "Please check your Network connection.");
		}
		qpObj.wait = false;
	}
	public Map<String, String> fetchHashMap(String jsonString)
	{
		Socket s;
		
		try {
			
			s = new Socket(Utilities.serverIP,6711);
			
			DataOutputStream outToServer = new DataOutputStream(s.getOutputStream());
			
			outToServer.writeBytes(jsonString+"\n");
			
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String sentence = inFromServer.readLine();
			
			System.out.println("JSON STRING IS : "+sentence);
			
			Map<String, String> localmap = new HashMap<String,String>();
			
			
			ObjectMapper mapper = new ObjectMapper();
			 
			try {
		 
				//convert JSON string to Map
				localmap = mapper.readValue(sentence, new TypeReference<HashMap<String,String>>(){});
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return localmap;
			//System.out.println(sentence);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}



class DrawView extends View{
	
	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	int correct;
	int wrong;
	int attempted;
	String title;
	float canvas_width;
	float canvas_height;
	public DrawView(Context context, int c, int w, int a, String title){
		super(context);
		correct = c;
		wrong = w;
		attempted = a;
		this.title = title;
	}
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		canvas_width=canvas.getWidth();
		canvas_height=canvas.getHeight();
		RectF rectf = new RectF(canvas.getWidth()/2+100,canvas.getHeight()/2-90, canvas.getWidth()/2+300, canvas.getHeight()/2+90);
		RectF roundF = new RectF(canvas.getWidth()/2-58,canvas.getHeight()-100, canvas.getWidth()/2+62, canvas.getHeight()-60);
		paint.setColor(Color.WHITE);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
		
	//Write title
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(38);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(Color.BLACK);
		canvas.drawText(title, canvas.getWidth()/2, 40, paint);
		
	//Display the results
		paint.setTypeface(Typeface.DEFAULT);
		paint.setTextSize(25);
		paint.setTextAlign(Align.RIGHT);
		paint.setColor(Color.BLACK);
		canvas.drawText("Correct Answers: "+correct, 240, canvas.getHeight()/2-40, paint);
		canvas.drawText("Wrong Answers: "+wrong, 240,canvas.getHeight()/2, paint);
		canvas.drawText("Attempted: "+attempted, 240, canvas.getHeight()/2+40, paint);
		
	//Drawing pie-graph	
		paint.setColor(Color.GREEN);
		
		
		float correctPercent = ((float)correct)/attempted*360;
		
		canvas.drawRect(260, canvas.getHeight()/2-60, 280, canvas.getHeight()/2-40, paint);
		canvas.drawArc(rectf, 0 , correctPercent , true, paint);
		
		paint.setColor(Color.RED);
		canvas.drawRect(260, canvas.getHeight()/2-20, 280, canvas.getHeight()/2, paint);
		canvas.drawArc(rectf, correctPercent, 360 - correctPercent , true, paint);
		
//		paint.setColor(Color.BLUE);
//		canvas.drawRect(260, canvas.getHeight()/2+20, 280, canvas.getHeight()/2+40, paint);
//	canvas.drawArc(rectf,correct*360/15 + wrong*360/15, 360-correct*360/15-wrong*360/15 ,  true, paint);
//		
//	//Draw Next Button
//		paint.setTypeface(Typeface.DEFAULT);
//		paint.setColor(Color.WHITE);
//		canvas.drawRoundRect(roundF,3,3, paint);
//		paint.setTextSize(15);
//		paint.setColor(Color.BLACK);
//		paint.setTextAlign(Align.RIGHT);
//		canvas.drawText("View Solutions",canvas.getWidth()/2+52, canvas.getHeight()-70, paint);
		
	}
	
}



public class QuizPerformance extends Activity {

	ProgressDialog pd1;
	Handler h1;
	DrawView drawview;
	boolean wait;
	public static QuizPerformance staticVar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		staticVar = this;
		wait = true;
        
        /*
         * Start the thread which forms the graph
         */
        
        Intent i = getIntent();
       
        
        LastTestPerformanceFetchThread thread = new LastTestPerformanceFetchThread(this, i.getExtras().getString("subject"));
        thread.start();
        
        /*
         * Spin the thing
         */
        pd1 = new ProgressDialog(this);
	    pd1.setProgress(0);
	    
	    h1 = new Handler()
	    {

			@Override
			public void handleMessage(Message msg) 
			{
				super.handleMessage(msg);
				if( wait == false )
				{
					pd1.dismiss();
					setContentView(drawview);
				}
				else
				{
					pd1.incrementProgressBy(1);
					h1.sendEmptyMessageDelayed(0, 200);
				}
				
			}
	    	
	    };
	    pd1.setTitle("Please wait!");
	    pd1.setMessage("Contacting Teacher...");
	    h1.sendEmptyMessage(0);
	    pd1.show();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.linequiz_results, menu);
		return true;
	}
	@Override
    public boolean onTouchEvent (MotionEvent event) {
		
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN : 
            	 if(event.getX()>drawview.canvas_width/2-58 && event.getX()<drawview.canvas_width/2+62 && event.getY()>drawview.canvas_height-110 && event.getY()<drawview.canvas_width-85){
                	                	
                }
                break;
        }

        return true;

    }
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();	
		finish();
	}

}