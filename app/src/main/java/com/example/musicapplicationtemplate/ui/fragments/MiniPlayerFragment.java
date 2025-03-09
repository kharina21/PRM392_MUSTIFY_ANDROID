package com.example.musicapplicationtemplate.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;

import model.Song;

public class MiniPlayerFragment extends Fragment {

    private ImageView imgMiniPlayer, btnPlayPause;
    private TextView tvMiniTitle, tvMiniArtist;
    private SeekBar seekBarMiniPlayer;
    private MusicPlayerManager musicPlayerManager;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private View miniPlayerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mini_player, container, false);

        miniPlayerView = view;
        view.setOnClickListener(v -> openPlayerFragment());
        imgMiniPlayer = view.findViewById(R.id.imgMiniPlayer);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        tvMiniTitle = view.findViewById(R.id.tvMiniTitle);
        tvMiniArtist = view.findViewById(R.id.tvMiniArtist);
        seekBarMiniPlayer = view.findViewById(R.id.seekBarMiniPlayer);

        musicPlayerManager = MusicPlayerManager.getInstance();

        seekBarMiniPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && musicPlayerManager.getCurrentSong() != null) {
                    musicPlayerManager.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        view.setVisibility(View.GONE);
        updateMiniPlayerUI();

        view.setOnClickListener(v -> openPlayerFragment());

        return view;
    }

    public void updateMiniPlayerUI() {
        Song currentSong = musicPlayerManager.getCurrentSong();
        if (currentSong != null) {
            miniPlayerView.setVisibility(View.VISIBLE);
            tvMiniTitle.setText(currentSong.getTitle());
            tvMiniArtist.setText(currentSong.getArtist());
            Glide.with(this).load("file:///android_asset/" + currentSong.getImage()).into(imgMiniPlayer);

            syncPlayPauseButton();

            seekBarMiniPlayer.setMax(musicPlayerManager.getDuration());

            if (updateSeekBarRunnable == null) {
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

            btnPlayPause.setOnClickListener(v -> togglePlayPause());
        } else {
            miniPlayerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (updateSeekBarRunnable != null) {
            handler.removeCallbacks(updateSeekBarRunnable);
            updateSeekBarRunnable = null;
        }
    }

    private void togglePlayPause() {
        musicPlayerManager.togglePlayPause();
        syncPlayPauseButton();
    }

    private void syncPlayPauseButton() {
        if (btnPlayPause != null) {
            btnPlayPause.setImageResource(musicPlayerManager.isPlaying()
                    ? android.R.drawable.ic_media_pause
                    : android.R.drawable.ic_media_play);
        }
    }

    private void openPlayerFragment() {
        PlayerFragment playerFragment = new PlayerFragment();
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.fade_out)
                .replace(R.id.fragment_container, playerFragment)
                .addToBackStack(null) // Lưu trạng thái fragment trước đó
                .commit();

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.toggleMiniPlayerVisibility(false); // Ẩn MiniPlayerFragment khi mở PlayerFragment
        }
    }

    public void updateSeekBar(int progress) {
        if (seekBarMiniPlayer != null) {
            seekBarMiniPlayer.setProgress(progress);
        }
    }

    public void setSeekBarMax(int max) {
        if (seekBarMiniPlayer != null) {
            seekBarMiniPlayer.setProgress(0);
            seekBarMiniPlayer.setMax(max);
        }
    }
}