import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class Host_Module {
	
	private final int GAME_W, GAME_H, GAME_T;
	
	private BufferedReader cmd;
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
				
			} catch (Exception e) {System.out.println("Hostside Game Error"); disconnect();}
		}
	};
	
	//CONSTRUCTORS//////////////////////////////////////////////////////////////////////////////////////
	public Host_Module(int a, int b) throws Exception {
		
		//cmd = new BufferedReader(new InputStreamReader (System.in));
			
		System.out.println("Game Width");
		GAME_W = 255;//Integer.parseInt(cmd.readLine());
			
		System.out.println("Game Height");
		GAME_H = 255;//Integer.parseInt(cmd.readLine());
			
		System.out.println("Game Timer (ms)");
		GAME_T = 100;//Integer.parseInt(cmd.readLine());
		
		point  = new Point(GAME_W, GAME_H);
		
		System.out.println("Init done. Waiting for Clients...");
		
		
		
		snake1 = new Snake(GAME_W, GAME_H);
		ss1 = new ServerSocket(a, 1, InetAddress.getLocalHost());
		s1 = ss1.accept();
		System.out.println("Client Connected");
		in1  = new BufferedInputStream (s1.getInputStream ());
		out1 = new BufferedOutputStream(s1.getOutputStream());
		
		
		
		snake2 = new Snake(GAME_W, GAME_H);
		ss2 = new ServerSocket(b, 1, InetAddress.getLocalHost());
		s2 = ss2.accept();
		System.out.println("Client Connected");
		in2  = new BufferedInputStream (s2.getInputStream ());
		out2 = new BufferedOutputStream(s2.getOutputStream());
		
		
		
		out1.write(new byte[] {(byte) GAME_W, 
	  			  			   (byte) GAME_H, 
	  			  			   (byte) GAME_T, 
	  			  			   (byte) point.width, 
	  			  			   (byte) point.height});
		out2.write(new byte[] {(byte) GAME_W, 
				  			   (byte) GAME_H, 
				  			   (byte) GAME_T, 
				  			   (byte) point.width, 
				  			   (byte) point.height});
		out1.flush();
		out2.flush();
		
		
		
		System.out.println("All Connected. Starting Game...");

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
		
			snake1 = new Snake(GAME_W, GAME_H);
			snake2 = new Snake(GAME_W, GAME_H);
			point  = new Point(GAME_W, GAME_H);
		
			if (in1.read() == -1) disconnect();
		}
	}
	
	private void disconnect() {
		try {
			cmd.close();
			ss1.close();
			ss2.close();
		} catch (Exception e) {System.out.println("Server may not have properly closed");}
		
		System.exit(0);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//MAIN METHOD///////////////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args) {
		try { new Host_Module(6005,6006);}				//change
		catch(Exception e) {}
	}
}