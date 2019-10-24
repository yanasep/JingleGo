package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.TextLayout;

import javax.swing.JPanel;

import stage.Stage;
import stage.Stage.Mode;
import utils.Utils;

// MVC : View
public class GamePanel extends JPanel implements Runnable {

    private static final long serialVersionUID = 1216662235258942411L;
    private static final int delay = 5;

    private App app;
    private Image backImg;
    private Stage stage;

    public GamePanel(App app, Mode mode){

        this.app = app;
        this.stage = new Stage(mode);

        setPreferredSize(new Dimension(App.WIDTH, App.HEIGHT));
        setFocusable(true);

        addKeyListener(new KeyHandler());

        this.backImg = Utils.loadImage("/images/sky.jpg", getClass());

        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(backImg, 0, 0, App.WIDTH, App.HEIGHT, this);

        g.setColor(Color.white);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString(stage.getOffset() + "m", 10, 10);
		g.drawString("Press 'P' to pause", 680, 470);

        // draw stage blocks
        var stg = stage.getStage();
        for (int i = 0; i < stg.length; ++i) {
            for (int j = 0; j < stg[i].length; ++j) {
                if (stg[i][j] == 'B')
                    g.drawImage(stage.getImage("block"), stage.toPix(j) - stage.getOffset(), stage.toPix(i),
                            stage.blockSize, stage.blockSize, this);
            }
        }

        // draw game entities
        var sprites = stage.getActiveSprites();

        for (var s : sprites) {
            var r = s.getRect();
            g.drawImage(s.getImage(), r.x - stage.getOffset(), r.y, r.width, r.height, this);
        }

        // draw player
        var p = stage.getPlayer();
        var r = p.getRect();
        g.drawImage(p.getImage(), r.x - stage.getOffset(), r.y, r.width, r.height, this);

        // draw game end text
        if (stage.isDead() || stage.isGoaled())
            drawGameEndText(g);

        else if (stage.isPaused())
            drawPausedText(g);

    }

    @Override
    public void run() {
        while (!stage.isGoaled() && !stage.isDead()) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stage.update();
            repaint();
        }
    }

    private class KeyHandler implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                System.exit(0);
            else if (stage.isGoaled() || stage.isDead()) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    app.gameEnd();
            }
            else if (stage.isPaused() && e.getKeyCode() == KeyEvent.VK_Q)
                app.gameEnd();
            else
                stage.onKeyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }
    }

    private void drawGameEndText(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // text
        Font f = new Font("SansSerif", Font.BOLD, 40);
        String str = stage.isGoaled() ? "Merry Christmas!" : "Game Over";

        // find horizontal center
        FontMetrics metrics = g2.getFontMetrics(f);
        int x = (App.WIDTH - metrics.stringWidth(str)) / 2;

        g2.setFont(f);
        g2.setColor(Color.black);

        g2.translate(x, 220);
        g2.drawString(str, 5, 5);

        // text outline
        TextLayout tl = new TextLayout(str, f, g2.getFontRenderContext());
        Shape shape = tl.getOutline(null);
        g2.setColor(Color.white);
        g2.fill(shape);

        // sub text
        g2.translate(-x, 0); // reset x-coordinate to 0
        f = new Font("SansSerif", Font.PLAIN, 20);
        str = "Press enter to return";

        metrics = g2.getFontMetrics(f);
        x = (App.WIDTH - metrics.stringWidth(str)) / 2;

        g2.setFont(f);
        g2.setColor(Color.black);

        g2.translate(x, 100);
        g2.drawString(str, 3, 3);

        // sub text outline
        tl = new TextLayout(str, f, g2.getFontRenderContext());
        shape = tl.getOutline(null);
        // g2.setStroke(new java.awt.BasicStroke(.00001f)); // line thickness
        g2.setColor(Color.white);
        g2.fill(shape);
    }

    private void drawPausedText(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(320, 230);

        // text
        Font f = new Font("SansSerif", Font.BOLD, 40);
        String str = "Pause";
        g2.setFont(f);
        g2.setColor(Color.black);
        g2.drawString(str, 5, 5);

        // text outline
        TextLayout tl = new TextLayout(str, f, g2.getFontRenderContext());
        Shape shape = tl.getOutline(null);
        g2.setColor(Color.white);
        g2.fill(shape);

        g2.translate(30, 100);
    }
}
