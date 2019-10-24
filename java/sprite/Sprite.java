package sprite;

import java.awt.Image;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * This is the base class for all game entities.
 */
public class Sprite {

    protected Rectangle rect;
    private Image image;

    public Sprite(Point p, Dimension d, Image image) {
        this.rect = new Rectangle(p, d);
        this.image = image;
    }

    // update sprite's position
    public void update(int TimeElapsed) {}

    public boolean isCollision(Sprite other) {
        return rect.intersects(other.rect);
    }

    public Point pos() {
        return new Point(rect.x, rect.y);
    }

    public Image getImage() {
        return image;
    }

    public Rectangle getRect() {
        return rect;
    }
}