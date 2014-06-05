package com.carouseldemo.main;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.content.DialogInterface.OnClickListener;
import android.view.*;


public class Group_name extends Activity implements View.OnClickListener 
{
	Button btn;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_name);
       
        btn=(Button)findViewById(R.id.submit);
        btn.setOnClickListener(this);
      
    }
	public void onClick(View v) 
	{
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle("Your Group Name is ");
		ad.setMessage("Press OK to confirm");
		ad.setPositiveButton("OK", new OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					Toast t = Toast.makeText(getBaseContext(), "Successfully Submitted...", 1000);
					t.show();
					dialog.dismiss();
					Intent i = new Intent(Group_name.this,Team.class);
					startActivity(i);
				}
			});
		
		
		ad.setNegativeButton("Cancel", new OnClickListener()
			{
				
				public void onClick(DialogInterface dialog, int which) 
				{
					Toast t = Toast.makeText(getBaseContext(), "Select the Group Name", 1000);
					t.show();
					dialog.dismiss();
					
				}
			});
		
		ad.show();
		
	}
}