
import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
/**
 * This class calculates stage position and
 * collision between stage block and sprites.
 * 
 * @author Takumi
 *
 */
public class Stage {
	// whole stage size
	final int LENGTH;
	final int HEIGHT;
	// stage block size
	final int BLOCK_SIZE = 13;
	final int GRAVITY = 1;
	// array representation of stage
	char[][] stage;
	private int row;
	private int col;
	//image
	private Image blockImage;
	//sprite list
	private List<Sprite> sprites;
	//sound
	AudioClip sound;
	
	//constructor
	public Stage() {
		sprites = new ArrayList<>();
		load("src/stage/stage.dat");
		
		LENGTH = col * BLOCK_SIZE;
		HEIGHT = row * BLOCK_SIZE;
		loadImage();
		
	}

	// draw stage
	public void draw(Graphics g, double offset) {
		//convert pixel to index of stage array
		int firstIndex = toIndex(offset);
		int lastIndex = toIndex(offset + GamePanel.WIDTH) + 1;
		
		//draw stage image
		for (int i = 0; i < row; i++) { // i = y-coordinate
			for (int j = firstIndex; j < lastIndex; j++) { // j = x-coordinate
				// draw
				switch(stage[i][j]) {
				case 'B':
					g.drawImage(blockImage, (int) (toPix(j) - offset), toPix(i), BLOCK_SIZE, BLOCK_SIZE, null);
					break;					
				}
				
			}
		}
	}
	
	//return list of sprites on the stage
	public List<Sprite> getSprites() {
		return sprites;
	}

	// return pixel position of the index of array
	public int toPix(int i) {
		return i * BLOCK_SIZE;
	}

	// return index of array at the position
	public int toIndex(double i) {
		return (int) Math.floor(i / BLOCK_SIZE);
	}

	/**
	 * returns the left-top point of collide block (collide horizontally)
	 * check each point of "?" mark below (not corner)
	 * _______
	 * ?     ?
	 * |     |
	 * |     |
	 * ?_____?
	 * 
	 * @param sprite
	 * @param x
	 * @param y
	 * @return
	 */
	public Point getHorizontalColBlock(Sprite sprite, double x, double y) {
		for (double xcheckpoint = x; xcheckpoint <= x + sprite.getWidth(); xcheckpoint += sprite.getWidth()) {
			for (double ycheckpoint = y + 1; ycheckpoint <= y + sprite.getHeight() - 1; ycheckpoint += sprite.getHeight() - 2) {
				int i = toIndex(ycheckpoint);
				int j = toIndex(xcheckpoint);
				
				if(j >= col) // out of stage length
					return null;
				
				try {
					if (stage[i][j] == 'B') // collide to block
						return new Point(toPix(j), toPix(i));
				}catch (IndexOutOfBoundsException e) {
					return null;
				}
			}
		}
		// no collision
		return null;
	}

	/**
	 * returns the left-top point of collide block (collide vertically)
	 * check each point of "?" mark below (not corner)
	 *  ?__?
	 * |    |
	 * |    |
	 * |    |
	 * |?__?|
	 * 
	 * @param sprite
	 * @param x
	 * @param y
	 * @return
	 */
	public Point getVerticalColBlock(Sprite sprite, double x, double y) {


		for (double xcheckpoint = x + 1; xcheckpoint <= x + sprite.getWidth() - 1; xcheckpoint += sprite.getWidth() - 2) { //loop twice
			for (double ycheckpoint = y; ycheckpoint <= y + sprite.getHeight(); ycheckpoint += sprite.getHeight()) { //loop twice
			
				int i = toIndex(ycheckpoint);
				int j = toIndex(xcheckpoint);
				
				if(i >= row) // more than stage height
					return null;
				
				try {
					if (stage[i][j] == 'B') // collision with block
						return new Point(toPix(j), toPix(i));
				}catch (IndexOutOfBoundsException e) {
					return null;
				}
			}
		}
		// no collision
		return null;
	}
    
	// load stage data from file
	public void load(String filename) {

		ArrayList<String> lines = new ArrayList<>();
		Path path = Paths.get(filename);

		try {
			// read all lines in a file
			lines = (ArrayList<String>) Files.readAllLines(path, StandardCharsets.UTF_8);
			
			// get width and height
			row = lines.size();
			col = lines.get(0).length();
			
			// store in an array
			stage = new char[row][col];
			for (int i = 0; i < row; i++) {
				String line = lines.get(i);
				for (int j = 0; j < col; j++) {
					stage[i][j] = line.charAt(j);
					if(line.charAt(j) == 'S')
						sprites.add(new Stone(toPix(j), toPix(i), this));
					else if(line.charAt(j) == 'M')
						sprites.add(new SnowMan(toPix(j), toPix(i), this));
				}
			}

		} catch (IOException e) {
			System.err.println("Unable to load stage file.");
		}

	}

	// load image of stage block
	private void loadImage() {
		ImageIcon icon = new ImageIcon(getClass().getResource("image/bubble.png"));
		blockImage = icon.getImage();
		
	}
}
