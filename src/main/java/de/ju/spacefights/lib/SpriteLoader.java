package de.ju.spacefights.lib;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class SpriteLoader {
    public static BufferedImage load(String path) {
        BufferedImage image;
        try {
            image = ImageIO.read(Objects.requireNonNull(SpriteLoader.class.getResource(path)));
            return image;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load image: " + path, e);
        }
    }
}
