
import java.awt.Graphics;
import java.awt.Point;

public class Stone extends Sprite {

	int vx;
	int vy;
	final int SPEED = -1;
	private boolean dead;
	
	public Stone(double x, double y, Stage stage) {
		super(x, y, 35, 35, "image/stone.png", stage);
		vx = 0;
		vy = 0;
		dead = false;
	}	

	public void update() {
		vx = SPEED;
		vy += getStage().GRAVITY;
		
		if(vy > 10) // set speed limit
			vy -= getStage().GRAVITY;
		
		//calculate new position
		double newX = getX() + vx;
		double newY = getY() + vy;

		// stop when drop from the stage
		if(newY + getHeight() + 10 > getStage().HEIGHT)
			return;
		
		// check collision about x direction
		Point point = getStage().getHorizontalColBlock(this, newX, getY());
		if (point == null) { // no collision
			setX(newX);

		} else { // collide to left
			setX(point.x + getStage().BLOCK_SIZE);
			vx = 0;
		}

		// check collision about y direction
		if(dead) 
			point = null; // fall
		else
			point = getStage().getVerticalColBlock(this, getX(), newY);
	
		if (point == null) { // no collision
			setY(newY);

		} else { // collide to floor
			setY(point.y - getHeight());
			vy = 0;
		}
	}

	public void die() {
		dead = true;
	}
}
