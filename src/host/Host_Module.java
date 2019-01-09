package host;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

public class Host_Module {
	
	private final int GAME_W, GAME_H, GAME_T;
	
	private Point point;
	
	private Snake 				 snake1, snake2;
	private ServerSocket 		 ss1,    ss2;
	private Socket 				 s1,     s2;
	private BufferedInputStream  in1,    in2;
	private BufferedOutputStream out1,   out2;
	
	private Timer t = new Timer();
	private TimerTask ttask = new TimerTask() {
		@Override
		public void run() {
			try {
				out1.write(0); out1.flush();
				out2.write(0); out2.flush();
				
				returnLocation();
				gameCheck();
				sendLocation();
				
			} catch (Exception e) {JOptionPane.showMessageDialog(null, "Hostside Game Error"); disconnect();}
		}
	};
	
	//CONSTRUCTORS//////////////////////////////////////////////////////////////////////////////////////
	public Host_Module() throws Exception {
			
		GAME_W = Integer.parseInt(JOptionPane.showInputDialog("Game Width" , "51"  ));
		GAME_H = Integer.parseInt(JOptionPane.showInputDialog("Game Height", "51"  ));
		GAME_T = Integer.parseInt(JOptionPane.showInputDialog("Game Time"  , "1000"));
		int n1 = Integer.parseInt(JOptionPane.showInputDialog("Socket 1"   , "6000"));
		int n2 = Integer.parseInt(JOptionPane.showInputDialog("Socket 2"   , "6001"));
		
		point  = new Point(GAME_W, GAME_H);
		
		
		
		snake1 = new Snake(GAME_W, GAME_H);
		ss1 = new ServerSocket(n1, 1, InetAddress.getLocalHost());
		s1 = ss1.accept();
		in1  = new BufferedInputStream (s1.getInputStream ());
		out1 = new BufferedOutputStream(s1.getOutputStream());
		
		snake2 = new Snake(GAME_W, GAME_H);
		ss2 = new ServerSocket(n2, 1, InetAddress.getLocalHost());
		s2 = ss2.accept();
		in2  = new BufferedInputStream (s2.getInputStream ());
		out2 = new BufferedOutputStream(s2.getOutputStream());
		
		
		out1.write(new byte[] {(byte) GAME_W, 
	  			  			   (byte) GAME_H,
	  			  			   (byte) point.width, 
	  			  			   (byte) point.height});
		out2.write(new byte[] {(byte) GAME_W, 
				  			   (byte) GAME_H,
				  			   (byte) point.width, 
				  			   (byte) point.height});
		out1.flush();
		out2.flush();

		t.schedule(ttask, 3000, GAME_T);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//INTERNET LOCATION METHODS/////////////////////////////////////////////////////////////////////////
	private void sendLocation() throws Exception {
		
		out1.write(new byte[] {(byte) snake1.get(0).width,
							   (byte) snake1.get(0).height,
							   (byte) snake2.get(0).width,
				   			   (byte) snake2.get(0).height,
				   			   (byte) snake1.size(),
				   			   (byte) snake2.size(),
							   (byte) point.width,
							   (byte) point.height});
		out2.write(new byte[] {(byte) snake2.get(0).width,
				   			   (byte) snake2.get(0).height,
				   			   (byte) snake1.get(0).width,
							   (byte) snake1.get(0).height,
							   (byte) snake2.size(),
							   (byte) snake1.size(),
				   			   (byte) point.width,
				   			   (byte) point.height});
		out1.flush();
		out2.flush();
	}
	
	private void returnLocation() throws Exception {
			
		if (snake1.setDir(in1.read())==-1||snake2.setDir(in2.read())==-1) disconnect();
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//END GAME METHODS//////////////////////////////////////////////////////////////////////////////////
	private void gameCheck() throws Exception {
		
		if (snake1.move(point) || snake2.move(point)) point.newPoint(snake1, snake2);
		
		endGame(snake1.check(snake2), snake2.check(snake1));
	}
	
	private void endGame(boolean lose1, boolean lose2) throws Exception {
		
		if (lose1 || lose2) {
		
			out1.write(new byte[] {0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, (byte) 255, (byte)(lose1?0:1)});
			out2.write(new byte[] {0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, (byte) 255, (byte)(lose2?0:1)});
		
			out1.flush();
			out2.flush();
		
			if (in1.read() == -1) disconnect();
			
			in2.read();
			
			snake1 = new Snake(GAME_W, GAME_H);
			snake2 = new Snake(GAME_W, GAME_H);
			point  = new Point(GAME_W, GAME_H);
		}
	}
	
	private void disconnect() {
		try {
			ss1.close();
			ss2.close();
		} catch (Exception e) {JOptionPane.showInputDialog("Server may not have closed properly");}
		
		System.exit(0);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//MAIN METHOD///////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		try {new Host_Module();}
		catch(Exception e) {}
	}
}