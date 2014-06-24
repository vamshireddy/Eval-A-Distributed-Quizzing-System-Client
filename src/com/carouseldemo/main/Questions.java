package com.carouseldemo.main;




import android.app.Activity;
import android.app.ListActivity;
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
import android.widget.TextView;
import android.widget.Toast;

public class Questions extends ListActivity 
{
	
	ArrayAdapter<String> subjects;
	String subject[] = {"Maths", "English", "Science", "Marathi", "Social","Hindi"};
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
     
        subjects = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subject);
		setListAdapter(subjects);    
    }
    protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		super.onListItemClick(l, v, position, id);
		Toast.makeText(this, "Opening "+subject[position]+"'s App", 1000).show();
		
		
			Intent i = new Intent(this,Displayquestions.class); //pass the subject as parameter in intent to retrieve th question
			i.putExtra("subject",subject[position]);
			startActivity(i);                                    
	
	}
	
}

