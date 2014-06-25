package com.carouseldemo.main;



import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;



	
	

public class Perf  extends Activity
{

		private XYPlot xyPlot;
		static int i=-1;
		ProgressDialog pd1;
		Handler h1;
		public boolean wait;
		
		final String[] tests = new String[] {
	        	"test1","test2","test3"
	        };
	
		public ArrayList<Map<String, Number[]>> subjectLists;
		
	    @Override
	    public void onCreate(Bundle savedInstanceState)
	    {
	 
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.performance2);
	        wait = true;
	        
	      
	     
	       
						plotGraph();
					
	    }
	    
	    public void plotGraph()
	    {
	    	

	    	System.out.println(".............!!!!");
	    	
	        // initialize our XYPlot reference:
	        xyPlot = (XYPlot) findViewById(R.id.xyplot);
	 
	        ArrayList<XYSeries> xyList = new ArrayList<XYSeries>();
	        ArrayList<LineAndPointFormatter> lpList = new ArrayList<LineAndPointFormatter>();
	        
	        for(int i=0;i<4;i++)
	        {
	        	Number[] numList = {20+i,30+i,40+i};
	        	String subject = "Maths";
	        	
	        	XYSeries series = new SimpleXYSeries(
		                Arrays.asList(numList),          		 // array => list
		                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY , // Y_VALS_ONLY means use the element index as the x value
		                subject);                             	 // Title of this series
	        	
	        	xyList.add(series);
	        }
	        
	        
	        for(int i=0;i<4;i++)
	        {
	        	// Create a formatter to format Line and Point of income series
		        LineAndPointFormatter lf = new LineAndPointFormatter(
		                Color.rgb(0, 0, ((i+1)*20)),                   // line color
		                Color.rgb(200, 200, 200),               // point color
		                null, null );                					// fill color (none)
		        lpList.add(lf);
	        }
	        
	        
	        for(int i=0;i<4;i++)
	        {
	        	XYSeries xytemp = xyList.get(i);
	        	LineAndPointFormatter lpTemp = lpList.get(i);
		        xyPlot.addSeries(xytemp,lpTemp);
	        }
	        
	       
	        System.out.println("I AM HERE");

	        
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
	        
	        // Reduce the number of range labels
	        xyPlot.setTicksPerRangeLabel(1);
	        
	        // Reduce the number of domain labels
	        xyPlot.setTicksPerDomainLabel(1);
	        System.out.println("DEAD!!");
	    }
}
