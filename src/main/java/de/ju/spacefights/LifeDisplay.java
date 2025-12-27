package de.ju.spacefights;

import de.ju.spacefights.lib.AbstractGameObj;
import de.ju.spacefights.lib.Game;
import de.ju.spacefights.lib.Vertex;

import java.awt.*;

public class LifeDisplay extends AbstractGameObj {
    private final Game game;
    private final Font font;

    public LifeDisplay(Game game) {
        super(new Vertex(20, 40), new Vertex(0, 0), 0, 0);
        this.game = game;
        this.font = new Font("Arial", Font.BOLD, 25);
    }

    @Override
    public void paintTo(Graphics g) {
        int lives = ((Player) game.player()).getLives();

        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString("Lives: " + lives, (int) pos.x, (int) pos.y);
    }
}