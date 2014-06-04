package StaticAttributes;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;

public class SocketHandler {

	public static DatagramSocket authSocket;
	public static DatagramSocket normalSocket;
	static
	{
		try {
			authSocket = new DatagramSocket(null);
			authSocket.setReuseAddress(true);
			authSocket.bind(new InetSocketAddress(Utilities.authClientPort));
			authSocket.setSoTimeout(2000);
			
			normalSocket = new DatagramSocket(null);
			normalSocket.setReuseAddress(true);
			normalSocket.bind(new InetSocketAddress(Utilities.clientPort));
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
