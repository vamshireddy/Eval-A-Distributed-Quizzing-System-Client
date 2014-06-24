package com.carouseldemo.main;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.ObjectOutputStream.PutField;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;

import StaticAttributes.Utilities;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.os.Build;


class persistentQuestions implements Serializable
{
	Map<String,String> englishQues;
	java.util.Date lastFetchedDateEng;
	Map<String,String> mathsQues;
	java.util.Date lastFetchedDateMat;
	Map<String,String> scienceQues;
	java.util.Date lastFetchedDateSci;
	Map<String,String> socialQues;
	java.util.Date lastFetchedDateSoc;
	Map<String,String> marathiQues;
	java.util.Date lastFetchedDateMar;
	Map<String,String> hindiQues;
	java.util.Date lastFetchedDateHin;
	
	public persistentQuestions() {
		englishQues = new HashMap<String, String>();
		marathiQues = new HashMap<String, String>();
		mathsQues = new HashMap<String, String>();
		scienceQues = new HashMap<String, String>();
		socialQues = new HashMap<String, String>();
		hindiQues = new HashMap<String, String>();
		/*
		 * Put the oldest date
		 */
		lastFetchedDateEng = new java.util.Date(0);
		lastFetchedDateMar = new java.util.Date(0);
		lastFetchedDateMat = new java.util.Date(0);
		lastFetchedDateSci = new java.util.Date(0);
		lastFetchedDateSoc = new java.util.Date(0);
		lastFetchedDateHin = new java.util.Date(0);
	}
}


class questionThread extends Thread
{
	Displayquestions obj;
	persistentQuestions questionsStored;
	
	/*
	 * Current subject hashmap and date
	 */
	Map<String, String> subjectHashMap;
	java.util.Date subjectLastFetchedDate;
	
	String subject;
	String fileName = "questions.ser";
	
	
	public questionThread(Displayquestions obj, String subject, persistentQuestions ques )
	{
		this.obj = obj;
		this.subject = subject;
		if( subject.equals("English") )
		{
			subjectHashMap = ques.englishQues;
			subjectLastFetchedDate = ques.lastFetchedDateEng;
		}
		else if( subject.equals("Hindi") )
		{
			subjectHashMap = ques.hindiQues;
			subjectLastFetchedDate = ques.lastFetchedDateHin;
		}
		else if( subject.equals("Maths") )
		{
			subjectHashMap = ques.mathsQues;
			subjectLastFetchedDate = ques.lastFetchedDateMat;
		}
		else if( subject.equals("Science") )
		{
			subjectHashMap = ques.scienceQues;
			subjectLastFetchedDate = ques.lastFetchedDateSci;
		}
		else if( subject.equals("Social") )
		{
			subjectHashMap = ques.socialQues;
			subjectLastFetchedDate = ques.lastFetchedDateSoc;
		}
		else if( subject.equals("Marathi") )
		{
			subjectHashMap = ques.marathiQues;
			subjectLastFetchedDate = ques.lastFetchedDateMar;
		}
		questionsStored = ques;
	}

	
	private Map<String,String> mergeMaps(Map<String,String> map1, Map<String,String> map2 )
	{
		/*
		 * Map1 is old
		 * Map2 is new
		 */
		Map<String,String> map3 = new HashMap<String, String>();
		if( map2 != null )
		{
			map3.putAll(map2);
		}
		if( map1 != null )
		{
			map3.putAll(map1);
		}
		return map3;
	}
	
	private void serialize(persistentQuestions ques)
	{
		 try
	     {
	         FileOutputStream fileOut = new FileOutputStream(fileName);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(ques);
	         out.close();
	         fileOut.close();
	         System.out.printf("Serialized data is saved in /tmp/employee.ser");
	      }
		  catch(IOException i)
	      {
	          i.printStackTrace();
	      }
	}
	
	public void run()
	{	
		/*
		 * Create a new JSON query packet with the last updated date and send it to the server.
		 */
		
		Map<String,String> newMap = getHashMap(subjectLastFetchedDate);
		
		/*
		 * Now merge the maps
		 */
		Map<String, String> mergedMap = mergeMaps(subjectHashMap, newMap);
		
		
		/*
		 * Display it to the front end and store update in the record
		 */
		if( subject.equals("English"))
		{
			questionsStored.englishQues = mergedMap;
			questionsStored.lastFetchedDateEng = new java.util.Date();
		}
		else if( subject.equals("Maths"))
		{
			questionsStored.mathsQues = mergedMap;
			questionsStored.lastFetchedDateMat = new java.util.Date();
		}
		else if( subject.equals("Science"))
		{
			questionsStored.scienceQues = mergedMap;
			questionsStored.lastFetchedDateSci = new java.util.Date();
		}
		else if( subject.equals("Social"))
		{
			questionsStored.socialQues = mergedMap;
			questionsStored.lastFetchedDateSoc = new java.util.Date();
		}
		else if( subject.equals("Hindi"))
		{
			questionsStored.hindiQues = mergedMap;
			questionsStored.lastFetchedDateHin = new java.util.Date();
		}
		else if( subject.equals("Marathi"))
		{
			questionsStored.marathiQues = mergedMap;
			questionsStored.lastFetchedDateMar = new java.util.Date();
		}
		obj.groups = createData(mergedMap);	
		obj.wait = false;
	}
	
	public Map<String,String> getHashMap(java.util.Date date)
	{
		/*
		 * Convert the date to SQL format
		 */
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = sdf.format(date); 
		
		Map<String,String> localmap = null;
		JSONObject obj = new JSONObject();
		obj.put("queryType","Questions");
		obj.put("subject",subject);
		obj.put("date", dateString);

		String jsonString = obj.toJSONString();
		
		Socket s;
		
		try {
			
			s = new Socket(Utilities.serverIP,6711);
			
			DataOutputStream outToServer = new DataOutputStream(s.getOutputStream());
			
			outToServer.writeBytes(jsonString+"\n");
			
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String sentence = inFromServer.readLine();
			
			localmap = new HashMap<String,String>();
			
			
			ObjectMapper mapper = new ObjectMapper();
			 
			try {
		 
				//convert JSON string to Map
				localmap = mapper.readValue(sentence, new TypeReference<HashMap<String,String>>(){});
				return localmap;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			//System.out.println(sentence);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return new HashMap<String, String>();
		}
		return localmap;
	}
	
	public String[] splitJSONString(String jsonStr)
	{
		String[] strings = new String[3];
		char c;
		int strIndex = 0;
		
		strings[0] = new String();
		for(int i=0;i<jsonStr.length();i++)
		{
			char curChar = jsonStr.charAt(i);
			if( curChar=='$')
			{
				System.out.println(strings[strIndex]);
				strIndex++;
				strings[strIndex] = new String();
			}
			else
			{
				strings[strIndex] = strings[strIndex] + curChar;
			}
		}
		return strings;
	}
	
    public SparseArray<Group> createData(Map<String,String> map)
    {
    		SparseArray<Group> groups = new SparseArray<Group>();
    		
    		Set<String> set = map.keySet();

    		//populate set
    		int index = 0;
    		for (String s : set) {
    			
    			String value = map.get(s);
    			System.out.println("JSON String :  "+value);
    			String[] values = splitJSONString(value);
    			
    		    Group group = new Group((index+1)+"."+s);
    		    
    		    group.children.add("Level : "+values[0]);
    		    group.children.add("Date : "+values[1]);
 	 			group.children.add("Answer: "+values[2]);
            	groups.append(index++, group);
    		}
    		return groups;
     }
}


class displayThread extends Thread
{
	Displayquestions obj;
	SparseArray<Group> groups;
	String subject;
	persistentQuestions storedQuestions;
	
	public displayThread(Displayquestions obj, String sub, persistentQuestions perQ) {
		
		this.obj = obj;
		storedQuestions = perQ;
		subject = sub;
	}
	
	public void run()
	{
		Map<String, String> hmap = null;
		
		System.out.println(subject);
		
		if( subject.equals("English"))
		{
			hmap = storedQuestions.englishQues;
		}
		else if( subject.equals("Maths"))
		{
			hmap = storedQuestions.mathsQues;
		}
		else if( subject.equals("Science"))
		{
			hmap = storedQuestions.scienceQues;
		}
		else if( subject.equals("Social"))
		{
			hmap = storedQuestions.socialQues;
		}
		else if( subject.equals("Hindi"))
		{
			hmap = storedQuestions.hindiQues;
		}
		else if( subject.equals("Marathi"))
		{
			hmap = storedQuestions.marathiQues;
		}
		/*
		 * If hashMap is still null
		 */
		if( hmap != null )
		{
			groups = createData(hmap);
			obj.groups = groups;
		}
		else
		{
			/*
			 * Create an Empty sparse array
			 */
			obj.groups = new SparseArray<Group>();
		}
		obj.wait = false;
	}
	
	public String[] splitJSONString(String jsonStr)
	{
		String[] strings = new String[3];
		
		int strIndex = 0;
		
		/*
		 * Initialize the 1st string
		 */
		strings[0] = new String();
		
		for(int i=0;i<jsonStr.length();i++)
		{
			char curChar = jsonStr.charAt(i);
			if( curChar=='$')
			{
				System.out.println(strings[strIndex]);
				strIndex++;
				/*
				 * Initialize the next string
				 */
				strings[strIndex] = new String();
			}
			else
			{
				strings[strIndex] = strings[strIndex] + curChar;
			}
		}
		return strings;
	}
	
    public SparseArray<Group> createData(Map<String,String> map)
    {
    		SparseArray<Group> groups = new SparseArray<Group>();
    		
    		Set<String> set = map.keySet();
    		//populate set
    		int index = 0;
    		for (String s : set) {
    			
    			String value = map.get(s);
    			System.out.println("JSON String :  "+value);
    			String[] values = splitJSONString(value);
    			
    		    Group group = new Group((index+1)+"."+s);
    		    
    		    group.children.add("Level : "+values[0]);
    		    group.children.add("Date : "+values[1]);
 	 			group.children.add("Answer: "+values[2]);
            	groups.append(index++, group);
    		}
    		return groups;
     }
}


public class Displayquestions extends Activity implements android.view.View.OnClickListener {
	
	persistentQuestions questionsStored;
	String fileName = "questions.ser";
	SparseArray<Group> groups = new SparseArray<Group>();
	MyExpandableListAdapter adapter;
	ProgressDialog pd1;
	Handler h1;
	Button but;
	boolean wait = true;
	String subject;
	public static Displayquestions staticVar;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displayquestion);
        staticVar = this;
        
        but = (Button)findViewById(R.id.refreshButton);
        but.setOnClickListener((android.view.View.OnClickListener) this);
        
        Intent i = getIntent();
        subject = i.getExtras().getString("subject");
        /*
         * Deserialize the existing questions
         */
        questionsStored = deserializeExistingQuestions();
        System.out.println("It is "+questionsStored);
        /*
         * Create a new thread for displaying the front end
         */
        if( questionsStored != null )
        {
        	System.out.println("Starting a thread with the existing object!");
        	displayThread t = new displayThread(this, subject, questionsStored);
        	t.start();
        }
        else
        {
        	wait = false;
        }
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
					ExpandableListView listView = (ExpandableListView)findViewById(R.id.listView);
					adapter = new MyExpandableListAdapter(staticVar, groups);
					listView.setAdapter(adapter);
				}
				else
				{
					pd1.incrementProgressBy(1);
					h1.sendEmptyMessageDelayed(0, 200);
				}
				
			}
	    	
	    };
	    pd1.setTitle("Please wait!");
	    pd1.setMessage("Questions are being fetched from database");
	    h1.sendEmptyMessage(0);
	    pd1.show();
    }
    
	private persistentQuestions deserializeExistingQuestions()
	{
		  try
		  {
			  	FileInputStream in = openFileInput(fileName);
			  	ObjectInputStream obin = new ObjectInputStream(in);
			  	persistentQuestions ques = (persistentQuestions)obin.readObject();
		        in.close();
		        obin.close();
		        return ques;
		  }
		  catch(FileNotFoundException i)
	      {
			  	System.out.println("file not found.. Creating a file now");
			  	/*
			  	 * Create a new empty file
			  	 */
			    try {
			    	FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
					System.out.println("File is created!");
			    	/*
			    	 * Create a new object
			    	 */
			    	persistentQuestions pques = new persistentQuestions();
			    	ObjectOutputStream out = new ObjectOutputStream(fos);
			        out.writeObject(pques);
			        out.close();
			        fos.close();
			        System.out.println("A new object is created and saved at "+fileName);
			        return null;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	      }
		  catch(ClassNotFoundException c)
	      {
	         System.out.println("class not found");
	         c.printStackTrace();
	      } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	      }
		  return null;
	}

	public void onClick(View v) {
		
		wait = true;
		
		questionThread qt = new questionThread(this, subject, questionsStored );
		qt.start();
		
		pd1.setTitle("Please wait!");
	    pd1.setMessage("Downloading questions from the server");
	    h1.sendEmptyMessage(0);
	    pd1.show();
	}
	
	public void onPause()
	{
		super.onPause();
		if(questionsStored != null)
		{
			FileOutputStream fos;
			try {
				File file = new File(fileName);
				System.out.println("File deleted : "+file.delete()+"\nQuestions:");
				Set<String> set = questionsStored.englishQues.keySet();
				for(String s: set)
				{
					System.out.println(s);
				}
				fos = openFileOutput(fileName, MODE_PRIVATE);
				ObjectOutputStream out = new ObjectOutputStream(fos);
		        out.writeObject(questionsStored);
		        out.close();
		        fos.close();
		        System.out.println("serialized the questions object");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}