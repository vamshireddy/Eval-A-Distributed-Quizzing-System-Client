package com.carouseldemo.main;

import com.example.peerbased.*;
import StaticAttributes.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.carouseldemo.controls.Carousel;
import com.carouseldemo.controls.CarouselAdapter;
import com.carouseldemo.controls.CarouselAdapter.OnItemClickListener;
import com.carouseldemo.controls.CarouselAdapter.OnItemSelectedListener;
import com.carouseldemo.controls.CarouselItem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

class QuizListen extends Thread
{
	DatagramSocket sock;
	public QuizListen() {
		sock = SocketHandler.normalSocket;
	}
	public void run()
	{
		while( true )
		{
			System.out.println("Started listening!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			byte[] b = new byte[Utilities.MAX_BUFFER_SIZE];
			DatagramPacket pack  =  new DatagramPacket(b, b.length);
			try {
				sock.receive(pack);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Packet p = (Packet)Utilities.deserialize(b);
			if( p.seq_no == 101010 && p.bcast == true )
			{
				ParameterPacket pp = (ParameterPacket)Utilities.deserialize(p.data);
				QuizAttributes.noOfLeaders = pp.noOfLeaders;
				QuizAttributes.noOfOnlineStudents = pp.noOfOnlineStudents;
				QuizAttributes.noOfRounds = pp.noOfRounds;
				QuizAttributes.sizeOfGroup = pp.sizeOfGroup;
				QuizAttributes.subject = pp.subject;
				Intent i = new Intent(MainActivity.staticAct, Quiz.class);
				MainActivity.staticAct.startActivity(i);
				MainActivity.staticAct.finish();
				break;
			}
		}
	}
}


public class MainActivity extends Activity {
	
	TextView txt;
	
	public static MainActivity staticAct;
	DatagramSocket sock;
	
	String Performance[] = {"Your individual Performance", "Your Overall Performance"};
	
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
    							 if(Performance[which].equals("Your individual Performance"))
    							 {
    								    j = new Intent(MainActivity.this,Performance1.class);
    									startActivity(j);
    							 }
    							else if(Performance[which].equals("Your Overall Performance"))
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
					txt.setText("PERFORMANCE");
					break;
				case 1:
					txt.setText("ASSIGNMENTS");
					break;
				case 2:
					txt.setText("FILES");
					break;
				case 3:
					txt.setText("QUESTIONS");
					break;
				case 4:
					txt.setText("CHANGE PASSWORD");
					break;
				}
				
			}

			public void onNothingSelected(CarouselAdapter<?> parent) {
			}
        	
        }
        );
        
    }
    
}

