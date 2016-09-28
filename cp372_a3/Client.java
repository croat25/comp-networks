//Shawn Cramp ,Bruno Salapic
//--------------------,100574460
//April,4,2016
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JTextField;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class Client extends JFrame {
	//JTextfield were port and ip are to be input
	private JTextField ip= new JTextField(25);
	private JTextField port = new JTextField(25);
	
	//Dimensions of initial JFrame
	public static final int HEIGHT = 100;
	public static final int WIDTH = 500;
	
	public Client() {
		//Initial window on client side
		super();
		this.setResizable(false);
		setSize(WIDTH,HEIGHT);
		setTitle("A3 Shawn Cramp & Bruno Salapic");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//Use Flow Layout
		setLayout(new FlowLayout(10,10,10));
		setBackground(Color.LIGHT_GRAY);
		
		//Buttons
		final JButton connect = new JButton("Connect");
		
		//Labels
		final JLabel iplabel = new JLabel(" Ip: ");
		final JLabel portLabel = new JLabel("Port number: ");
		
		//Set text field for user
		ip.setText("225.0.0.1");
		port.setText("8080");
		
		//Add components to super class
		add(iplabel);
		add(ip);
		add(connect);
		add(portLabel);
		add(port);
		//Action Listener  for Connect Button
		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				
				//Create new JFrame for the White Board
				final JFrame display = new JFrame("Whiteboard frame");
				final Whiteboard draw = new Whiteboard(ip.getText(), Integer.parseInt(port.getText()));
				
				//The Window settings
				display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				display.getContentPane().add(draw);
				display.pack();
				display.setResizable(false);
				display.setVisible(true);
				display.setSize(640, 640);
				
				}
		});
		}
	//White board Class withJPanel
	private class Whiteboard extends JPanel 
	{
		//Sockets
		MulticastSocket rSocket = null;
		DatagramPacket sendPacket = null;
		DatagramPacket recievePacket = null;
		
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		//Size
		byte[] rec = new byte[512];
		byte[] snd = new byte[512];
		//Random Color is assigned for user
		int B= (int)(Math.random()*256);
		int G = (int)(Math.random()*256);
		int R = (int)(Math.random()*256);
		
		Color yC = new Color(R, G, B);
		Color c = yC;
		//Panels
		JPanel panel;
		//Buttons
		JButton btn1;
		JButton disconnect = new JButton("Disconnect");
		//Constants for dimensions of pen
		int width = 5;
		int length = 5;
		int delay = 5; 
		
		public Whiteboard(final String server, final int port)
		{

			try {
				//Address here
				InetAddress address = InetAddress.getByName(server);
				rSocket = new MulticastSocket(port);
				rSocket.joinGroup(address);
				//Exceptions
			} catch (Exception e){
				System.out.println(e);
			}
			//Default Settings for new window
			setBackground(Color.white);
			setLayout(new BorderLayout());
			panel = new JPanel();
			panel.setLayout(new GridLayout(2,4));
			
			//Buttons
			btn1 = new JButton ("Colour");
			btn1.setBackground(yC);
			
			//Add everything to new panel
			panel.add(btn1);
			panel.add(disconnect);
			
			//location of buttons
			add(panel,"North");
			
			//Action listener
			ActionListener taskPerformer = new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					//Receive and receive length
					DatagramPacket packet = new DatagramPacket(rec, rec.length);

					try {
						rSocket.setSoTimeout(5);
						rSocket.receive(packet);
						ByteArrayInputStream in=new ByteArrayInputStream(packet.getData(), packet.getOffset(),
								packet.getLength());
						DataInputStream din=new DataInputStream(in);
						//Reads 3 values
						// x-point of pen
						int x = din.readInt(); 
						//Reads away the tab character
						char c = din.readChar(); 
						// y-point of pen
						int y = din.readInt(); 
						//Set colour and oval size
						final Graphics g = getGraphics();
						g.setColor(yC);
						g.fillOval(x, y, width, length);
						din.close();
						//Exceptions
					} catch(SocketTimeoutException e) {

					} catch (SocketException e) {

					} catch (IOException e) {

					}
				}
			};
			new Timer(delay, taskPerformer).start();
			// Add Mouse Listener to Panel
			addMouseMotionListener(new MouseMotionListener() {

				@Override
				public void mouseMoved(MouseEvent evt) {
					
}
				@Override
				public void mouseDragged(MouseEvent evt) {
					
					//x and y point from retrieving point
					final Point p = evt.getPoint();
					final int mx=(int) p.getX();
					final int my=(int) p.getY();
					//Set colour and shape
					final Graphics g = getGraphics();
					g.setColor(c);
					g.fillOval(mx, my, width, length);
					//Throw a try block and catch handler for points in transfer
					try{
						//Open Streams
						ByteArrayOutputStream baos=new ByteArrayOutputStream();
						DataOutputStream dos=new DataOutputStream(baos);
						//Transfer points
						dos.writeInt(p.x);
						dos.writeChar('\t');//the tab character is used as a separator
						dos.writeInt(p.y);
						dos.close();
						byte[]data=baos.toByteArray();
						DatagramPacket packet=new DatagramPacket(data,data.length, InetAddress.getByName(server), port);
						
						rSocket.send(packet);
						//Close Streams
						baos.close();
						dos.close();
					}catch (Exception e){
						System.out.println(e);
					}
				}
			});
			//Action listener for dissconnect button
			disconnect.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					//Close curret window
					dispose();
				}
			});
		} 
	}
	//Main call
	public static void main(final String[] args) throws IOException {
		Client g = new Client();
		g.setVisible(true);
	}	
}