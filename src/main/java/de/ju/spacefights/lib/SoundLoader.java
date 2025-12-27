package de.ju.spacefights.lib;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundLoader {
    public static void playSound(String path) {
        new Thread(() -> {
            try {
                InputStream audioSrc = SoundLoader.class.getResourceAsStream(path);
                if (audioSrc == null) return;

                InputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);

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

    public static Clip playMusic(String path) {
        try {
            InputStream audioSrc = SoundLoader.class.getResourceAsStream(path);
            if (audioSrc == null) return null;

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            return clip;
        } catch (Exception e) {
            return null;
        }
    }
}