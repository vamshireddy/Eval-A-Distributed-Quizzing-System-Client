package com.carouseldemo.main;



import java.io.BufferedReader;
import java.io.DataOutputStream;
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
import android.content.Intent;
import android.graphics.Color;
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

//
//class questionThread extends Thread
//{
//	Displayquestions obj;
//	SparseArray<Group> groups;
//	String subject;
//	String fileName = "questions.ser";
//	
//	
//	public questionThread(Displayquestions obj, String subject)
//	{
//		this.obj = obj;
//		this.subject = subject;
//	}
//
//	
//	private Map<String,String> mergeMaps(Map<String,String> map1, Map<String,String> map2 )
//	{
//		/*
//		 * Map1 is old
//		 * Map2 is new
//		 */
//		Map<String,String> map3 = new HashMap<String, String>();
//		if( map2 != null )
//		{
//			map3.putAll(map2);
//		}
//		if( map1 != null )
//		{
//			map3.putAll(map1);
//		}
//		return map3;
//	}
//	
//	private void serialize(persistentQuestions ques)
//	{
//		 try
//	     {
//	         FileOutputStream fileOut = new FileOutputStream(fileName);
//	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
//	         out.writeObject(ques);
//	         out.close();
//	         fileOut.close();
//	         System.out.printf("Serialized data is saved in /tmp/employee.ser");
//	      }
//		  catch(IOException i)
//	      {
//	          i.printStackTrace();
//	      }
//	}
//	
//	public void run()
//	{
//		/*
//		 * Open the file and Deserialize the object.
//		 * If exception is caught, then file might be empty. So directly query without any date.
//		 * If not, Get the last fetched date and use this to send a query to the server
//		 */
//		persistentQuestions existingQuestions = deserializeExistingQuestions();
//		
//		/*
//		 * If returns NULL, then the file is created for the first time
//		 */
//		
//		Map<String,String> mergedMap = null;
//		
//		if( existingQuestions == null )
//		{
//			/*
//			 * Use the existing hashMap and the new hashmap and write it to the front end
//			 */
//			mergedMap = getHashMap(new java.util.Date(0));
//			/*
//			 * Get the hashmap by querying the server with the current date
//			 */
//		}
//		else
//		{
//			/*
//			 * Get the existing MAP object.
//			 */
//			Map<String,String> existingMap = null;
//			/*
//			 * Get the last fetched date, so that we could use it in the query to get only the questions 
//			 * formed after that time.
//			 */
//			java.util.Date lastFetchedDate = null;
//			
//			if( subject.equals("English") )
//			{
//				existingMap = existingQuestions.englishQues;
//				lastFetchedDate = existingQuestions.lastFetchedDateEng;
//			}
//			else if( subject.equals("Hindi"))
//			{
//				existingMap = existingQuestions.hindiQues;
//				lastFetchedDate = existingQuestions.lastFetchedDateHin;
//			}
//			else if( subject.equals("Maths"))
//			{
//				existingMap = existingQuestions.mathsQues;
//				lastFetchedDate = existingQuestions.lastFetchedDateMat;
//			}
//			else if( subject.equals("Science"))
//			{
//				existingMap = existingQuestions.scienceQues;
//				lastFetchedDate = existingQuestions.lastFetchedDateSci;
//			}
//			else if( subject.equals("Social"))
//			{
//				existingMap = existingQuestions.socialQues;
//				lastFetchedDate = existingQuestions.lastFetchedDateSoc;
//			}
//			/*
//			 * All subjects should be done similarly
//			 */
//			Map<String,String> map = getHashMap(lastFetchedDate);
//			/*
//			 * Use the existing hashMap and the new hashmap and write it to the front end
//			 */
//			mergedMap = mergeMaps(existingMap, map);
//		}
//		
//		groups = createData(mergedMap);
//		obj.groups = groups;
//		/*
//		 * Update the persistent HASHMAP with the old one + existing one
//		 */
//		if( existingQuestions == null )
//		{
//			persistentQuestions pq = new persistentQuestions();
//			if( subject.equals("english") )
//			{
//				pq.englishQues = mergedMap;
//				pq.lastFetchedDateEng = new java.util.Date();
//				serialize(pq);
//			}
//		}
//		obj.wait = false;
//	}
//	
//	public Map<String,String> getHashMap(java.util.Date date)
//	{
//		/*
//		 * Convert the date to SQL format
//		 */
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
//		String dateString = sdf.format(date); 
//		
//		Map<String,String> localmap = null;
//		JSONObject obj = new JSONObject();
//		obj.put("queryType","Questions");
//		obj.put("subject",subject);
//		obj.put("date", dateString);
//
//		String jsonString = obj.toJSONString();
//		
//		Socket s;
//		
//		try {
//			
//			s = new Socket(Utilities.serverIP,6711);
//			
//			DataOutputStream outToServer = new DataOutputStream(s.getOutputStream());
//			
//			outToServer.writeBytes(jsonString+"\n");
//			
//			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
//			String sentence = inFromServer.readLine();
//			
//			localmap = new HashMap<String,String>();
//			
//			
//			ObjectMapper mapper = new ObjectMapper();
//			 
//			try {
//		 
//				//convert JSON string to Map
//				localmap = mapper.readValue(sentence, new TypeReference<HashMap<String,String>>(){});
//				return localmap;
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			//System.out.println(sentence);
//			
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return localmap;
//	}
//	
//	public String[] splitJSONString(String jsonStr)
//	{
//		String[] strings = new String[3];
//		char c;
//		int strIndex = 0;
//		
//		strings[0] = new String();
//		for(int i=0;i<jsonStr.length();i++)
//		{
//			char curChar = jsonStr.charAt(i);
//			if( curChar=='$')
//			{
//				System.out.println(strings[strIndex]);
//				strIndex++;
//				strings[strIndex] = new String();
//			}
//			else
//			{
//				strings[strIndex] = strings[strIndex] + curChar;
//			}
//		}
//		return strings;
//	}
//	
//    public SparseArray<Group> createData(Map<String,String> map)
//    {
//    		SparseArray<Group> groups = new SparseArray<Group>();
//    		
//    		Set<String> set = map.keySet();
//
//    		//populate set
//    		int index = 0;
//    		for (String s : set) {
//    			
//    			String value = map.get(s);
//    			System.out.println("JSON String :  "+value);
//    			String[] values = splitJSONString(value);
//    			
//    		    Group group = new Group((index+1)+"."+s);
//    		    
//    		    group.children.add("Level : "+values[0]);
//    		    group.children.add("Date : "+values[1]);
// 	 			group.children.add("Answer: "+values[2]);
//            	groups.append(index++, group);
//    		}
//    		return groups;
//     }
//}


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
		
		if( subject.equals("english"))
		{
			hmap = storedQuestions.englishQues;
		}
		else if( subject.equals("maths"))
		{
			hmap = storedQuestions.mathsQues;
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

 

public class Displayquestions extends Activity {
	
	persistentQuestions questionsStored;
	String fileName = "questions.ser";
	SparseArray<Group> groups = new SparseArray<Group>();
	MyExpandableListAdapter adapter;
	ProgressDialog pd1;
	Handler h1;
	boolean wait = true;
	public static Displayquestions staticVar;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displayquestion);
        staticVar = this;
        
        Intent i = getIntent();
        String subject = i.getExtras().getString("subject");
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
	    pd1.setMessage("After this, a new quiz will start");
	    h1.sendEmptyMessage(0);
	    pd1.show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.questions_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.item1:
                openhome();
                return true;
            case R.id.item2:
                 openlogout();
                return true;
            case R.id.item3:
            	finish();
            	startActivity(getIntent());
               return true;
          
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    
	private void openhome() {
		// TODO Auto-generated method stub
		Intent i;
		i= new Intent(this,MainActivity.class);
		startActivity(i);
		
		
	}


	private void openlogout() {
		// TODO Auto-generated method stub
		Intent intent;
		intent= new Intent(this,Login.class);
		 intent.putExtra("finish", true); // if you are checking for this in your other Activities
		    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
		                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
		                    Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(intent);
		    finish();
		
		
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
}