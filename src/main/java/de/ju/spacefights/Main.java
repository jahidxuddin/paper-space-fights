package de.ju.spacefights;

import de.ju.spacefights.lib.Game;
import de.ju.spacefights.lib.GameObj;
import de.ju.spacefights.lib.Vertex;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Main implements Game {
    private List<List<? extends GameObj>> goss;
    private Player player;
    private ScoreDisplay scoreDisplay;
    private boolean isPaused = false;
    private boolean isGameOver = false;

    @Override
    public String title() {
        return "Paper Space Fights";
    }

    @Override
    public int width() {
        Window[] windows = Window.getWindows();
        if (windows.length > 0 && windows[0].isVisible()) {
            return windows[0].getWidth() - windows[0].getInsets().left - windows[0].getInsets().right;
        }
        return 720;
    }

    @Override
    public int height() {
        Window[] windows = Window.getWindows();
        if (windows.length > 0 && windows[0].isVisible()) {
            return windows[0].getHeight() - windows[0].getInsets().top - windows[0].getInsets().bottom;
        }
        return 480;
    }

    @Override
    public GameObj player() {
        return player;
    }

    @Override
    public List<List<? extends GameObj>> goss() {
        return goss;
    }

    @Override
    public void init() {
        this.player = new Player(this, new Vertex((double) (width() - 96) / 2, (double) (height() - 96) / 2), 96, 96);

        this.goss = new ArrayList<>();

        List<GameObj> backgroundLayer = new ArrayList<>();
        backgroundLayer.add(new InfiniteBackground(this, player, "/assets/background.png"));
        goss.add(backgroundLayer);

        List<GameObj> enemyLayer = new ArrayList<>();
        goss.add(enemyLayer);

        List<GameObj> projectileLayer = new ArrayList<>();
        goss.add(projectileLayer);

        List<GameObj> uiLayer = new ArrayList<>();
        uiLayer.add(new LifeDisplay(this));

        this.scoreDisplay = new ScoreDisplay();
        uiLayer.add(this.scoreDisplay);

        goss.add(uiLayer);
    }

    @Override
    public void move() {
        if (isPaused || isGameOver) return;
        Game.super.move();
    }

    @Override
    public void doChecks() {
        if (isPaused || isGameOver) return;

        List<GameObj> enemies = (List<GameObj>) goss.get(1);
        List<GameObj> projectiles = goss.size() > 2 ? (List<GameObj>) goss.get(2) : new ArrayList<>();

        if (enemies.isEmpty()) {
            enemies.addAll(spawnEnemies());
        }

        List<GameObj> deadEnemies = new ArrayList<>();
        List<GameObj> deadProjectiles = new ArrayList<>();

        for (GameObj p : projectiles) {
            if (collides(player, p)) {
                player.loseLife();
                deadProjectiles.add(p);

                if (player.getLives() <= 0) {
                    isGameOver = true;
                }
            }

            for (GameObj e : enemies) {
                if (collides(e, p)) {
                    scoreDisplay.addScore(100);
                    deadEnemies.add(e);
                    deadProjectiles.add(p);
                }
            }
        }

        for (GameObj e : enemies) {
            if (collides(player, e)) {
                player.loseLife();
                deadEnemies.add(e);

                if (player.getLives() <= 0) {
                    isGameOver = true;
                }
            }
        }

        enemies.removeAll(deadEnemies);
        projectiles.removeAll(deadProjectiles);
    }

    private boolean collides(GameObj o1, GameObj o2) {
        double dx = o1.pos().x - o2.pos().x;
        double dy = o1.pos().y - o2.pos().y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        return dist < (o1.width() + o2.width()) / 4.0;
    }

    private List<GameObj> spawnEnemies() {
        List<GameObj> newEnemies = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            double angle = Math.random() * Math.PI * 2;
            double minDistance = 700;
            double variableDistance = Math.random() * 400;
            double totalDistance = minDistance + variableDistance;

            double spawnX = player.getX() + Math.cos(angle) * totalDistance;
            double spawnY = player.getY() + Math.sin(angle) * totalDistance;

            newEnemies.add(new Enemy(this, new Vertex(spawnX, spawnY), 96, 96));
        }

        return newEnemies;
    }

    @Override
    public void keyPressedReaction(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> {
                if (isGameOver) {
                    init();
                    isGameOver = false;
                    isPaused = false;
                } else {
                    isPaused = !isPaused;
                }
            }

            case KeyEvent.VK_SPACE -> {
                if (isPaused || isGameOver) return;

                Laser shot = player.shoot();

                if (goss.size() > 2) {
                    if (shot != null) {
                        ((List<GameObj>) goss.get(2)).add(shot);
                    }
                } else {
                    List<GameObj> newLayer = new ArrayList<>();
                    newLayer.add(shot);
                    goss.add(newLayer);
                }
            }
        }
    }

    @Override
    public void keyReleasedReaction(KeyEvent keyEvent) {
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        if (isPaused || isGameOver) return;

        int mouseX = e.getX();
        int mouseY = e.getY();

        calcPlayerDirection(mouseX, mouseY);
    }

    private void calcPlayerDirection(int mouseX, int mouseY) {
        int screenPlayerX = width() / 2;
        int screenPlayerY = height() / 2;

        double dx = mouseX - screenPlayerX;
        double dy = mouseY - screenPlayerY;

        double angleRad = Math.atan2(dy, dx);
        double angleDeg = Math.toDegrees(angleRad);

        player.setAngle(angleDeg + 90);
    }

    @Override
    public void paintTo(Graphics g) {
        Game.super.paintTo(g);

        if (isGameOver) {
            drawTextToScreen(g, "GAME OVER");
        } else if (isPaused) {
            drawTextToScreen(g, "PAUSED");
        }
    }

    private void drawTextToScreen(Graphics g, String text) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, width(), height());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics fm = g.getFontMetrics();
        int x = (width() - fm.stringWidth(text)) / 2;
        int y = (height() / 2) + fm.getAscent() / 4;
        g.drawString(text, x, y);
    }

    @Override
    public boolean won() {
        return false;
    }

    @Override
    public boolean lost() {
        return false;
    }

    public static void main(String[] args) {
        new Main().play();
    }
}