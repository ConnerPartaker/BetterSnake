package client;

import javax.swing.JFrame;

public class Game_Client {
	public static void main(String[] args) {
		
		JFrame f = new JFrame();
		Game_Panel p = new Game_Panel();
		
		f.setSize(p.S_DIM);
		f.setUndecorated(true);
		f.setVisible(true);
		f.add(p);
		
		p.requestFocusInWindow();
		p.inet.startGame();
	}
}