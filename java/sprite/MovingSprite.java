package sprite;

import java.awt.Point;

import stage.Stage;

import java.awt.Dimension;
import java.awt.Image;

public class MovingSprite extends Sprite {

    protected double vx;
    protected double vy;
    protected double max_fall_speed = Stage.max_fall_speed;
    protected Stage stage;

    public MovingSprite(Point p, Dimension d, Image image, double vx, double vy, Stage stage) {
        super(p, d, image);
        this.vx = vx;
        this.vy = vy;
        this.stage = stage;
    }

    public void update(int timeElapsed) {

        vy += Stage.gravity * timeElapsed;
        vy = Math.min(vy, max_fall_speed);

        int newX = (int) (rect.x + vx * timeElapsed);
        int newY = (int) (rect.y + vy * timeElapsed);

        // move horizontally first (change only x)
        var block = stage.getCollisionBlock(this, newX, rect.y);

        if (block == null) {
            rect.x = newX;
        }
        else {
            if (vx > 0) // moving to the right
                rect.x = block.x - rect.width - 1;
            else // moving to the left
                rect.x = block.x + stage.blockSize + 1;
        }

        // move vertically
        block = stage.getCollisionBlock(this, rect.x, newY);

        if (block == null) {
            rect.y = newY;
        }
        else {
            if (vy > 0) // moving down
                rect.y = block.y - rect.height - 1;
            else // moving up
                rect.y = block.y + stage.blockSize + 1;
            vy = 0;
        }

    }

}