package com.carouseldemo.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Changepwd extends Activity implements OnClickListener 
{
	Button btn;
	EditText new_password,old_password,confirm_password;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);
        
        new_password=(EditText)findViewById(R.id.nwpwd);
        old_password=(EditText)findViewById(R.id.oldpwd);   
        confirm_password=(EditText)findViewById(R.id.cnwpwd);
        btn=(Button)findViewById(R.id.button1);
       
        btn.setOnClickListener(this);
    }
	public void onClick(View v)     //actions performed after change password button is clicked.
	{   
		 if(confirm_password.equals(new_password))
	        {
			    Toast.makeText(this, "Password Changed", 1000).show();
				Intent i = new Intent(this,MainActivity.class);     //directing to the student options page.
				startActivity(i);
	        }
		 else
		    {
			   //actions performed if new_password and confirm_password does not match.
		   
            }
		
		}
}

