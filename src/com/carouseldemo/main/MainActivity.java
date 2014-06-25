package com.carouseldemo.main;

import com.example.peerbased.*;

import StaticAttributes.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.carouseldemo.controls.Carousel;
import com.carouseldemo.controls.CarouselAdapter;
import com.carouseldemo.controls.CarouselAdapter.OnItemClickListener;
import com.carouseldemo.controls.CarouselAdapter.OnItemSelectedListener;
import com.carouseldemo.controls.CarouselItem;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

class QuizListen extends Thread
{
	DatagramSocket sock;
	public QuizListen() {
		sock = SocketHandler.normalSocket;
		/*
		 * InitialiZe the socket timeout
		 */
		try {
			sock.setSoTimeout(1000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	public void run()
	{
		boolean rcvd = false;
		
		while( true )
		{
			System.out.println("Started listening!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			
			byte[] b = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket pack  =  new DatagramPacket(b, b.length);
			
			try {
				sock.receive(pack);
			}
			catch( SocketTimeoutException ste )
			{
				if( rcvd == true )
				{
					System.out.println("I am going to HELLLL!!");
					Intent i = new Intent(MainActivity.staticAct, Quiz.class);
					MainActivity.staticAct.startActivity(i);
					break;
				}
				else
				{
					continue;
				}
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}

			Packet p = (Packet)Utilities.deserialize(b);
			
			if( p.type == PacketTypes.QUIZ_TURN_SCREEN )
			{
				if( rcvd == false )
				{
					System.out.println("GOTCHAAA!");
					ParameterPacket pp = (ParameterPacket)Utilities.deserialize(p.data);
					QuizAttributes.noOfLeaders = pp.noOfLeaders;
					QuizAttributes.noOfOnlineStudents = pp.noOfOnlineStudents;
					QuizAttributes.noOfRounds = pp.noOfRounds;
					QuizAttributes.sizeOfGroup = pp.sizeOfGroup;
					QuizAttributes.subject = pp.subject;
					rcvd = true;
				}
				/*
				 * Send the Ack back
				 */
				p.ack = true;
				p.data = null;
				byte[] ackPackbytes = Utilities.serialize(p);
				DatagramPacket ackPack = new DatagramPacket(ackPackbytes, ackPackbytes.length, Utilities.serverIP, Utilities.servPort);
				try {
					sock.send(ackPack);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(0);
				}
				continue;
				/*
				 * Now wait for socket timeout seconds and break
				 */
			} 
			else
			{
				continue;
			}
		}
	}
}


public class MainActivity extends Activity {
	
	TextView txt;
	
	public static MainActivity staticAct;
	DatagramSocket sock;
	
	String Performance[] = {"Last Test Performance", "Overall Performance"};
	
    /** Called when the activity is first created. */
    @Override
      public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.main);
          
          sock = SocketHandler.normalSocket;
          staticAct = this;
          QuizListen q = new QuizListen();
          q.start();
          
          txt=(TextView)findViewById(R.id.item_text);
    
         
        
        final Carousel carousel = (Carousel)findViewById(R.id.carousel);
        carousel.setOnItemClickListener(new OnItemClickListener(){
        	
        	String s;
        	int t;
        	
        public void onItemClick(CarouselAdapter<?> parent, View view,int position, long id) {	
			
				  CarouselItem item = (CarouselItem)parent.getChildAt(position);

                    // HERE: Force context menu
                   carousel.showContextMenuForChild(item);

    				Toast.makeText(MainActivity.this, String.format("%s has been clicked", 
    				((CarouselItem)parent.getChildAt(position)).getName()), Toast.LENGTH_SHORT).show();
    				
    				s=((CarouselItem)parent.getChildAt(position)).getName();
    				
    				
    			   if(s.equals("questions"))
    					t=2;
    				else if(s.equals("performance"))
    					t=3;
    				else if(s.equals("files"))
    					t=4;
    				else if(s.equals("assignment"))
    					t=5;
    				else t=6;
    				
    				  Intent i;
    				
    				switch(t)
    				{
    				
    					
    				case 2:     
    					        i = new Intent(MainActivity.this,Questions.class);
    						    startActivity(i);
    						    break;
    					
    				case 3:    
    					      AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
    					      ad.setTitle("Choose the performance");
    					      ad.setSingleChoiceItems(Performance, -1, new DialogInterface.OnClickListener() 
    					      {
    						
    						     public void onClick(DialogInterface dialog, int which) 
    						     {
    							 final Intent j;
    							 Toast t = Toast.makeText(getBaseContext(), "You selected "+Performance[which], 2000);
    							 t.show();
    							 if(Performance[which].equals("Last Test Performance"))
    							 {
    								    j = new Intent(MainActivity.this,Performance1.class);
    									startActivity(j);
    							 }
    							 else if(Performance[which].equals("Overall Performance"))
    							 { 
    								    j = new Intent(MainActivity.this,Performance2.class);
    									startActivity(j);
    							 }
    							
    							dialog.dismiss();					
    						   }
    					     });
    			    
    					      ad.show();
    			              break;
    				
    				case 4:     i = new Intent(MainActivity.this,Files.class);
				                 startActivity(i);
				                 break;
    					
    				case 5:     i = new Intent(MainActivity.this,Assignment.class);
	                             startActivity(i);
	                             break;
			
    					
    				case 6:       i = new Intent(MainActivity.this,Changepwd.class);
                                  startActivity(i);
                                  break;
    				
    				}
			}
        });

            carousel.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(CarouselAdapter<?> parent, View view,
					int position, long id) {
				
		        final TextView txt = (TextView)(findViewById(R.id.selected_item));
		        
				switch(position){
				
				
				
				case 0:
					txt.setTypeface(null, Typeface.BOLD|Typeface.ITALIC);
					txt.setText("Performance");
					
					break;
				case 1:
					txt.setTypeface(null, Typeface.BOLD|Typeface.ITALIC);
					txt.setText("Assignments");
					break;
				case 2:
					txt.setTypeface(null, Typeface.BOLD|Typeface.ITALIC);
					txt.setText("Files");
					break;
				case 3:
					txt.setTypeface(null, Typeface.BOLD|Typeface.ITALIC);
					txt.setText("Questions");
					break;
				case 4:
					txt.setTypeface(null, Typeface.BOLD|Typeface.ITALIC);
					txt.setText("Change Password");
					break;
				}
				
			}

			public void onNothingSelected(CarouselAdapter<?> parent) {
			}
        	
        }
        );
        
    }
    
}

