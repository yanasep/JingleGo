package sprite;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;

import stage.Stage;
import utils.Utils;

public class Player extends MovingSprite {

    private static final double JUMP_SPEED = Stage.player_jump_speed;
    private double speedUp;
    private AudioInputStream stream;
    private Clip clip;

    // if player is damaged, player will be knocked back and fall (i.e., no collision to
    // the floor)
    private enum State {
        onGround, jumped, doubleJumped, damaged
    }

    private State state;

    public Player(Point p, Dimension d, Image image, Stage stage) {
        super(p, d, image, stage.scroll_speed, 0, stage);
        this.state = State.onGround;
        speedUp = 0;

        // load jump sound
        try {
            stream = Utils.getAudioStream("/audio/powerup03.wav", getClass());
            clip = Utils.getClip(stream);
            clip.open(stream);
        } catch (Exception e) {
            // do nothing
        }
    }

    public void jump() {
        if (state != State.onGround && state != State.jumped)
            return;
        vy = JUMP_SPEED;
        state = (state == State.onGround) ? State.jumped : State.doubleJumped;
        if (clip != null)
            clip.start();
    }

    // jump when player steps on other sprite
    public void stepOn() {
        vy = JUMP_SPEED;
        state = State.jumped;
    }

    public void speedUp(double v) {
        // vx = stage.scroll_speed + v;
        speedUp = v;
    }

    // close audio input stream
    public void end() {
        try {
            stream.close();
            clip.close();
        } catch (Exception e) {
        }
    }

    @Override
    public void update(int timeElapsed) {

        vx = stage.scroll_speed + speedUp;
        vy += Stage.gravity * timeElapsed;
        vy = Math.min(vy, max_fall_speed);

        int newX = (int) (rect.x + vx * timeElapsed);
        int newY = (int) (rect.y + vy * timeElapsed);

        if (state == State.damaged) { // no collision (being ghost)
            // knock back a bit
            rect.x = (int) (rect.x - .4);
            rect.y = newY;
            return;
        }

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
            if (vy > 0) { // moving down
                rect.y = block.y - rect.height - 1;
                state = State.onGround;
            } else // moving up
                rect.y = block.y + stage.blockSize + 1;
            vy = 0;
        }

        // collision check to other sprites
        var sprites = stage.getActiveSprites();
        for (var it = sprites.iterator(); it.hasNext();) {
            var s = it.next();
            if (rect.intersects(s.rect)) {
                if (rect.y + rect.height * .5 < s.rect.y) {
                    stepOn();
                    it.remove();
                }
                else {
                    state = State.damaged;
                    vy = JUMP_SPEED;
                }
            }
        }

    }
}