//Shawn Cramp ,Bruno Salapic
//--------------------,100574460
//April,4,2016

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MultiServer extends Thread {
	
	public static void main(String[] args) throws java.io.IOException {
		new MultiServer().start();
	}
	MulticastSocket socket = null;
	DatagramPacket recievePacket = null;
	DatagramPacket sendPacket = null;
	
	ByteArrayOutputStream baos=new ByteArrayOutputStream();
	DataOutputStream dos=new DataOutputStream(baos);

	byte[] rec = new byte[512];
	byte[] snd = new byte[512];

	@Override
	public void run() {
		try {
			InetAddress group = InetAddress.getByName("225.0.0.1");
			socket = new MulticastSocket(8080);
			socket.joinGroup(group);
			recievePacket = new DatagramPacket(rec, rec.length);
			sendPacket = new DatagramPacket(snd, snd.length,group,8085);
		} catch (Exception e){
			System.out.println(e);
		}

		while (true) {
			try {

				socket.receive(recievePacket);
				InputStream in=new ByteArrayInputStream(recievePacket.getData(), recievePacket.getOffset(),
						recievePacket.getLength());
				DataInputStream din=new DataInputStream(in);

				//Reads 3 values: an int, then a char, and then an int again
				int x = din.readInt(); 
				char c = din.readChar(); 
				int y = din.readInt(); 
				din.close();
				snd = rec;
				// send it
				socket.send(sendPacket);
				
			}catch (Exception e){
				System.out.println(e);
			}
		}
	}
}
