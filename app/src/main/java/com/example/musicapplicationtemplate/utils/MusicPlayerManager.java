package com.example.musicapplicationtemplate.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.example.musicapplicationtemplate.model.Song;

public class MusicPlayerManager {
    private static MusicPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private List<Song> playlist;
    private Song currentSong;
    private int currentIndex;
    private boolean isPlaying;
    private boolean isShuffle;
    private int repeatMode;
    private OnPlaybackChangeListener playbackChangeListener;
    private OnSeekBarChangeListener seekBarChangeListener;
    private OnUIUpdateListener uiUpdateListener;
    private Handler seekBarHandler = new Handler();

    private MusicPlayerManager() {
        mediaPlayer = new MediaPlayer();
        playlist = new ArrayList<>();
        currentIndex = 0;
        isPlaying = false;
        isShuffle = false;
        repeatMode = 0;
    }

    public static MusicPlayerManager getInstance() {
        if (instance == null) {
            instance = new MusicPlayerManager();
        }
        return instance;
    }

    public void setPlaylist(List<Song> playlist, int index) {
        this.playlist = playlist;
        this.currentIndex = index;
        this.currentSong = playlist.isEmpty() ? null : playlist.get(index);
        if (uiUpdateListener != null) {
            uiUpdateListener.onUIUpdate(currentSong);
        }
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
                mediaPlayer.setOnCompletionListener(mp -> {
                    if (playbackChangeListener != null) {
                        playbackChangeListener.onSongComplete();
                    }
                });
                mediaPlayer.start();
                currentSong = song;
                isPlaying = true;
                if (uiUpdateListener != null) {
                    uiUpdateListener.onUIUpdate(currentSong);
                }
                startSeekBarUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSeekBarUpdate() {
        seekBarHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.onSeekBarUpdate(getCurrentPosition());
                }
                seekBarHandler.postDelayed(this, 500);
            }
        }, 500);
    }

    public void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
            } else {
                mediaPlayer.start();
                isPlaying = true;
                startSeekBarUpdate();
            }
        }
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return (mediaPlayer != null && currentSong != null) ? mediaPlayer.getDuration() : 0;
    }
    public void toggleShuffle() {
        isShuffle = !isShuffle;
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
    public void setOnPlaybackChangeListener(OnPlaybackChangeListener listener) {
        this.playbackChangeListener = listener;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        this.seekBarChangeListener = listener;
    }

    public void setOnUIUpdateListener(OnUIUpdateListener listener) {
        this.uiUpdateListener = listener;
    }

    public interface OnPlaybackChangeListener {
        void onSongComplete();
    }
    public void toggleRepeat() {
        repeatMode = (repeatMode + 1) % 3;
    }
    public interface OnSeekBarChangeListener {
        void onSeekBarUpdate(int progress);
    }

    public interface OnUIUpdateListener {
        void onUIUpdate(Song song);
    }
}