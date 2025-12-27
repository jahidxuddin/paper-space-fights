package de.ju.spacefights.lib;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundLoader {
    public static void playSound(String path) {
        new Thread(() -> {
            try {
                URL url = SoundLoader.class.getResource(path);
                if (url == null) {
                    System.err.println("Sound nicht gefunden: " + path);
                    return;
                }

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }).start();
    }
}