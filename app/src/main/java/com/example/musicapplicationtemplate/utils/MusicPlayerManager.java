package com.example.musicapplicationtemplate.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

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
    private final List<OnUIUpdateListener> uiUpdateListeners = new ArrayList<>();
    private OnPlaybackChangeListener playbackChangeListener;
    private OnSeekBarChangeListener seekBarChangeListener;
    private OnSongChangedListener songChangedListener;
    private Handler seekBarHandler = new Handler();

    public MusicPlayerManager() {
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
        if (playlist == null || playlist.isEmpty()) {
            Log.e("MusicPlayerManager", "setPlaylist: Danh sách bài hát rỗng!");
            return;
        }

        this.playlist = playlist;
        this.currentIndex = index;
        this.currentSong = playlist.get(index);
        notifyUIUpdate(currentSong);
    }

    public boolean checkMediaPlayer(){
        if(mediaPlayer != null) return true;
        else return false;
    }

    public void playSong(Context context, Song song) {
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
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
                mediaPlayer.setOnCompletionListener(mp -> handleSongCompletion(context));
                mediaPlayer.start();
                currentSong = song;
                isPlaying = true;
                notifyUIUpdate(currentSong);
                startSeekBarUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSongCompletion(Context context) {
        if (repeatMode == 2) { // Repeat One
            playSong(context, playlist.get(currentIndex));
        } else if (isShuffle) { // Shuffle mode
            currentIndex = new Random().nextInt(playlist.size());
            playSong(context, playlist.get(currentIndex));
        } else { // Next song
            playNext(context);
        }

        if (songChangedListener != null) {
            songChangedListener.onSongChanged();
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
            notifyUIUpdate(currentSong);
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

    public void setShuffle(boolean shuffle) {
        this.isShuffle = shuffle;
    }

    public void setRepeatMode(int mode) {
        this.repeatMode = mode;
    }

    public void playNext(Context context) {
        if (playlist == null || playlist.isEmpty()) {
            Log.e("MusicPlayerManager", "Danh sách bài hát trống! Không thể phát bài tiếp theo.");
            return;
        }

        if (isShuffle) {
            currentIndex = new Random().nextInt(playlist.size());
        } else {
            currentIndex = (currentIndex + 1) % playlist.size();
        }

        playSong(context, playlist.get(currentIndex));
        notifyUIUpdate(currentSong);
    }

    public void playPrevious(Context context) {
        if (playlist == null || playlist.isEmpty()) {
            Log.e("MusicPlayerManager", "Danh sách bài hát trống! Không thể phát bài trước đó.");
            return;
        }

        if (isShuffle) {
            currentIndex = new Random().nextInt(playlist.size());
        } else {
            currentIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
        }

        playSong(context, playlist.get(currentIndex));
        notifyUIUpdate(currentSong);
    }

    public void setOnPlaybackChangeListener(OnPlaybackChangeListener listener) {
        this.playbackChangeListener = listener;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        this.seekBarChangeListener = listener;
    }

    public void setOnSongChangedListener(OnSongChangedListener listener) {
        this.songChangedListener = listener;
    }

    // ✅ Sử dụng danh sách để hỗ trợ nhiều Fragment cập nhật UI
    public void addOnUIUpdateListener(OnUIUpdateListener listener) {
        if (listener != null && !uiUpdateListeners.contains(listener)) {
            uiUpdateListeners.add(listener);
        }
    }

    public void removeOnUIUpdateListener(OnUIUpdateListener listener) {
        uiUpdateListeners.remove(listener);
    }

    private void notifyUIUpdate(Song song) {
        for (OnUIUpdateListener listener : uiUpdateListeners) {
            listener.onUIUpdate(song);
        }
    }

    public interface OnPlaybackChangeListener {
        void onSongComplete();
    }

    public interface OnSeekBarChangeListener {
        void onSeekBarUpdate(int progress);
    }

    public interface OnUIUpdateListener {
        void onUIUpdate(Song song);
    }

    public interface OnSongChangedListener {
        void onSongChanged();
    }

    public void clearCurrentSong() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset(); // Đặt lại MediaPlayer
        }

        playlist.clear();  // Xóa toàn bộ danh sách phát
        currentSong = null; // Không có bài hát nào đang phát
        currentIndex = -1;  // Đặt index về -1 để tránh lỗi truy cập
        isPlaying = false;

        notifyUIUpdate(null); // Cập nhật UI để ẩn thông tin bài hát
    }


}
