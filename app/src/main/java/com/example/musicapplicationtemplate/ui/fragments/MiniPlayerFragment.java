package com.example.musicapplicationtemplate.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;

import com.example.musicapplicationtemplate.model.Song;

public class MiniPlayerFragment extends Fragment {

    private ImageView imgMiniPlayer, btnPlayPause;
    private TextView tvMiniTitle, tvMiniArtist;
    private SeekBar seekBarMiniPlayer;
    private MusicPlayerManager musicPlayerManager;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private View miniPlayerView;
    private CardView miniPlayer;

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
        miniPlayer = view.findViewById(R.id.miniPlayer);

        musicPlayerManager = MusicPlayerManager.getInstance();

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

        view.setVisibility(View.GONE);
        updateMiniPlayerUI();

        view.setOnClickListener(v -> openPlayerFragment());

        return view;
    }

    public void updateMiniPlayerUI() {
        Song currentSong = musicPlayerManager.getCurrentSong();
        musicPlayerManager.setOnSongChangedListener(() -> updateMiniPlayerUI());
        if (currentSong != null) {
            miniPlayerView.setVisibility(View.VISIBLE);
            tvMiniTitle.setText(currentSong.getTitle());
            tvMiniArtist.setText(currentSong.getArtist());
            Glide.with(this)
                    .asBitmap()
                    .load("file:///android_asset/" + currentSong.getImage())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imgMiniPlayer.setImageBitmap(resource);
                            setBackgroundFromAlbumArt(resource); // Cập nhật màu nền dựa vào ảnh
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

            syncPlayPauseButton();

            seekBarMiniPlayer.setMax(musicPlayerManager.getDuration());

            if (updateSeekBarRunnable == null) {
                updateSeekBarRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (musicPlayerManager.getCurrentSong() != null) {
                            if (musicPlayerManager.isPlaying()) {
                                int currentPosition = musicPlayerManager.getCurrentPosition();
                                seekBarMiniPlayer.setProgress(currentPosition);
                                handler.postDelayed(this, 1000); // Chỉ cập nhật khi đang phát
                            }
                        }
                    }
                };
                handler.post(updateSeekBarRunnable);
            }

            btnPlayPause.setOnClickListener(v -> togglePlayPause());
        } else {
            if (currentSong != null) {
                miniPlayerView.setVisibility(View.VISIBLE);
            } else {
                miniPlayerView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setBackgroundFromAlbumArt(Bitmap bitmap) {
        if (bitmap != null) {
            Palette.from(bitmap).generate(palette -> {
                if (palette != null) {
                    // Ưu tiên màu tối, nếu không có thì lấy màu trung tính
                    int darkMutedColor = palette.getDarkMutedColor(0);
                    int mutedColor = palette.getMutedColor(0);
                    int darkVibrantColor = palette.getDarkVibrantColor(0);
                    int fallbackColor = getResources().getColor(android.R.color.black); // Màu dự phòng

                    int backgroundColor;
                    if (darkMutedColor != 0) {
                        backgroundColor = darkMutedColor;
                    } else if (mutedColor != 0) {
                        backgroundColor = mutedColor;
                    } else if (darkVibrantColor != 0) {
                        backgroundColor = darkVibrantColor;
                    } else {
                        backgroundColor = fallbackColor;
                    }

                    if (miniPlayer != null) {
                        miniPlayer.setCardBackgroundColor(backgroundColor);
                    }
                }
            });
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
                .setCustomAnimations(R.anim.slide_up, 0)
                .add(R.id.fragment_container, playerFragment)
                .addToBackStack(null) // Lưu trạng thái fragment trước đó
                .commit();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.toggleMiniPlayerVisibility(false); // Ẩn MiniPlayerFragment khi mở PlayerFragment
        mainActivity.toggleBottomNavigationVisibility(false);
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