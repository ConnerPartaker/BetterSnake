import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class Internet_Module implements KeyListener{
	
	public  int GAME_W, GAME_H;
	
	public  ArrayList<Dimension> snake1 = new ArrayList<Dimension>();
	public  ArrayList<Dimension> snake2 = new ArrayList<Dimension>();
	public  Dimension point = new Dimension();
	
	private Socket s;
	private BufferedReader cmd;
	private BufferedInputStream  in ;
	private BufferedOutputStream out;
	
	private Game_Panel g;

	private int dir, newdir;
	
	//CONSTRUCTORS//////////////////////////////////////////////////////////////////////////////////////
	public Internet_Module(Game_Panel g, int a) {
		try {
			
			this.g = g;
			
			cmd = new BufferedReader(new InputStreamReader (System.in));
			System.out.println("Enter Socket Number");
			//int SOCKET_ID = Integer.parseInt(cmd.readLine()); 
			
			System.out.println("Enter Host IP");
			s = new Socket("192.168.1.163", a); //s = new Socket(cmd.readLine(), SOCKET_ID);
			System.out.println("Host Reached...");
			
			in  = new BufferedInputStream (s.getInputStream() );
			out = new BufferedOutputStream(s.getOutputStream());
			
			
			byte[] inputs = new byte[5];
			in.read(inputs);
			
			GAME_W 		 = inputs[0] & 0xFF;
			GAME_H 		 = inputs[1] & 0xFF;
			point.width  = inputs[3] & 0xFF; 
			point.height = inputs[4] & 0xFF;
		
		} catch (Exception e) {System.out.println("Internet Module Construction Error");}
	}
	
	public void startGame() {
		try {
			
			while (true) {
			
				switch (in.read()) {
					case -1:  disconnect();
						break;
					case 255: endGame();
						break;
					default:  sendLocation();
						break;
				}
				returnLocation();
				g.repaint();
			}
			
		} catch (Exception e) {System.out.println("Clientside Game Error");}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//INTERNET LOCATION METHODS/////////////////////////////////////////////////////////////////////////
	private void sendLocation() throws Exception {
		
		out.write(dir = newdir);
		out.flush();
	}
	
	private void returnLocation() throws Exception {
		
		byte[] inputs = new byte[8];
		in.read(inputs);
		
		snake1.add(0, new Dimension(inputs[0] & 0xFF, inputs[1] & 0xFF));
		snake2.add(0, new Dimension(inputs[2] & 0xFF, inputs[3] & 0xFF));
			
		if (snake1.size() != (inputs[4] & 0xFF)) snake1.remove(snake1.size()-1);
		if (snake2.size() != (inputs[5] & 0xFF)) snake2.remove(snake2.size()-1);
			
		point.width  = inputs[6] & 0xFF;
		point.height = inputs[7] & 0xFF;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////

	//END GAME METHODS//////////////////////////////////////////////////////////////////////////////////
	private void endGame() throws Exception {
		
		if (in.read() == 1) {System.out.println("WIN");} 
		else {System.out.println("| || || |_");}
		
		disconnect();
	}

	public void disconnect() {
		
		System.out.println("Opponent has left the match");
		
		try {
			cmd.close();
			in.close();
			out.close();
		} catch (Exception e) {System.out.println("Client Socket may not have properly closed");}
		
		System.exit(0);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//KEYLISTENERS//////////////////////////////////////////////////////////////////////////////////////
	public void setDir(int dir) {newdir = (this.dir + dir)%2 == 1 || snake1.size() == 1 ? dir : newdir;}
	
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_RIGHT : setDir(0);
				break;
			case KeyEvent.VK_UP    : setDir(1);
				break;
			case KeyEvent.VK_LEFT  : setDir(2);
				break;
			case KeyEvent.VK_DOWN  : setDir(3);
				break;
			case KeyEvent.VK_ESCAPE: disconnect();
				break;
			default:
				break;
		}
	}
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}
	////////////////////////////////////////////////////////////////////////////////////////////////////
}