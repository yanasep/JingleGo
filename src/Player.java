
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
/**
 * This class calculates player's position
 * @author Takumi
 *
 */
public class Player extends Sprite {
	// velocity
	private double vx;
	private double vy;
	private final double JUMP_SPEED = 12;
	double scroll_speed;
	//status
	private boolean onGround;
	private boolean canAirjump;
	private boolean dead; // player die when hit a sprite
	private int deadcount; // used for dead action
	private AudioClip jumpsound;

	public Player(double x, double y, Stage stage) {
		super(x, y, 40, 30, "image/flyingSanta.png", stage);
		vx = 0;
		vy = 0;
		scroll_speed = 5;
		onGround = true;
		canAirjump = true;
		dead = false;
		deadcount = 0;
		jumpsound = Applet.newAudioClip(getClass().getResource("sound/powerup03.wav"));
	}

	// jump
	public void jump() {
		if(!onGround && !canAirjump) // on the air but cannot air jump
			return; // do nothing
		
		vy = -JUMP_SPEED; // jump
		jumpsound.play();
		
		if(onGround)
			onGround = false; // no longer on the ground
		else if(canAirjump) 
			canAirjump = false; // allow no more air jump
	}
	

	// update player position
	public void update() {
		
		vx = scroll_speed;
		vy += getStage().GRAVITY;
		
		if(vy > 12)  // set speed limit
			vy -= getStage().GRAVITY;
		
		//calculate new position
		double newX = getX() + vx;
		double newY = getY() + vy;

		// player goes no higher than the height of stage
		if (newY < 0) {
			newY = 0;
			vy = 0;
		}

		// check collision about x direction
		Point point = getStage().getHorizontalColBlock(this, newX, getY());
		if (point == null) { // no collision
			setX(newX);

		} else { // collide to right
			setX(point.x - getWidth());
			vx = 0;
		}

		// check collision about y direction
		if(!dead)
			point = getStage().getVerticalColBlock(this, getX(), newY);
		else {// if player is dead, slip through the foor
			point = null;
			
			// dead action
			if(deadcount == 0) // jump a little bit
				vy = 2;
			else if(deadcount == 1) // fall immediately
				vy = -7;
			deadcount++;
		}
		
			
		if (point == null) { // no collision
			setY(newY);
			onGround = false;
		} else {
			if (vy < 0) // collide to ceiling
				setY(point.y + getStage().BLOCK_SIZE);
			else { // collide to floor
				setY(point.y - getHeight());
				onGround = true;
				canAirjump = true;
			}

			vy = 0;
		}

	}//end method update
	
	public void die() {
		dead = true;
	}
	
	// getter, setter
	public boolean isDead() {
		return dead;
	}
	public void setCanAirJump(boolean b) {
		canAirjump = b;
	}
}//end class Player
