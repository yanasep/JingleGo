package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.TextLayout;

import javax.swing.JPanel;

import stage.Stage.Mode;
import utils.Utils;

public class StartPanel extends JPanel {

	private static final long serialVersionUID = 8216662761258942411L;

	private Image santaImg;
	private Image backImg;
	private App app;

	public StartPanel(App app) {

		this.app = app;

		setPreferredSize(new Dimension(App.WIDTH, App.HEIGHT));
		setFocusable(true);

		addKeyListener(new KeyHandler());

		backImg = Utils.loadImage("/images/snowView.png", getClass());
		santaImg = Utils.loadImage("/images/standingSanta.png", getClass());
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(backImg, 0, 0, App.WIDTH, App.HEIGHT, null);
		g.drawImage(santaImg, 500, 200, 170, 200, null);

		g.setColor(Color.black);
		g.setFont(new Font("SansSerif", Font.PLAIN, 11));
		g.drawString("Press ESC to quit", 680, 470);

		g.setFont(new Font("SansSerif", Font.PLAIN, 20));
		g.drawString("Choose: (E)asy, (H)ard", 290, 300);

		Graphics2D g2 = (Graphics2D) g;
		String str = "Jingle Go";
		Font f = new Font("SansSerif", Font.BOLD, 60);

		FontMetrics metrics = g2.getFontMetrics(f);
		int x = (App.WIDTH - metrics.stringWidth(str)) / 2;
		g2.translate(x, 150);

		// shadow
		g2.setFont(f);
		g2.setColor(new Color(50, 50, 50));
		g2.drawString(str, 5, 5);

		// text
		g2.setColor(Color.GREEN);
		g2.drawString(str, 0, 0);

		// outline
		TextLayout tl = new TextLayout(str, f, g2.getFontRenderContext());
		Shape shape = tl.getOutline(null);
		g2.setStroke(new java.awt.BasicStroke(2f));
		g2.setColor(Color.white);
		g2.draw(shape);
	}

	private class KeyHandler implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
				break;
			case KeyEvent.VK_E:
				app.gameStart(Mode.EASY);
				break;
			case KeyEvent.VK_H:
				app.gameStart(Mode.HARD);
				break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}

		@Override
		public void keyTyped(KeyEvent e) {}
	}
}
