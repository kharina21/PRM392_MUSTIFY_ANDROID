package com.example.musicapplicationtemplate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import model.Song;

public class PlayerActivity extends AppCompatActivity {
    private ImageView playerImage, playerPlayPause, playerPrevious, playerNext;
    private TextView playerTitle, playerArtist;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private MusicPlayerManager musicPlayerManager;
    private Song currentSong;
    private boolean isPlaying;

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(MusicService.ACTION_UPDATE_SEEKBAR.equals(intent.getAction())){
                int currentPosition = intent.getIntExtra(MusicService.EXTRA_CURRENT_POSITION, 0);
                seekBar.setProgress(currentPosition);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Khởi tạo UI
        playerImage = findViewById(R.id.player_image);
        playerTitle = findViewById(R.id.player_title);
        playerArtist = findViewById(R.id.player_artist);
        playerPlayPause = findViewById(R.id.player_play_pause);
        playerPrevious = findViewById(R.id.player_previous);
        playerNext = findViewById(R.id.player_next);
        seekBar = findViewById(R.id.seekBarMiniPlayer);
        musicPlayerManager = MusicPlayerManager.getInstance();

        setupSeekBar();

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("song")) {
            currentSong = (Song) intent.getSerializableExtra("song");
            int currentPosition = intent.getIntExtra("current_position", 0);
            isPlaying = intent.getBooleanExtra("is_playing", false);

            setupUI(currentSong);
            continuePlaying(currentPosition, isPlaying);
        }

        // Xử lý sự kiện
        playerPlayPause.setOnClickListener(v -> togglePlayPause());
        playerPrevious.setOnClickListener(v -> playPreviousSong());
        playerNext.setOnClickListener(v -> playNextSong());
    }

    private void setupUI(Song song) {
        if (song != null) {
            playerTitle.setText(song.getTitle());
            playerArtist.setText(song.getArtist());
            Glide.with(this).load("file:///android_asset/" + song.getImage()).dontAnimate().into(playerImage);
        }
    }

    private void continuePlaying(int position, boolean playing) {
        if (musicPlayerManager.getMediaPlayer() != null) {
            seekBar.setMax(musicPlayerManager.getMediaPlayer().getDuration());
            seekBar.setProgress(position);
            updatePlayPauseButton();
            setupSeekBar();
        }
    }

    private void updatePlayPauseButton() {
        playerPlayPause.setImageResource(musicPlayerManager.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
    }

    private void setupSeekBar() {
        seekBar.setMax(musicPlayerManager.getMediaPlayer().getDuration());
        Runnable updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (musicPlayerManager.getMediaPlayer() != null) {
                    seekBar.setProgress(musicPlayerManager.getCurrentPosition());
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.post(updateSeekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicPlayerManager.getMediaPlayer().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void togglePlayPause() {
        musicPlayerManager.togglePlayPause();
        updatePlayPauseButton();
    }

    private void playPreviousSong() {
        // TODO: Thêm logic chuyển bài trước (yêu cầu danh sách bài hát)
    }

    private void playNextSong() {
        // TODO: Thêm logic chuyển bài tiếp (yêu cầu danh sách bài hát)
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(updateReceiver, new IntentFilter(MusicService.ACTION_UPDATE_SEEKBAR), Context.RECEIVER_NOT_EXPORTED);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}