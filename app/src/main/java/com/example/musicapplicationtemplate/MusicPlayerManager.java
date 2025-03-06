package com.example.musicapplicationtemplate;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Random;

import model.Song;

public class MusicPlayerManager {
    private static MusicPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private ArrayList<Song> playlist;
    private Song currentSong;
    private boolean isPlaying = false;
    private boolean isShuffle = false;
    private int repeatMode = 0; // 0: No Repeat, 1: Repeat All, 2: Repeat One
    private int currentIndex = 0;
    private OnSeekBarChangeListener seekBarChangeListener;

    private MusicPlayerManager() {
        mediaPlayer = new MediaPlayer();
        playlist = new ArrayList<>();
    }

    public static MusicPlayerManager getInstance() {
        if (instance == null) {
            instance = new MusicPlayerManager();
        }
        return instance;
    }

    public void setPlaylist(ArrayList<Song> songs, int startIndex) {
        this.playlist = songs;
        this.currentIndex = startIndex;
        this.currentSong = songs.get(startIndex);
    }

    public void playSong(Context context, Song song) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            int resId = context.getResources().getIdentifier(
                    song.getFile_path().substring(0, song.getFile_path().lastIndexOf(".")),
                    "raw", context.getPackageName());
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(context, resId);
                mediaPlayer.start();
                currentSong = song;
                isPlaying = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playNext(Context context) {
        if (repeatMode == 2) { // Repeat One
            playSong(context, currentSong);
            return;
        }
        if (isShuffle) {
            currentIndex = new Random().nextInt(playlist.size());
        } else {
            currentIndex++;
            if (currentIndex >= playlist.size()) {
                if (repeatMode == 1) { // Repeat All
                    currentIndex = 0;
                } else {
                    return;
                }
            }
        }
        playSong(context, playlist.get(currentIndex));
    }

    public void playPrevious(Context context) {
        if (isShuffle) {
            currentIndex = new Random().nextInt(playlist.size());
        } else {
            currentIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
        }
        playSong(context, playlist.get(currentIndex));
    }

    public void toggleShuffle() {
        isShuffle = !isShuffle;
    }

    public void toggleRepeat() {
        repeatMode = (repeatMode + 1) % 3;
    }

    public void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
            } else {
                mediaPlayer.start();
                isPlaying = true;
            }
        }
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public int getDuration() {
        return (mediaPlayer != null && currentSong != null) ? mediaPlayer.getDuration() : 0;
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }
    public int getRepeatMode() {
        return repeatMode;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        this.seekBarChangeListener = listener;
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(int currentPosition, int duration);
    }
}
