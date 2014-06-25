package com.example.grouppeer;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Files  extends ListActivity implements OnClickListener
{
	
	ArrayAdapter<String> files;
	String file_name[] = {"Maths", "English", "Science", "Economics", "Social Science"};
	Button btn;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files);
        btn = (Button)findViewById(R.id.button1);
     
     
        files = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, file_name);
		setListAdapter(files);
		
          btn.setOnClickListener(this);
		
      
    }
   
	


    protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		Toast.makeText(this, "Opening "+file_name[position]+"'s App", 1000).show();
	
	
	}
	
	public void onClick(View v) 
	{
	
	    //get the new uploaded files from server
	    
	    String files_name[]={"file1","file2","file3","file4","file5"};
	    
	    AlertDialog.Builder ad = new AlertDialog.Builder(this);
				ad.setTitle("Choose a File to download");
				ad.setSingleChoiceItems(files_name, -1, new OnClickListener() 
				{
					
			       public void onClick(DialogInterface dialog, int which) 
					{
	              Toast t = Toast.makeText(getBaseContext(), "You selected "+files_name
	              [which], 2000);
                      t.show();
						//calling the download function
//						download(files_name[which]);
						
						
					dialog.dismiss();					
					}
				});
				ad.show();
				break;
			}
	    
	
	}
	
	
    
    
    
   
}