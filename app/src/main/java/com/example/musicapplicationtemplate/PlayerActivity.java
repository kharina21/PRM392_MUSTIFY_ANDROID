package com.example.musicapplicationtemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import model.Song;

public class PlayerActivity extends AppCompatActivity {
    private ImageView playerImage, playerPlayPause, playerPrevious, playerNext, playerShuffle, playerRepeat;
    private TextView playerTitle, playerArtist, elapsedTime, remainingTime;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private MusicPlayerManager musicPlayerManager;
    private boolean isShuffle;
    private int repeatMode;
    private ArrayList<Song> playlist;
    private int currentIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playerImage = findViewById(R.id.player_image);
        playerTitle = findViewById(R.id.player_title);
        playerArtist = findViewById(R.id.player_artist);
        playerPlayPause = findViewById(R.id.playerPlayPause);
        playerPrevious = findViewById(R.id.playerPrevious);
        playerNext = findViewById(R.id.playerNext);
        playerShuffle = findViewById(R.id.playerShuffle);
        playerRepeat = findViewById(R.id.playerRepeat);
        seekBar = findViewById(R.id.seekBar);
        elapsedTime = findViewById(R.id.elapsedTime);
        remainingTime = findViewById(R.id.remainingTime);

        musicPlayerManager = MusicPlayerManager.getInstance();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("playlist")) {
            playlist = (ArrayList<Song>) intent.getSerializableExtra("playlist");
            currentIndex = intent.getIntExtra("current_index", 0);
            musicPlayerManager.setPlaylist(playlist, currentIndex);
            musicPlayerManager.playSong(this, playlist.get(currentIndex));
        }

        setupUI();
        setupSeekBar();
        updatePlayPauseButton();

        playerPlayPause.setOnClickListener(v -> togglePlayPause());
        playerPrevious.setOnClickListener(v -> {
            musicPlayerManager.playPrevious(this);
            setupUI();
        });
        playerNext.setOnClickListener(v -> {
            musicPlayerManager.playNext(this);
            setupUI();
        });
        playerShuffle.setOnClickListener(v -> toggleShuffle());
        playerRepeat.setOnClickListener(v -> toggleRepeat());
    }

    private void setupUI() {
        Song song = musicPlayerManager.getCurrentSong();
        if (song != null) {
            playerTitle.setText(song.getTitle());
            playerArtist.setText(song.getArtist());
            Glide.with(this).load("file:///android_asset/" + song.getImage()).dontAnimate().into(playerImage);
        }
    }

    private void setupSeekBar() {
        seekBar.setMax(musicPlayerManager.getDuration());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentPosition = musicPlayerManager.getCurrentPosition();
                int totalDuration = musicPlayerManager.getDuration();
                seekBar.setProgress(currentPosition);
                elapsedTime.setText(formatTime(currentPosition));
                remainingTime.setText(formatTime(totalDuration - currentPosition));
                handler.postDelayed(this, 500);
            }
        }, 500);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicPlayerManager.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    private void updatePlayPauseButton() {
        playerPlayPause.setImageResource(musicPlayerManager.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
    }

    private void togglePlayPause() {
        musicPlayerManager.togglePlayPause();
        updatePlayPauseButton();
    }

    private void toggleShuffle() {
        isShuffle = !isShuffle;
        musicPlayerManager.toggleShuffle();
        playerShuffle.setImageResource(isShuffle ? R.drawable.shuffle : R.drawable.shuffle_off);
    }

    private void toggleRepeat() {
        musicPlayerManager.toggleRepeat();
//        repeatMode = musicPlayerManager.getRepeatMode();
        switch (repeatMode) {
            case 0:
                playerRepeat.setImageResource(R.drawable.repeat_off);
                break;
            case 1:
                playerRepeat.setImageResource(R.drawable.repeat);
                break;
            case 2:
                playerRepeat.setImageResource(R.drawable.repeat_1);
                break;
        }
    }



    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}