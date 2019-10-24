package stage;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.App;
import sprite.MovingSprite;
import sprite.Player;
import sprite.Sprite;
import utils.Utils;

// MVC : Model
public class Stage {

    private char[][] stage; // char map of (set to 0 when sprite is instantiated)
    private List<Sprite> activeSprites; // list of sprites on screen
    private Player player;
    private double offset; // screen offset from the beggining of the stage
    private double playerOffset; // player offset from the left edge of the screen

    private final int screen_row; // screen height
    private final int screen_col; // screen width

    // constants
    public double scroll_speed = .45;
    public static final double acceleration = .000005;
    public static final double gravity = .005;
    public static final double max_fall_speed = 1;
    public static final double player_jump_speed = -.85;
    public static final double stone_speed = -.05;

    public final int blockSize;

    private long oldTime; // to measure time elapsed
    private Map<String, Image> spriteImages;

    private enum State {
        PLAYING, PAUSED, DEAD, GOAL
    }

    private State state;

    public enum Mode {
        EASY("/stages/stage2.dat"),
        HARD("/stages/stage.dat");

        private final String value;

        private Mode(String s) {
            value = s;
        }
    }

    public Stage(Mode mode) {
        state = State.PLAYING;
        activeSprites = new ArrayList<>();
        loadImages();
        load(mode.value);
        offset = 0;
        blockSize = App.HEIGHT / stage.length;
        screen_row = stage.length;
        screen_col = toIndex(App.WIDTH);
        setUp();
        oldTime = 0;
    }

    // instantiate sprites on the first screen
    private void setUp() {
        for (int i = 0; i < screen_row; ++i) {
            for (int j = 0; j < screen_col; ++j) {
                var c = stage[i][j];
                if (c == 'S' || c == 'M') {
                    activeSprites.add(instantiate(c, i, j));
                    stage[i][j] = 0;
                } else if (c == 'P') {
                    player = (Player) instantiate(c, i, j);
                    playerOffset = player.getRect().x - offset;
                    stage[i][j] = 0;
                }
            }
        }
        if (player == null) {
            System.err.println("No player in screen");
            state = State.PAUSED;
        }
    }

    // update sprites on screen
    public void update() {
        if (state != State.PLAYING)
            return;

        if (oldTime == 0) { // first update
            oldTime = System.currentTimeMillis();
            return;
        }
        int timeElapsed = (int) (System.currentTimeMillis() - oldTime);
        // update screen offset
        offset += scroll_speed * timeElapsed;
        int offset_j = toIndex(offset);

        // if screen reaches end, stop shifting screen
        if (offset_j + screen_col >= stage[0].length) {
            offset -= scroll_speed;
        } else {

            // update active sprite list
            for (int i = 0; i < screen_row; ++i) {
                for (int j = 0; j < screen_col; ++j) {
                    var c = stage[i][j + offset_j];
                    if (c == 'S' || c == 'M') {
                        activeSprites.add(instantiate(c, i, j + offset_j));
                        stage[i][j + offset_j] = 0;
                    }
                }
            }
        }

        // update sprites position
        for (var it = activeSprites.iterator(); it.hasNext();) {
            var s = it.next();
            if (s.getRect().x + 50 < offset || s.getRect().x > offset + App.WIDTH || s.getRect().y > App.HEIGHT + 100)
                it.remove();
            else
                s.update(timeElapsed);
        }

        player.update(timeElapsed);

        // game end
        if (player.getRect().y > App.HEIGHT + 100 || player.getRect().x < offset - 200) {
            player.end();
            state = State.DEAD;
            return;
        } else if (player.getRect().x >= toPix(stage[0].length)) {
            player.end();
            state = State.GOAL;
            return;
        }

        if (player.getRect().x < offset + playerOffset)
            player.speedUp(.2);
        else
            player.speedUp(0); // return to normal speed

        scroll_speed += acceleration * timeElapsed;
        oldTime = System.currentTimeMillis();
    }

    private void load(String filename) {

        var lines = Utils.readFile(filename, getClass());

        int row = lines.size();
        int col = lines.get(0).length();

        // store in an array
        stage = new char[row][col];
        for (int i = 0; i < row; ++i) {
            stage[i] = lines.get(i).toCharArray();
        }

    }

    public int toPix(int index) {
        return index * blockSize;
    }

    public int toIndex(double pix) {
        return (int) (pix / blockSize);
    }

    private Sprite instantiate(char c, int i, int j) {
        switch (c) {
        case 'S':
            return new MovingSprite(new Point(toPix(j), toPix(i)), new Dimension(35, 35), spriteImages.get("stone"), stone_speed, 0, this);
        case 'M':
            return new Sprite(new Point(toPix(j), toPix(i)), new Dimension(75, 75), spriteImages.get("snowman"));
        case 'P':
            return new Player(new Point(toPix(j), toPix(i)), new Dimension(40, 30), spriteImages.get("player"), this);
        default:
            return null;
        }
    }

    private void loadImages() {
        spriteImages = new HashMap<>();
        spriteImages.put("player", Utils.loadImage("/images/flyingSanta.png", getClass()));
        spriteImages.put("block", Utils.loadImage("/images/bubble.png", getClass()));
        spriteImages.put("snowman", Utils.loadImage("/images/snowman.png", getClass()));
        spriteImages.put("stone", Utils.loadImage("/images/stone.png", getClass()));
    }

    public char[][] getStage() {
        return stage;
    }

    public int getOffset() {
        return (int) offset;
    }

    public List<Sprite> getActiveSprites() {
        return activeSprites;
    }

    public Player getPlayer() {
        return player;
    }

    public Image getImage(String name) {
        return spriteImages.get(name);
    }

    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (state == State.PLAYING)
                state = State.PAUSED;
            else {
                state = State.PLAYING;
                oldTime = 0;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (state == State.PLAYING)
                player.jump();
        }
    }

    /**
     * check rectangler area and get first-found block intersected. star represents
     * sprite's current and new position. *------------- | | -------------*
     *
     * @return left-top point of colliding block
     */
    public Point getCollisionBlock(MovingSprite sprite, int newX, int newY) {
        // get left-top and right-bottom corners of rectangle
        int fromX = Math.min(sprite.getRect().x, newX);
        int fromY = Math.min(sprite.getRect().y, newY);
        int toX = Math.max(sprite.getRect().x, newX);
        int toY = Math.max(sprite.getRect().y, newY);

        int fromI = toIndex(fromY);
        int fromJ = toIndex(fromX);
        int toI = toIndex(toY + sprite.getRect().height);
        int toJ = toIndex(toX + sprite.getRect().width);

        for (int i = fromI; i <= toI; ++i) {
            for (int j = fromJ; j <= toJ; ++j) {
                if (i < 0 || i >= stage.length || j < 0 || j >= stage[0].length)
                    continue;
                if (stage[i][j] == 'B')
                    return new Point(toPix(j), toPix(i));
            }
        }
        return null;
    }

    public boolean isDead() {
        return state == State.DEAD;
    }

    public boolean isGoaled() {
        return state == State.GOAL;
    }

    public boolean isPaused() {
        return state == State.PAUSED;
    }
}
