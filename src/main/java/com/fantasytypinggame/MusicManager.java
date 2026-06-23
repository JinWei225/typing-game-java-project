package com.fantasytypinggame;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicManager {

    private MediaPlayer menuMusicPlayer;

    public void playMenuMusic() {
        if (menuMusicPlayer == null) {
            Media menuMusic = new Media(
                getClass()
                    .getResource("/assets/menu_music.mp3")
                    .toExternalForm()
            );

            menuMusicPlayer = new MediaPlayer(menuMusic);
            menuMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            menuMusicPlayer.setVolume(0.20);
        }

        menuMusicPlayer.play();
    }

    public void stopMenuMusic() {
        if (menuMusicPlayer != null) {
            menuMusicPlayer.stop();
        }
    }
}
