package com.carouseldemo.main;

import java.io.File;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class filesView extends ListActivity{
	
	ArrayAdapter<String> fileAdapter;
	private String[] files;
	private String subject;

	  public void onCreate(Bundle savedInstanceState) 
	  {
		  super.onCreate(savedInstanceState);
		  Intent i = getIntent();
		  subject = i.getExtras().getString("subject");
		  /*
		   * Populate the files
		   */
		  File f = new File("/mnt/sdcard/Eval/"+subject);
		  System.out.println("I AM LIST : "+f.getAbsolutePath());
		  files = f.list();
		  System.out.println(files);
		  fileAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, files);
		  setListAdapter(fileAdapter);
	  }
}
