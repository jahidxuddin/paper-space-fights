package de.ju.spacefights;

import de.ju.spacefights.lib.AbstractGameObj;
import de.ju.spacefights.lib.Vertex;

import java.awt.*;

public class ScoreDisplay extends AbstractGameObj {
    private int score = 0;
    private final Font font;

    public ScoreDisplay() {
        super(new Vertex(20, 70), new Vertex(0, 0), 0, 0);
        this.font = new Font("Arial", Font.BOLD, 25);
    }

    public void addScore(int points) {
        this.score += points;
    }

    @Override
    public void paintTo(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString("Score: " + score, (int) pos.x, (int) pos.y);
    }
}