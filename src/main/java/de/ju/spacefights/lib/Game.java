package de.ju.spacefights.lib;

 import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialDeepOceanIJTheme;

import javax.swing.*;
import java.util.List;
import java.awt.event.*;
import java.awt.*;

public interface Game {
    default String title() {
        return "";
    }

    int width();

    int height();

    GameObj player();

    List<List<? extends GameObj>> goss();

    void init();


    void doChecks();

    void keyPressedReaction(KeyEvent keyEvent);

    void keyReleasedReaction(KeyEvent keyEvent);

    void onMouseClicked(MouseEvent e);

    void onMouseMoved(MouseEvent e);

    default void move() {
        if (ended()) return;
        for (var gos : goss()) gos.forEach(GameObj::move);
        player().move();
    }

    boolean won();

    boolean lost();

    default boolean ended() {
        return won() || lost();
    }

    default void paintTo(Graphics g) {
        for (var gos : goss()) gos.forEach(go -> go.paintTo(g));
        player().paintTo(g);
    }

    default void play() {
        FlatMTMaterialDeepOceanIJTheme.setup();
        init();
        var f = new javax.swing.JFrame();
        f.setTitle(title());
        java.awt.image.BufferedImage logo = SpriteLoader.load("/assets/ship_L.png");
        if (logo != null) {
            f.setIconImage(logo);
        } else {
            System.err.println("Konnte Fenster-Logo nicht laden: /assets/ship_L.png");
        }
        f.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        f.add(new SwingScreen(this));
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}

