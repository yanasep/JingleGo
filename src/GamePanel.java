
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Game panel
 * gets sprites and stage data, and draw game view
 * @author Takumi
 *
 */
public class GamePanel extends JPanel implements Runnable {

	// screen size
	static final int WIDTH = 550;
	static final int HEIGHT = 300;
	// speed
	//double scroll_speed = 5;
	//static final int GRAVITY = 1;
	private final double ACCERELATION = 0.002;
	// offset of screen position from the beginning of the stage
	private double offset;
	// game objects
	private Stage stage;
	private Player player;
	private List<Sprite> sprites;
	//private JLabel label;
	private boolean over;
	private boolean goal;
	// image, sound
	private Image stageImage;
	// is key action performed
	private boolean keyAct;
	
	// constructor
	public GamePanel() {
		setBackground(Color.WHITE);
		offset = 0;
		//this.label = label;
		stage = new Stage();
		player = new Player(130, 170, stage);
		sprites = stage.getSprites();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		//
		over = false;
		goal = false;
		keyAct = false;
		
		// load image
		loadImage();

		//start
		Thread th = new Thread(this);
		th.start();
	}

	// draw stage and player
	public void paintComponent(Graphics g) {
		// draw stage
		g.drawImage(stageImage, 0, 0, WIDTH, HEIGHT, null);

		// draw game objects
		stage.draw(g, (int) offset);
		player.draw(g, (int) offset);
		for (Sprite sprite : sprites)
			sprite.draw(g, (int) offset);

		g.setFont(new Font("SansSerif", Font.BOLD, 60));
		g.setColor(Color.WHITE);
		if(over)
			g.drawString("Game Over", 200, 100);
		else if(goal)
			g.drawString("Goal", 200, 100);
	}

	// game loop
	public void run() {

		//calculate sprites, player, game over, goal
		while (!over && !goal) {
			if(MainPanel.mode != 1) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			continue;
			}
		
			//jump check
			if(keyAct) {
				player.jump();
				keyAct = false;
			}
			
			player.scroll_speed += ACCERELATION;

			// stop scrolling when reach the end of the stage
			if (offset > stage.LENGTH - WIDTH - 1)
				offset = stage.LENGTH - WIDTH - 1;

			// game over
			// if player get left or under the screen
			if (player.getX() + player.getWidth() - offset + 10 < -10 || player.getY() + player.getHeight() > stage.HEIGHT + 15) {
				// prevent wired move
			}else if (player.getX() + player.getWidth() - offset + 10 < 0 || player.getY() + player.getHeight() + 20 > stage.HEIGHT) {
				gameover();
				break;
			}
			// goal
			if (player.getX() + player.getWidth() + 10 > stage.LENGTH) {
				goal();
				break;
			}

			offset += player.scroll_speed;
			player.update();
			
			// update sprites
			for (int i = 0; i < sprites.size(); i++) {
				Sprite sprite = sprites.get(i);

				if (sprite.getX() + sprite.getWidth() - offset < 0) { // got out of the screen
					sprites.remove(sprite);
					i--;
					continue;
				}

				if (sprite.getX() - offset < WIDTH) // update only if on the screen
					sprite.update();

				if (!player.isDead() && player.isCollision(sprite)) {
					if (player.getY() + player.getHeight() < sprite.getY() + 20) { // step on a sprite
						
						if(sprite instanceof Stone){ // stone falls
							Stone stone = (Stone) sprite;
							stone.die();
						}
						
						else if(sprite instanceof SnowMan) // snowman vanish
							sprites.remove(sprite);
						
						// jump on the sprite 
						// do not count as an air jump
						player.setCanAirJump(true);
						player.jump();
						player.setCanAirJump(true);
						i--;
						continue;

					} else { // player die
						player.die();
					}
				}
			}

			// repaint
			repaint();

			// sleep
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		repaint();
		
		// if goal or game over, sleep and go to start panel
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(over || goal) 
			MainPanel.setMode(0); // go to start panel
		
	}

	private void gameover() {
		over = true;
	}

	private void goal() {
		goal = true;
	}

	private void loadImage() {
		ImageIcon icon = new ImageIcon(getClass().getResource("image/sky.jpg"));
		stageImage = icon.getImage();
	}

	public void setKeyAct(boolean b) {
		keyAct = b;
	}

}
