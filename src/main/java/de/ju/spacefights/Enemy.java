package de.ju.spacefights;

import de.ju.spacefights.lib.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends AbstractGameObj {
    private final Game game;
    private final BufferedImage sprite;

    private final double idealDistance = 300;
    private final double turnSpeed = 0.1;

    private double angle = 0;
    private int cooldown = (int) (Math.random() * 200);

    public Enemy(Game game, Vertex position, double width, double height) {
        super(position, new Vertex(0, 0), width, height);
        this.game = game;
        this.sprite = SpriteLoader.load("/assets/ship_sidesC.png");
    }

    @Override
    public void move() {
        GameObj player = game.player();

        double dx = player.pos().x - this.pos.x;
        double dy = player.pos().y - this.pos.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        double targetAngleRad = Math.atan2(dy, dx);
        double targetAngle = Math.toDegrees(targetAngleRad) + 90;

        this.angle = lerpAngle(this.angle, targetAngle, turnSpeed);

        double dirX = dx / distance;
        double dirY = dy / distance;

        double targetMoveX;
        double targetMoveY;

        if (distance > idealDistance + 50) {
            targetMoveX = dirX;
            targetMoveY = dirY;
        } else if (distance < idealDistance - 50) {
            targetMoveX = -dirX;
            targetMoveY = -dirY;
        } else {
            targetMoveX = -dirY;
            targetMoveY = dirX;
        }

        double separationRange = 150;
        double separationForce = 3.5;

        double sepX = 0;
        double sepY = 0;
        int neighborsFound = 0;

        for (var list : game.goss()) {
            for (var obj : list) {
                if (obj == this || !(obj instanceof Enemy)) continue;

                double diffX = this.pos.x - obj.pos().x;
                double diffY = this.pos.y - obj.pos().y;
                double dist = Math.sqrt(diffX * diffX + diffY * diffY);

                if (dist < separationRange) {
                    sepX += diffX / dist;
                    sepY += diffY / dist;
                    neighborsFound++;
                }
            }
        }

        if (neighborsFound > 0) {
            targetMoveX += sepX * separationForce;
            targetMoveY += sepY * separationForce;
        }

        double currentSpeed = 8;
        if (distance > 600) {
            currentSpeed = 22;
        }

        double totalLength = Math.sqrt(targetMoveX * targetMoveX + targetMoveY * targetMoveY);
        if (totalLength > 0) {
            targetMoveX = (targetMoveX / totalLength) * currentSpeed;
            targetMoveY = (targetMoveY / totalLength) * currentSpeed;
        }

        double smoothVelX = lerp(this.velocity.x, targetMoveX, turnSpeed);
        double smoothVelY = lerp(this.velocity.y, targetMoveY, turnSpeed);

        this.velocity = new Vertex(smoothVelX, smoothVelY);

        if (cooldown > 0) {
            cooldown--;
        } else if (distance < 600) {
            shoot();
            cooldown = 150 + (int) (Math.random() * 100);
        }

        super.move();
    }

    private void shoot() {
        SwingUtilities.invokeLater(() -> {
            if (game.goss().size() <= 2) {
                game.goss().add(new ArrayList<>());
            }

            double rad = Math.toRadians(angle - 90);
            double noseDistance = this.height / 2.0;

            double spawnX = this.pos.x + Math.cos(rad) * noseDistance;
            double spawnY = this.pos.y + Math.sin(rad) * noseDistance;

            Laser shot = new Laser(game, new Vertex(spawnX, spawnY), angle, Color.RED);

            ((List<GameObj>) game.goss().get(2)).add(shot);
        });
    }

    private double lerp(double current, double target, double factor) {
        return current + factor * (target - current);
    }

    private double lerpAngle(double current, double target, double factor) {
        double diff = target - current;

        while (diff < -180) diff += 360;
        while (diff > 180) diff -= 360;

        return current + diff * factor;
    }

    @Override
    public void paintTo(Graphics g) {
        if (sprite == null) return;

        GameObj player = game.player();
        double diffX = this.pos.x - player.pos().x;
        double diffY = this.pos.y - player.pos().y;

        int screenCenterX = game.width() / 2;
        int screenCenterY = game.height() / 2;

        int drawX = (int) (screenCenterX + diffX - (width / 2));
        int drawY = (int) (screenCenterY + diffY - (height / 2));

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTrans = g2d.getTransform();

        double centerX = drawX + width / 2.0;
        double centerY = drawY + height / 2.0;

        g2d.rotate(Math.toRadians(angle), centerX, centerY);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (drawX > -width && drawX < game.width() + width &&
                drawY > -height && drawY < game.height() + height) {
            g2d.drawImage(sprite, drawX, drawY, (int) width, (int) height, null);
        }

        g2d.setTransform(oldTrans);
    }
}