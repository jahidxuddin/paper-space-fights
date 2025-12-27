package de.ju.spacefights;

import de.ju.spacefights.lib.AbstractGameObj;
import de.ju.spacefights.lib.Game;
import de.ju.spacefights.lib.GameObj;
import de.ju.spacefights.lib.Vertex;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Laser extends AbstractGameObj {
    private final Game game;
    private final double angle;
    private final Color color;

    public Laser(Game game, Vertex position, double angle) {
        this(game, position, angle, Color.CYAN);
    }

    public Laser(Game game, Vertex position, double angle, Color color) {
        super(position, new Vertex(0, 0), 6, 30);
        this.game = game;
        this.angle = angle;
        this.color = color;

        double speed = 25;
        double rad = Math.toRadians(angle - 90);

        this.velocity = new Vertex(
                Math.cos(rad) * speed,
                Math.sin(rad) * speed
        );
    }

    @Override
    public void paintTo(Graphics g) {
        GameObj player = game.player();

        double diffX = this.pos.x - player.pos().x;
        double diffY = this.pos.y - player.pos().y;

        int screenCenterX = game.width() / 2;
        int screenCenterY = game.height() / 2;

        int drawX = (int) (screenCenterX + diffX - (width / 2));
        int drawY = (int) (screenCenterY + diffY - (height / 2));

        if (drawX < -50 || drawX > game.width() + 50 || drawY < -50 || drawY > game.height() + 50) return;

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTrans = g2d.getTransform();

        g2d.rotate(Math.toRadians(angle), drawX + width / 2.0, drawY + height / 2.0);

        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
        g.fillOval(drawX - 2, drawY - 2, (int) width + 4, (int) height + 4);

        g.setColor(color);
        g.fillOval(drawX, drawY, (int) width, (int) height);

        g2d.setTransform(oldTrans);
    }
}