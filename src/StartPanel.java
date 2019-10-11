
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Start Menu Panel
 * has play button to start game
 * @author Takumi
 *
 */
public class StartPanel extends JPanel {

	private JButton playBtn;
	private Image backImage;
	private Image santaImage;
	private AudioClip sound;
	
	public StartPanel(ActionListener al) {
		setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
		Font f = new Font("SansSerif", Font.BOLD, 20);
		// set up play button
		playBtn = new JButton("PLAY");
		playBtn.setFont(f);
		playBtn.setBackground(Color.WHITE);
		playBtn.setSize(150, 50);
		playBtn.setLocation(getWidth()/2, getHeight()/2);
		playBtn.addActionListener(al);
		
		add(playBtn);
		
		loadImage();
		ImageIcon icon = new ImageIcon(getClass().getResource("image/standingSanta.png"));
		santaImage = icon.getImage();

		// sound
		sound = Applet.newAudioClip(getClass().getResource("sound/Jingle Bells.wav"));
		sound.loop();
	}
	
	public void paintComponent(Graphics g) {
		//draw background image
		g.drawImage(backImage, 0, 0, GamePanel.WIDTH, GamePanel.HEIGHT, null);
		g.drawImage(santaImage, 350, 100, 170, 200, null);
		// draw title
		g.setColor(Color.GREEN);
		Font f = new Font("SansSerif", Font.BOLD, 60);
		g.setFont(f);
		g.drawString("JINGLE GO", 80, 80);
	}

	
	private void loadImage() {
		ImageIcon icon = new ImageIcon(getClass().getResource("image/snowView.png"));
		backImage = icon.getImage();
	}
}
