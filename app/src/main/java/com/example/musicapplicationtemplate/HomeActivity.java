package com.example.musicapplicationtemplate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import adapter.SongAdapter;
import model.Song;
import sqlserver.SongDAO;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvSongs;
    private View miniPlayer;
    private ImageView imgMiniPlayer, btnPlayPause;
    private TextView tvMiniTitle, tvMiniArtist;
    private SeekBar seekBarMiniPlayer;
    private MusicPlayerManager musicPlayerManager;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Khởi tạo UI từ layout (rvSongs, miniPlayer, …)
        rvSongs = findViewById(R.id.rvSongs);
        miniPlayer = findViewById(R.id.miniPlayer);
        imgMiniPlayer = findViewById(R.id.imgMiniPlayer);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        tvMiniTitle = findViewById(R.id.tvMiniTitle);
        tvMiniArtist = findViewById(R.id.tvMiniArtist);
        seekBarMiniPlayer = findViewById(R.id.seekBarMiniPlayer);

        musicPlayerManager = MusicPlayerManager.getInstance();

        // Xử lý nút Play/Pause và click miniPlayer (mở PlayerActivity)
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        miniPlayer.setOnClickListener(v -> openPlayerActivity());

        // Load dữ liệu cho RecyclerView (rvSongs) không thay đổi
        loadSongs();

        // Thiết lập listener cho SeekBar của miniPlayer
        seekBarMiniPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && musicPlayerManager.getCurrentSong() != null) {
                    musicPlayerManager.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Runnable cập nhật SeekBar theo thời gian chạy của bài hát (mỗi giây)
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (musicPlayerManager.getCurrentSong() != null && musicPlayerManager.isPlaying()) {
                    int currentPosition = musicPlayerManager.getCurrentPosition();
                    seekBarMiniPlayer.setProgress(currentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateSeekBarRunnable);
    }

    private void loadSongs() {
        SongDAO songDAO = new SongDAO();
        List<Song> songs = songDAO.getAllSongs();
        SongAdapter songAdapter = new SongAdapter(this, songs, this::playSong);
        rvSongs.setAdapter(songAdapter);
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
    }

    private void playSong(Song song) {
        musicPlayerManager.playSong(this, song);
        updateMiniPlayer();
    }

    private void updateMiniPlayer() {
        Song currentSong = musicPlayerManager.getCurrentSong();
        if (currentSong != null) {
            miniPlayer.setVisibility(View.VISIBLE);
            tvMiniTitle.setText(currentSong.getTitle());
            tvMiniArtist.setText(currentSong.getArtist());
            Glide.with(this)
                    .load("file:///android_asset/" + currentSong.getImage())
                    .into(imgMiniPlayer);
            btnPlayPause.setImageResource(musicPlayerManager.isPlaying()
                    ? android.R.drawable.ic_media_pause
                    : android.R.drawable.ic_media_play);
            // Đặt giá trị tối đa cho SeekBar dựa trên thời lượng của bài hát
            seekBarMiniPlayer.setMax(musicPlayerManager.getDuration());
        } else {
            miniPlayer.setVisibility(View.GONE);
        }
    }

    private void togglePlayPause() {
        musicPlayerManager.togglePlayPause();
        btnPlayPause.setImageResource(musicPlayerManager.isPlaying()
                ? android.R.drawable.ic_media_pause
                : android.R.drawable.ic_media_play);
    }

    private void openPlayerActivity() {
        Song currentSong = musicPlayerManager.getCurrentSong();
        if (currentSong != null) {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("song", currentSong);
            intent.putExtra("current_position", musicPlayerManager.getCurrentPosition());
            intent.putExtra("is_playing", musicPlayerManager.isPlaying());
            startActivity(intent);
            overridePendingTransition(0, 0); // Loại bỏ animation chuyển cảnh
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMiniPlayer(); // Cập nhật lại miniPlayer khi quay lại Activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBarRunnable); // Ngừng cập nhật SeekBar khi Activity bị huỷ
    }
}
