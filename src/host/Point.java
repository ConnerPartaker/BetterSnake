package host;

import java.awt.Dimension;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Point extends Dimension {
	
	private final int GAME_W, GAME_H;
	
	public Point (int GAME_W, int GAME_H) {
		
		this.GAME_W = GAME_W;
		this.GAME_H = GAME_H;
		
		makePoint();
	}
	
	public void newPoint(ArrayList<Dimension> snake1, ArrayList<Dimension> snake2) {
		
		do {makePoint();} while (snake1.contains(this) || snake2.contains(this));
	}
	
	public void makePoint() {
		
		width  = (int)Math.floor(Math.random()*GAME_W); 
		height = (int)Math.floor(Math.random()*GAME_H);
	}
}