package com.carouseldemo.main;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;

import StaticAttributes.QuizAttributes;
import StaticAttributes.Utilities;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;



class performanceFetchThread extends Thread
{
	Performance2 perfObj;
	public performanceFetchThread(Performance2 obj) {
		perfObj = obj;
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
			}
			return localmap;
			//System.out.println(sentence);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new HashMap<String, String>();
		} catch (IOException e) {
			e.printStackTrace();
			return new HashMap<String, String>();
		}
	}
	
	public ArrayList<Map<String, Number[]>> fetchArrayList(Map<String, String> hmap)
	{
		Set<String> set = hmap.keySet();
		
		ArrayList<Map<String, Number[]>> lists = new ArrayList<Map<String,Number[]>>();
		
		for( String sub : set)
		{
			String valueString = hmap.get(sub);
			
			ArrayList<Number> numbers = new ArrayList<Number>();
			
			String tempStr = "";
			for(int i=0;i<valueString.length();i++)
			{
				char c = valueString.charAt(i);
				if( c == '$' )
				{
					numbers.add(Integer.parseInt(tempStr));
					tempStr = "";
				}
				else
				{
					tempStr = tempStr + c;
				}
			}
			
			Number[] staticNumbers = new Number[numbers.size()];
			
			for(int i=0;i<numbers.size();i++)
			{
				staticNumbers[i] = numbers.get(i);
			}
			
			HashMap<String, Number[]> finalMap = new HashMap<String, Number[]>();
			finalMap.put(sub,staticNumbers);
			
			lists.add(finalMap);
		}	
		return lists;
	}
	
	public void run()
	{
		
		JSONObject obj = new JSONObject();
		obj.put("queryType","Performance");
		obj.put("perfType","Overall");
		obj.put("studentID", QuizAttributes.studentID);

		String jsonString = obj.toJSONString();
		
		Map<String, String> hashMap = fetchHashMap(jsonString);

		perfObj.subjectLists = fetchArrayList(hashMap);
		perfObj.wait = false;
	}
}


public class Performance2  extends Activity
{

		private XYPlot xyPlot;
		static int i=-1;
		ProgressDialog pd1;
		Handler h1;
		public boolean wait;
		
		String[] tests;
		
		/*
		 * The following variable will be set by the thread.
		 */
		public ArrayList<Map<String, Number[]>> subjectLists;
		
	    @Override
	    public void onCreate(Bundle savedInstanceState)
	    {
	 
	        super.onCreate(savedInstanceState);
	        wait = true;
	        /*
	         * Start the thread which forms the graph
	         */
	        
	        performanceFetchThread thread = new performanceFetchThread(this);
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
						setContentView(R.layout.performance2);
						plotGraph();
						pd1.dismiss();
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
	    
	    public void plotGraph()
	    {
	    	System.out.println(".............!!!!");
	    	
	        // initialize our XYPlot reference:
	        xyPlot = (XYPlot) findViewById(R.id.xyplot);
	 
	        // Converting the above income array into XYSeries
	        
	        
	        ArrayList<Number[]> tempList = new ArrayList<Number[]>();
	        ArrayList<String> subjecStrings = new ArrayList<String>();
	        
	        int maxSize = 0;
	        
	        for(int i=0;i<subjectLists.size();i++)
	        {
	        	Map<String, Number[]> map = subjectLists.get(i);
	        	/*
	        	 * Obtain the subject name
	        	 */
	        	Set<String> set = map.keySet();
	        	
	        	String subject = null;
	        	
	        	for( String s: set )
	        	{
	        		subject = s;
	        	}
	        	System.out.println("SUBJECT IS : "+subject);
	        	/*
	        	 * Get the marks list
	        	 */
	        	
	        	Number[] marksList = map.get(subject);
	        	
	        	/*
	        	 * Find the max size
	        	 */
	        	if( marksList.length > maxSize )
	        	{
	        		maxSize = marksList.length;
	        	}
	        	
	        	System.out.println("MARKS LIST IS : "+marksList);
	        	
	        	for(int j=0;j<marksList.length;j++)
		        {
		        	System.out.println(marksList[j]);
		        }
	        	System.out.println("------------------------------------");
	        	tempList.add(marksList);
	        	subjecStrings.add(subject);
	        }
	        
	        tests = new String[maxSize+2];
	        
	        for(int i=0;i<maxSize+2;i++)
	        {
	        	tests[i] = "Test"+(i+1);
	        }
	        
	        ArrayList<XYSeries> xyList = new ArrayList<XYSeries>();
	        ArrayList<LineAndPointFormatter> lpList = new ArrayList<LineAndPointFormatter>();
	        
	        for(int i=0;i<tempList.size();i++)
	        {
	        	Number[] numList = tempList.get(i);
	        	String subject = subjecStrings.get(i);
	        	
	        	XYSeries series = new SimpleXYSeries(
		                Arrays.asList(numList),          		 // array => list
		                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
		                subject);                             	 // Title of this series
	        	xyList.add(series);
	        }
	        
	        
	        for(int i=0;i<tempList.size();i++)
	        {
	        	// Create a formatter to format Line and Point of income series
	        	int r = 0 + (int)(Math.random() * ((255 - 0) + 1));
	        	int g = 0 + (int)(Math.random() * ((255 - 0) + 1));
	        	int b = 0 + (int)(Math.random() * ((255 - 0) + 1));
	        	
		        LineAndPointFormatter lf = new LineAndPointFormatter(
		                Color.rgb(r,g,b),                   // line color
		                Color.rgb(200, 200, 200),               // point color
		                null, null );                					// fill color (none)
		        lpList.add(lf);
	        }
	        
	        
	        for(int i=0;i<tempList.size();i++)
	        {
	        	XYSeries xytemp = xyList.get(i);
	        	LineAndPointFormatter lpTemp = lpList.get(i);
		        xyPlot.addSeries(xytemp,lpTemp);
	        }
	        
	       
	        System.out.println("I AM HERE");
//	     // Converting the above expense array into XYSeries
//	        XYSeries expenseSeries = new SimpleXYSeries(	
//	        		Arrays.asList(science), 				// array => list
//	        		SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
//	        		"Expense");								// Title of this series
//	 
//	        
//	        
//	        
//	        // Create a formatter to format Line and Point of expense series
//	        LineAndPointFormatter expenseFormat = new LineAndPointFormatter(
//	                Color.rgb(255, 0, 0),                   // line color
//	                Color.rgb(200, 200, 200),               // point color
//	                null, null);					                // fill color (none)
//	 
//	        
//	        
//
//	        
//	        // add income series to the xyplot:
//	        xyPlot.addSeries(incomeSeries, incomeFormat);
	        
	        
	        // Formatting the Domain Values ( X-Axis )
	        xyPlot.setDomainValueFormat(new Format() {
	 
				@Override
	            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
					System.out.println("DEAD!!");
					return new StringBuffer( tests[ ( (Number)obj).intValue() ]  );		
					
	                //return new StringBuffer( mMonths[ i]  );	
	              
	            }
	 
	            @Override
	            public Object parseObject(String source, ParsePosition pos) {
	                return null; 
	            }
	        });        
	       
	        xyPlot.setDomainLabel("");
	        xyPlot.setRangeLabel("Marks");
	        
	        // Increment X-Axis by 1 value
	        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
	        
	        xyPlot.getGraphWidget().setRangeLabelWidth(50);               
	        
	        xyPlot.getLegendWidget().setTableModel(new DynamicTableModel(3, 3));
	        
	        // adjust the legend size so there is enough room
	        // to draw the new legend grid:
	        xyPlot.getLegendWidget().setSize(new SizeMetrics(100, SizeLayoutType.ABSOLUTE, 200, SizeLayoutType.ABSOLUTE));
	 
	        // add a semi-transparent black background to the legend
	        // so it's easier to see overlaid on top of our plot:
	        Paint bgPaint = new Paint();
	        bgPaint.setColor(Color.BLACK);
	        bgPaint.setStyle(Paint.Style.FILL);
	        bgPaint.setAlpha(140);
	        xyPlot.getLegendWidget().setBackgroundPaint(bgPaint);
	 
	        // adjust the padding of the legend widget to look a little nicer:
	        xyPlot.getLegendWidget().setPadding(10, 1, 1, 1);      
	 
	        // reposition the grid so that it rests above the bottom-left
	        // edge of the graph widget:
	       /* xyPlot.position(
	                xyPlot.getLegendWidget(),
	                20,
	                XLayoutStyle.ABSOLUTE_FROM_RIGHT,
	                35,
	                YLayoutStyle.ABSOLUTE_FROM_BOTTOM,
	                AnchorPosition.RIGHT_BOTTOM);*/
	        
	        // Reduce the number of range labels
	        xyPlot.setTicksPerRangeLabel(1);
	        
	        // Reduce the number of domain labels
	        xyPlot.setTicksPerDomainLabel(1);
	        System.out.println("DEAD!!");
	    }
}
