package com.carouseldemo.main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import StaticAttributes.QuizAttributes;
import StaticAttributes.Utilities;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;
import android.content.*;


class fileFetcher extends Thread
{
	
	/*
	 * Inner class to display toast message from a non activity thread
	 */
	class toastDisplay implements Runnable
	{
			fileFetcher f;
			public toastDisplay(fileFetcher f) {
				this.f = f;
			}
	        public void run() {
	            Toast.makeText(obj, "Your file "+f.fileName+" has been downloaded to "+f.path, Toast.LENGTH_SHORT).show();
	        }
	}
	
	
	
	String fileName;
	String path;
	Socket s;
	Files obj;
	DataOutputStream outToServer;
	BufferedReader inFromServer;
	public fileFetcher(Files obj)
	{
		this.obj = obj;
		/*
		 * Create a new Socket for contacting server
		 */
		try {
			s = new Socket(Utilities.serverIP,6711);
			
			outToServer = new DataOutputStream(s.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			s = null;
			outToServer = null;
			inFromServer = null;
		}

	}
	public void run()
	{
		if( outToServer == null || inFromServer == null )
		{
			/*
			 * No server running
			 */
			return;
		}
		
		JSONObject jobj = new JSONObject();
		jobj.put("queryType", "Files");
		jobj.put("standard", "standard6");
		
		try {
			/*
			 * Send the query
			 */
			
			outToServer.writeBytes(jobj.toJSONString()+"\n");
			
			
			String sentence = inFromServer.readLine();
			
			HashMap<String, String> hm = getHashMapFromJSONString(sentence);
	        
	        String files[] = new String[hm.size()];
	        
	        int index = 0;
	        
	        Set<String> set = hm.keySet();
	        
	        for(String tempStr : set )
	        {
	        	files[index++] = tempStr;
	        }
	        
	        obj.downloadedFileNames = files;
	        obj.wait = false;
	        
	        /*
	         * Now wait until the user clicks the files
	         */
	        
	        while( obj.filesReady == false )
	        {
	        	try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        /*
	         * Got the clicked file names
	         */
	        fileName = obj.fileToDownload;
	        System.out.println("FILE NAME :"+fileName);
	        /*
	         * Now fetch the files and store it in the local database
	         */
	        		/*
	        		 * Download this file
	        		 */
	        		String filePath = hm.get(fileName);
	        		System.out.println("FILE PATH : "+filePath);
	        		String sub = extractSubject(fileName);
	        		/*
	        		 * Contact the server
	        		 */
	        		JSONObject fileDownObj = new JSONObject();
	        		fileDownObj.put("fileName", filePath);
	        		System.out.println("Now downloading : "+filePath);
	        		
	        		String command = fileDownObj.toJSONString();
	        		outToServer.writeBytes(fileDownObj.toJSONString()+"\n");
	        		
	        		System.out.println("Sent the file name");
	        		/*
	        		 * Get the file size from server and store it in the directory
	        		 */
	        		
	        		int filesize;
	        		
	        		String fileParamFromClient = inFromServer.readLine();
	        		
	        		
	        		HashMap<String, String> hmFileSize = getHashMapFromJSONString(fileParamFromClient);
	        		
	        		filesize = Integer.parseInt(hmFileSize.get("fileSize"));
	        		
	        		System.out.println("The file size is : "+filesize);
	        		
	        		/*
	        		 * Get the file from the server
	        		 */
	        		path = "/mnt/sdcard/Eval/"+sub+"/"+fileName;
	        	    FileOutputStream outputStream = new FileOutputStream(path);
	        	    
	        	    BufferedOutputStream buff = new BufferedOutputStream(outputStream);
	        	    
	        	    byte[] buffer = new byte[1024];
	        	    
	        	    int count;
	        	    
	        	    InputStream in = s.getInputStream();
	        	    
	        	    System.out.println("got outputstream");
	        	    
	        	    while(( count = in.read(buffer) ) > 0 )
	        	    {
	        	    	System.out.println("read "+count+" bytes");
	        	    	outputStream.write(buffer,0,count);
	        	    }
	        	    outputStream.close();
	        	    System.out.println("Written file");
	        	    /*
	        	     * Display toast
	        	     */
	        	    obj.runOnUiThread(new toastDisplay(this));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		

		
	}
	
	private HashMap<String, String> getHashMapFromJSONString(String sentence)
	{
		ObjectMapper mapper = new ObjectMapper();
		
        HashMap<String,String> hm = new HashMap<String, String>();
        
        try {

                //convert JSON string to Map
                hm = mapper.readValue(sentence, new TypeReference<HashMap<String,String>>(){});
        }
        catch (Exception e) {
                e.printStackTrace();
        }
        return hm;
	}

	public String extractSubject(String s)
	{
		String[] arr = s.split("_");
		return arr[0];
	}
}


public class Files  extends ListActivity implements android.view.View.OnClickListener
{
	
	ArrayAdapter<String> files;
    String downloadedFileNames[];
	String file_name[] = {"Hindi", "English", "Marathi", "Science", "Social", "Maths"};
	Button btn;
	ProgressDialog pd1;
	Handler h1;
	boolean wait;
	String fileToDownload;
	boolean filesReady;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files);
        
        createDirectories();
        
        btn = (Button)findViewById(R.id.button1);
        
        /*
         * Wait used for waiting until the files are displayed
         */
        wait = false;
        /*
         * filesReady makes the fetch thread wait until the user clicks the files to download
         */
        filesReady = false;
        
        files = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, file_name);
		setListAdapter(files);
		
        btn.setOnClickListener((android.view.View.OnClickListener) this);
	    
	    h1 = new Handler()
	    {

			@Override
			public void handleMessage(Message msg) 
			{
				super.handleMessage(msg);
				if( wait == false )
				{
					pd1.dismiss();
				}
				else
				{
					pd1.incrementProgressBy(1);
					h1.sendEmptyMessageDelayed(0, 200);
				}
				
			}
	    	
	    };
    }
   
	public void createDirectories()
	{
		/*
		 * Create Directories for the first time
		 */
		new File("/mnt/sdcard/EVal").mkdir();
		new File("/mnt/sdcard/Eval/Science").mkdir();
		new File("/mnt/sdcard/Eval/Maths").mkdir();
		new File("/mnt/sdcard/Eval/English").mkdir();
		new File("/mnt/sdcard/Eval/Marathi").mkdir();
		new File("/mnt/sdcard/Eval/Social").mkdir();
		new File("/mnt/sdcard/Eval/Hindi").mkdir();
	}


    protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, filesView.class);
		i.putExtra("subject", file_name[position]);
		startActivity(i);
	}
	
	public void onClick(View v) 
	{
		wait = true;
		filesReady = false;
		fileFetcher ff = new fileFetcher(this);
		ff.start();
		
        pd1 = new ProgressDialog(this);
	    pd1.setProgress(0);
	    pd1.setTitle("Please wait!");
	    pd1.setMessage("Questions are being fetched from database");
	    h1.sendEmptyMessage(0);
	    pd1.show();
	    
	    /*
	     * Wait until the files names are downloaded
	     */
	    while( wait == true )
	    {
	    	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    //get the new uploaded files from server
	
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle("Select the files to download");
		ad.setSingleChoiceItems(downloadedFileNames, -1, new OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				fileToDownload = downloadedFileNames[which];
				filesReady = true;
				dialog.dismiss();
				
			}
		});
		ad.show();
	}
}
 