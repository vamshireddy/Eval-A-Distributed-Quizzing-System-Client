package com.carouseldemo.main;
import StaticAttributes.*;
import QuizPackets.*;
import StaticAttributes.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import com.example.peerbased.*;

import StaticAttributes.QuizAttributes;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.MailTo;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.os.Build;

/*
 * There is a scope to clean the client buffer here!!!
 * 
 */
class WaitScreen extends Thread
{
	public void run()
	{
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Intent i = new Intent(GroupWelcome.staticVar,SimpleQuizStartPage.class);
		GroupWelcome.staticVar.startActivity(i);
	}
}


public class GroupWelcome extends Activity{

    TextView tv;
    public static GroupWelcome staticVar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_details);
        staticVar = this;
        tv = (TextView)findViewById(R.id.groupWelcometxt);
        tv.setText("Welcome to Group "+QuizAttributes.groupName+"!");
        WaitScreen qsp = new WaitScreen();
        qsp.start();
    }
}