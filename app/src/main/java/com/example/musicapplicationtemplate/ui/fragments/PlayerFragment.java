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
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;
import com.example.musicapplicationtemplate.R;

import model.Song;

public class PlayerFragment extends Fragment {
    private ImageView playerImage, playerPlayPause, playerPrevious, playerNext, playerShuffle, playerRepeat;
    private TextView playerTitle, playerArtist, elapsedTime, remainingTime;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private MusicPlayerManager musicPlayerManager;
    private boolean isShuffle;
    private ImageView btnDown;
    private MiniPlayerFragment miniPlayerFragment;
    private int repeatMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        playerImage = view.findViewById(R.id.player_image);
        playerTitle = view.findViewById(R.id.player_title);
        playerArtist = view.findViewById(R.id.player_artist);
        playerPlayPause = view.findViewById(R.id.playerPlayPause);
        playerPrevious = view.findViewById(R.id.playerPrevious);
        playerNext = view.findViewById(R.id.playerNext);
        playerShuffle = view.findViewById(R.id.playerShuffle);
        playerRepeat = view.findViewById(R.id.playerRepeat);
        seekBar = view.findViewById(R.id.seekBar);
        elapsedTime = view.findViewById(R.id.elapsedTime);
        remainingTime = view.findViewById(R.id.remainingTime);
        btnDown = view.findViewById(R.id.btnDown);
        miniPlayerFragment = new MiniPlayerFragment();

        btnDown.setOnClickListener(v -> closePlayerFragment());
        musicPlayerManager = MusicPlayerManager.getInstance();

        setupUI();
        setupSeekBar();
        updatePlayPauseButton();

        playerPlayPause.setOnClickListener(v -> togglePlayPause());
        playerPrevious.setOnClickListener(v -> {
            musicPlayerManager.playPrevious(getContext());
            setupUI();
        });
        playerNext.setOnClickListener(v -> {
            musicPlayerManager.playNext(getContext());
            setupUI();
        });
        playerShuffle.setOnClickListener(v -> toggleShuffle());
        playerRepeat.setOnClickListener(v -> toggleRepeat());

        return view;
    }
    private void closePlayerFragment() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.toggleMiniPlayerVisibility(true); // Hiển thị MiniPlayerFragment
        }
        getParentFragmentManager().popBackStack(); // Quay lại fragment trước đó
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
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}