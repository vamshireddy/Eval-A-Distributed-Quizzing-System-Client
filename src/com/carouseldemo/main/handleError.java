package com.carouseldemo.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.example.peerbased.Packet;

import StaticAttributes.Utilities;

public class handleError extends Thread{
	
	DatagramSocket sock;
	
	public handleError() {
		try {
			sock = new DatagramSocket(Utilities.clientErrorPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		byte[] bytes = new byte[Utilities.MAX_BUFFER_SIZE];
		DatagramPacket dp = new DatagramPacket(bytes, bytes.length);
		try {
			
			// Continue blocking untill the packet is received
			sock.receive(dp);
			if( bytes[0] == 0 )
			{
				System.exit(0);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
