package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Game_Panel extends JPanel {
	
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