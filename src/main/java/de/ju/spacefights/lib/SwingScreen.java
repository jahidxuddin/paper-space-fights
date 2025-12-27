package de.ju.spacefights.lib;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.Serial;

public class SwingScreen extends JPanel {
    @Serial
    private static final long serialVersionUID = 1403492898373497054L;
    Game logic;
    Timer t;

    public SwingScreen(Game gl) {
        this.logic = gl;

        t = new Timer(13, (ev) -> {
            logic.move();
            logic.doChecks();
            repaint();
            getToolkit().sync();
            requestFocus();
        });
        t.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                logic.keyPressedReaction(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                logic.keyReleasedReaction(e);
            }
        });

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logic.onMouseClicked(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                logic.onMouseMoved(e);
            }
        };

        addMouseListener(ma);

        addMouseMotionListener(ma);

        setFocusable(true);
        requestFocus();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(logic.width(), logic.height());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        logic.paintTo(g);
    }
}