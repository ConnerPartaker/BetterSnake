package singleFiles;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class Client_Exe {
	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		Game_Panel p = new Game_Panel(); //							<~~~~~~~~~~~RIGHT~HERE~~~~~~~~~~~
		
		f.setSize(p.S_DIM);
		f.setUndecorated(true);
		f.setVisible(true);
		f.add(p);
		
		p.requestFocusInWindow();
		p.inet.startGame();
	}
}



@SuppressWarnings("serial")
class Game_Panel extends JPanel {
	
	public  final Dimension S_DIM = Toolkit.getDefaultToolkit().getScreenSize();
	private final int BOX_DIM, W_MARGIN, H_MARGIN, GAME_W, GAME_H;
	
	public Internet_Module inet;
	
	private BufferedImage b;
	
	private static final Color     GREY = new Color(255,255,255,10);
	private static final Color DARKGREY = new Color(255,255,255,30);
	private static final Font    LUCIDA = new Font("Lucida Console", Font.PLAIN, 1000);
			
	//CONSTRUCTORS//////////////////////////////////////////////////////////////////////////////////////
	public Game_Panel() {
		
		inet = new Internet_Module(this);
		
		GAME_W = inet.GAME_W;
		GAME_H = inet.GAME_H;
		BOX_DIM = Math.min( (int)Math.floor(((S_DIM.getWidth()  - 20)/GAME_W)), 
							(int)Math.floor(((S_DIM.getHeight() - 20)/GAME_H)));
		
		W_MARGIN = (S_DIM.width  - BOX_DIM*GAME_W)/2;
		H_MARGIN = (S_DIM.height - BOX_DIM*GAME_H)/2;
		
		b = new BufferedImage(S_DIM.width, S_DIM.height, BufferedImage.TYPE_INT_ARGB);
		
		setSize(S_DIM);
		addKeyListener(inet);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//PAINT METHODS/////////////////////////////////////////////////////////////////////////////////////
	private void paintFrame() {
		
		Graphics g = b.getGraphics();
		
		//Main Board Creation
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, S_DIM.width, S_DIM.height);
		
		g.setColor(GREY);
		g.fillRect(W_MARGIN, H_MARGIN, BOX_DIM*GAME_W, BOX_DIM*GAME_H);
		
		//Score Creation
		g.setColor(DARKGREY);
		g.setFont(LUCIDA);
		if (inet.snake1.size() > 0)
			g.drawChars((""+(inet.snake1.size()-1)).toCharArray(), 0, (int)Math.ceil(Math.log10(inet.snake1.size())), W_MARGIN, H_MARGIN + BOX_DIM*GAME_H);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, H_MARGIN + BOX_DIM*GAME_H, S_DIM.width, H_MARGIN);
		
		//Snake Creation
		g.setColor(Color.BLUE);
		for(Dimension i: inet.snake1) fillbox(g, i);
		
		//Internet Snake Creation
		g.setColor(Color.GREEN);
		for(Dimension i: inet.snake2) fillbox(g, i);
		
		//Point Creation
		g.setColor(Color.RED);
		fillbox(g, inet.point);
		
		//Border Creation
		g.setColor(Color.WHITE);
		g.drawRect(W_MARGIN, H_MARGIN, BOX_DIM*GAME_W, BOX_DIM*GAME_H);
	}
	
	private void fillbox(Graphics g, Dimension i) {
		
		g.fillRect(W_MARGIN + i.width*BOX_DIM, H_MARGIN + (GAME_H - i.height - 1)*BOX_DIM, BOX_DIM, BOX_DIM);
	}
	
	protected void paintComponent(Graphics g) {	
		paintFrame();
		g.drawImage(b, 0, 0, null);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
}



class Internet_Module implements KeyListener{
	
	public  int GAME_W, GAME_H, dir, newdir;
	
	public  ArrayList<Dimension> snake1 = new ArrayList<Dimension>();
	public  ArrayList<Dimension> snake2 = new ArrayList<Dimension>();
	public  Dimension point = new Dimension();
	
	private Socket s;
	private BufferedInputStream  in ;
	private BufferedOutputStream out;
	
	private Game_Panel g;
	
	//CONSTRUCTORS//////////////////////////////////////////////////////////////////////////////////////
	public Internet_Module(Game_Panel g) {
		try {
			
			this.g = g;
			
			int    SOCKET_ID = Integer.parseInt(JOptionPane.showInputDialog("Socket Number"  , "6000"         )); 
			String HOST_IP   = (String)        (JOptionPane.showInputDialog("Host IP Address", "192.168.1.163"));
			
			s   = new Socket(HOST_IP, SOCKET_ID);
			in  = new BufferedInputStream (s.getInputStream() );
			out = new BufferedOutputStream(s.getOutputStream());
			
			
			byte[] inputs = new byte[5];
			in.read(inputs);
			
			GAME_W 		 = inputs[0] & 0xFF;
			GAME_H 		 = inputs[1] & 0xFF;
			point.width  = inputs[3] & 0xFF; 
			point.height = inputs[4] & 0xFF;
		
		} catch (Exception e) {JOptionPane.showMessageDialog(g, "INTERNET MODULE CONSTRUCTION ERROR");}
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
			
		} catch (Exception e) {}
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
		
		if (in.read() == 1) {JOptionPane.showMessageDialog(g, "W I N");} 
		else {JOptionPane.showMessageDialog(g, "L O S S");}
		
		disconnect();
	}

	public void disconnect() {
		try {
			in.close();
			out.close();
		} catch (Exception e) {}
		
		JOptionPane.showMessageDialog(g, "Opponent has left the Match");
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