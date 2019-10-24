package app;

import javax.sound.sampled.Clip;
import javax.swing.JFrame;

import stage.Stage.Mode;
import utils.Utils;

public class App extends JFrame {

	private static final long serialVersionUID = 5430513214412853815L;

	public static final int WIDTH = 800;
	public static final int HEIGHT = 500;

	private StartPanel startPanel;
	private GamePanel gamePanel;

	public App() {
		super("Jingle Go");
		startPanel = new StartPanel(this);
		add(startPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(200, 100);
		pack();
		setResizable(false);
		setVisible(true);

		// BGM
		try {
			var stream = Utils.getAudioStream("/audio/Jingle Bells.wav", getClass());
			var clip = Utils.getClip(stream);
			clip.open(stream);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
			// do nothing
		}
	}

	public static void main(String[] args) {
		new App();
	}

	public void gameStart(Mode mode) {
		gamePanel = new GamePanel(this, mode);
		add(gamePanel);
		gamePanel.setVisible(true);
		startPanel.setVisible(false);
	}

	public void gameEnd() {
		startPanel.setVisible(true);
		gamePanel.setVisible(false);
	}

}
