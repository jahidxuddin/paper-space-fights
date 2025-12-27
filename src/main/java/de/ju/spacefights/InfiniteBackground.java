package de.ju.spacefights;

import de.ju.spacefights.lib.AbstractGameObj;
import de.ju.spacefights.lib.Game;
import de.ju.spacefights.lib.SpriteLoader;
import de.ju.spacefights.lib.Vertex;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class InfiniteBackground extends AbstractGameObj {
    private final Game game;
    private final BufferedImage image;
    private final Player player;
    private final int imgWidth;
    private final int imgHeight;

    public InfiniteBackground(Game game, Player player, String imagePath) {
        super(new Vertex(0, 0), new Vertex(0, 0), 0, 0);
        this.game = game;
        this.player = player;
        this.image = SpriteLoader.load(imagePath);

        if (this.image != null) {
            this.imgWidth = image.getWidth();
            this.imgHeight = image.getHeight();
        } else {
            this.imgWidth = 100;
            this.imgHeight = 100;
            System.err.println("Fehler: Hintergrundbild konnte nicht geladen werden: " + imagePath);
        }
    }

    @Override
    public void paintTo(Graphics g) {
        if (image == null) return;

        int currentWidth = game.width();
        int currentHeight = game.height();

        double camX = player.pos().x - (currentWidth / 2.0);
        double camY = player.pos().y - (currentHeight / 2.0);

        int xOffset = (int) (camX % imgWidth);
        int yOffset = (int) (camY % imgHeight);

        if (xOffset < 0) xOffset += imgWidth;
        if (yOffset < 0) yOffset += imgHeight;

        for (int x = -1; x <= 2; x++) {
            for (int y = -1; y <= 2; y++) {
                g.drawImage(image,
                        (x * imgWidth) - xOffset,
                        (y * imgHeight) - yOffset,
                        null);
            }
        }
    }
}