package de.ju.spacefights;

import de.ju.spacefights.lib.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Player extends AbstractGameObj {
    private final Game game;
    private final BufferedImage sprite;
    private final double speed;

    private double angle = 0;
    private int cooldown = 0;

    private int lives = 5;

    public Player(Game game, Vertex position, double width, double height) {
        super(position, new Vertex(0, 0), width, height);
        this.game = game;
        this.sprite = SpriteLoader.load("/assets/ship_L.png");
        this.speed = 10;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        lives--;
    }

    public Laser shoot() {
        if (cooldown > 0) return null;

        cooldown = 15;

        SoundLoader.playSound("/assets/player_shoot.wav");

        double rad = Math.toRadians(angle - 90);

        double noseDistance = this.height / 2.0;

        double spawnX = this.pos.x + Math.cos(rad) * noseDistance;
        double spawnY = this.pos.y + Math.sin(rad) * noseDistance;

        return new Laser(game, new Vertex(spawnX, spawnY), angle);
    }

    public double getX() { return this.pos.x; }
    public double getY() { return this.pos.y; }
    public double getSpeed() { return this.speed; }
    public void moveX(double deltaX) { this.pos.add(new Vertex(deltaX, 0)); }
    public void moveY(double deltaY) { this.pos.add(new Vertex(0, deltaY)); }
    public void setAngle(double degree) { this.angle = degree; }

    @Override
    public void move() {
        if (cooldown > 0) {
            cooldown--;
        }

        double radians = Math.toRadians(this.angle - 90);

        double dx = Math.cos(radians) * speed;
        double dy = Math.sin(radians) * speed;

        this.pos.add(new Vertex(dx, dy));
    }

    @Override
    public void paintTo(Graphics g) {
        if (sprite == null) return;

        Graphics2D g2d = (Graphics2D) g;

        int screenWidth = game.width();
        int screenHeight = game.height();

        int drawX = (screenWidth / 2) - ((int) width / 2);
        int drawY = (screenHeight / 2) - ((int) height / 2);

        double centerX = drawX + width / 2.0;
        double centerY = drawY + height / 2.0;

        AffineTransform oldTransform = g2d.getTransform();

        g2d.rotate(Math.toRadians(angle), centerX, centerY);

        g.drawImage(sprite, drawX, drawY, (int) width, (int) height, null);

        g2d.setTransform(oldTransform);
    }
}