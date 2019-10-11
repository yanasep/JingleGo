
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

/**
 * This class is an abstract class of sprite.
 * @author Takumi
 *
 */
public abstract class Sprite {
    private double x;    
    private double y;
    private int width;  
    private int height; 
    private Image image;
    private Stage stage;
    
    //constructor
    public Sprite(double x, double y, int width, int height, String filename, Stage stage) {
    	this.x = x;
    	this.y = y;
    	this.width = width;
    	this.height = height;
    	loadImage(filename);
    	this.stage = stage;
    }
    
    /**
     * method to calculate sprite's next position
     */
    public abstract void update();

    /**
     * draw image at sprite's position     * 
     * @param g
     * @param offset
     */
    public void draw(Graphics g, double offset) {
		g.drawImage(image, (int) (x - offset), (int) y, width, height, null);
	}

    /**
     * load image of sprite
     * @param filename
     */
    private void loadImage(String filename) {
    	ImageIcon icon = new ImageIcon(getClass().getResource(filename));
		image = icon.getImage();
    }

    /**
     * checks collision with other sprite, but not stage
     * @param sprite
     * @return
     */
	public boolean isCollision(Sprite sprite) {
		Rectangle myRect = new Rectangle((int) x, (int) y, width, height);
		Rectangle spriteRect = new Rectangle((int) sprite.x, (int) sprite.y, sprite.width, sprite.height); 
		
		return myRect.intersects(spriteRect);
	}
    
    //getter, setter
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public Stage getStage() {
		return stage;
	}
}