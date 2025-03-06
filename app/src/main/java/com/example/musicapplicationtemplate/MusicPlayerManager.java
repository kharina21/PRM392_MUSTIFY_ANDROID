package com.example.musicapplicationtemplate;

import android.content.Context;
import android.media.MediaPlayer;

import model.Song;

public class MusicPlayerManager {
    private static MusicPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private boolean isPlaying = false;
    private OnSeekBarChangeListener seekBarChangeListener;

    private MusicPlayerManager() {
        mediaPlayer = new MediaPlayer();
    }
    public int getDuration() {
        if(mediaPlayer != null && currentSong != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    // Phương thức seekTo: cho phép di chuyển đến vị trí mới
    public void seekTo(int position) {
        if(mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public static MusicPlayerManager getInstance() {
        if (instance == null) {
            instance = new MusicPlayerManager();
        }
        return instance;
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

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            isPlaying = false;
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        this.seekBarChangeListener = listener;
    }
    public void updateSeekBar() {
        if (seekBarChangeListener != null && mediaPlayer != null) {
            seekBarChangeListener.onProgressChanged(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
        }
    }
    public interface OnSeekBarChangeListener {
        void onProgressChanged(int currentPosition, int duration);
    }

}