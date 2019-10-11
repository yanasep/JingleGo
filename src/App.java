import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Main class
 * create JFrame and add MainPanel
 * @author Takumi
 *
 */
public class App extends JFrame{
	
	private static MainPanel panel;

	public App() {
		super("Jingle Go");

		// create and add MainPanel 
		panel = new MainPanel();
		add(panel);
	}
	
	public static void main(String[] args) {
		App frame = new App();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		// fit to panel size
		frame.pack();
	}

}
