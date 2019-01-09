import javax.swing.JFrame;

public class Multithread_Drifting {

	public static void main(String[] args) {
		
		int a = 5086;
		
		new Thread() { 
			
			public void run() {
						
				try { new Host_Module(a, a+1);}
				catch(Exception e) {}
		
			}
		}.start();
		
		new Thread() { 
			
			public void run() {
						
				JFrame f = new JFrame();
				Game_Panel p = new Game_Panel(a);
		
				f.setSize(p.S_DIM);
				f.setUndecorated(true);
				f.setVisible(true);
				f.add(p);
		
				p.inet.startGame();
		
			}
		}.start();
		
		new Thread() { 
			
			public void run() {
						
				JFrame f = new JFrame();
				Game_Panel p = new Game_Panel(a+1);
		
				f.setSize(p.S_DIM);
				f.setUndecorated(true);
				f.setVisible(true);
				f.add(p);
		
				p.inet.startGame();
		
			}
		}.start();
	}
}
