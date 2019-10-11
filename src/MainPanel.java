import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

/**
 * This class is a controller.
 * 
 * @author Takumi
 *
 */
public class MainPanel extends JPanel implements Runnable {
	private StartPanel startPanel;
	private GamePanel gamePanel;
	static int mode; // 0 = start 1 = game

	public MainPanel() {
		mode = 0;

		setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
		setFocusable(true);

		// play button handler
		ButtonHandler bh = new ButtonHandler();

		// create panels
		startPanel = new StartPanel(bh);
		gamePanel = new GamePanel();
		gamePanel.setVisible(false);
		// add
		add(startPanel);
		add(gamePanel);
		// register listener
		addKeyListener(new KeyHandler());

		// start
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override

	public void run() {
		while (true) {

			// switch screen between start panel and game panel
			if (mode == 0) {
				startPanel.setVisible(true);
				gamePanel.setVisible(false);

			} else if (mode == 1) {
				startPanel.setVisible(false);
				gamePanel.setVisible(true);

			}

			// sleep
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private class KeyHandler implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			if(mode == 1)
				gamePanel.setKeyAct(true);
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

	}

	// play button handler class
	private class ButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (mode == 0) { // accepts action only on start panel
				mode = 1;
				remove(gamePanel);
				gamePanel = new GamePanel();
				add(gamePanel);
			}
		}

	}

	public static void setMode(int i) {
		mode = i;
	}

}
