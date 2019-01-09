import java.awt.Dimension;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Snake extends ArrayList<Dimension> {
	
	public int dir, newdir;
	private final int GAME_W, GAME_H;
	
	public Snake(int GAME_W, int GAME_H) {
		
		this.GAME_W = GAME_W;
		this.GAME_H = GAME_H;
		
		add(new Dimension((int)Math.floor(Math.random()*GAME_W/2) + GAME_W/4, (int)Math.floor(Math.random()*GAME_H/2) + GAME_H/4));
	}

	public int setDir(int dir) {
		
		newdir = (this.dir + dir)%2 == 1 || size() == 1 ? dir : newdir;
		return dir;
	}
	
	public boolean move(Dimension point) {
		
		Dimension head = new Dimension(get(0));
		
		dir = newdir;
		
		head.width += Math.abs(dir-2) - 1;
		head.height-= Math.abs(dir-1) - 1;
		
		add(0, head);
		
		if(head.equals(point)) return true; 
		
		remove(size()-1);
		return false;
	}
	
	public boolean check(ArrayList<Dimension> illegal) {
		
		Dimension head = new Dimension(get(0));
		
		if( (head.width +1)*(head.width -GAME_W) >= 0 
		|| 	(head.height+1)*(head.height-GAME_H) >= 0 
		|| 	(lastIndexOf(head) !=  0)
		||  (illegal.indexOf(head) != -1))
		
			return true;
		return false;
	}
}