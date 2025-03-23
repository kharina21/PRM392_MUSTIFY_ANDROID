package com.example.musicapplicationtemplate.ui.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicapplicationtemplate.model.Like;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;
import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.model.Song;
import com.example.musicapplicationtemplate.utils.UserSession;
import com.example.musicapplicationtemplate.api.ViewModel.LikeViewModel;

public class PlayerFragment extends Fragment {
    private ImageView playerImage, playerPlayPause, playerPrevious, playerNext, playerShuffle, playerRepeat, ivPlayerAddSongLike;
    private TextView playerTitle, playerArtist, elapsedTime, remainingTime;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private MusicPlayerManager musicPlayerManager;
    private boolean isShuffle;
    private boolean isSongLiked = false;
    private ImageView btnDown,playerPlaylist;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    private LikeViewModel likeViewModel;
    private int repeatMode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        likeViewModel = new ViewModelProvider(this).get(LikeViewModel.class);

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        playerImage = view.findViewById(R.id.player_image);
        playerTitle = view.findViewById(R.id.player_title);
        playerPlaylist = view.findViewById(R.id.playerPlaylist);
        playerPlaylist.setOnClickListener(v->toggleOpenPlaylist());
        playerArtist = view.findViewById(R.id.player_artist);
        playerPlayPause = view.findViewById(R.id.playerPlayPause);
        playerPlayPause.setOnClickListener(v -> togglePlayPause());
        playerPrevious = view.findViewById(R.id.playerPrevious);
        playerNext = view.findViewById(R.id.playerNext);
        playerShuffle = view.findViewById(R.id.playerShuffle);
        playerRepeat = view.findViewById(R.id.playerRepeat);
        seekBar = view.findViewById(R.id.seekBar);
        ivPlayerAddSongLike = view.findViewById(R.id.ivPlayerAddSongLike);
        elapsedTime = view.findViewById(R.id.elapsedTime);
        remainingTime = view.findViewById(R.id.remainingTime);
        btnDown = view.findViewById(R.id.btnDown);
        btnDown.setOnClickListener(v -> closePlayerFragment());
        musicPlayerManager = MusicPlayerManager.getInstance();
        musicPlayerManager.addOnUIUpdateListener(song -> setupUI());

        //check xem co trong list ua thich cchua
        checkIsLikeSong();
        ivPlayerAddSongLike.setOnClickListener(v->toggleAddAndRemoveSong());

        setupUI();
        setupSeekBar();
        updatePlayPauseButton();

        playerNext.setOnClickListener(v -> {
            musicPlayerManager.playNext(getContext());
            updatePlayPauseButton(); // Cập nhật trạng thái phát/tạm dừng
            setupUI(); // Cập nhật giao diện
        });

        playerPrevious.setOnClickListener(v -> {
            musicPlayerManager.playPrevious(getContext());
            updatePlayPauseButton();
            setupUI();
        });
        playerShuffle.setOnClickListener(v -> toggleShuffle());
        playerRepeat.setOnClickListener(v -> toggleRepeat());

        getParentFragmentManager().setFragmentResultListener("updateSongList", this, (requestKey, bundle) -> {
            boolean isUpdated = bundle.getBoolean("isUpdated", false);
            if (isUpdated) {
                checkIsLikeSong(); // Cập nhật trạng thái thích
            }
        });

        return view;
    }

    private void toggleShuffle() {
        isShuffle = !isShuffle;
        musicPlayerManager.setShuffle(isShuffle);
        Log.d("PlayerFragment", "Shuffle mode: " + isShuffle); // Kiểm tra trạng thái
        playerShuffle.setImageResource(isShuffle ? R.drawable.shuffle : R.drawable.shuffle_off);
    }

    private void toggleOpenPlaylist(){
        AddToPlaylistFragment addToPlaylistFragment = new AddToPlaylistFragment();
        // Đóng gói dữ liệu vào Bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected_song", musicPlayerManager.getCurrentSong()); // Hoặc putParcelable nếu Song là Parcelable
        addToPlaylistFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, 0)
                .add(R.id.fragment_container, addToPlaylistFragment)
                .addToBackStack(null) // Lưu trạng thái fragment trước đó
                .commit();
//        MainActivity mainActivity = (MainActivity) getActivity();
//        mainActivity.toggleMiniPlayerVisibility(false); // Ẩn MiniPlayerFragment khi mở PlayerFragment
//        mainActivity.toggleBottomNavigationVisibility(false);
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

                    ConstraintLayout playerLayout = getView().findViewById(R.id.playerLayout);
                    if (playerLayout != null) {
                        playerLayout.setBackgroundColor(backgroundColor);
                    }
                }
            });
        }
    }

    private void checkIsLikeSong() {
        Song song  = musicPlayerManager.getCurrentSong();
        likeViewModel.fetchSongsLikeByUserId(new UserSession(requireContext()).getUserSession().getId());
        likeViewModel.getSongsLikeByUserId().observe(getViewLifecycleOwner(), listLike ->{
            for(Like like : listLike) {
                if(like.getSong().getSong_id() == song.getSong_id()){
                    ivPlayerAddSongLike.setImageResource(R.drawable.ic_checkcircle);
                    isSongLiked = true;
                    return;
                }
            }
            ivPlayerAddSongLike.setImageResource(R.drawable.ic_addcircle);
            isSongLiked = false;
        });
    }

    private void toggleAddAndRemoveSong(){
        Song song = musicPlayerManager.getCurrentSong();

        if(isSongLiked){
            ivPlayerAddSongLike.setImageResource(R.drawable.ic_addcircle);
            likeViewModel.fetchDeleteSongInListLike(new UserSession(requireContext()).getUserSession().getId(),song.getSong_id());
            Toast.makeText(requireContext(), "Đã xóa \""+song.getTitle()+"\" khỏi danh sách ưa thích", Toast.LENGTH_SHORT).show();
            isSongLiked = false;
        }else{
            ivPlayerAddSongLike.setImageResource(R.drawable.ic_checkcircle);
            likeViewModel.fetchAddSongToListLike(new UserSession(requireContext()).getUserSession().getId(),song.getSong_id());
            Toast.makeText(requireContext(), "Đã thêm \""+song.getTitle()+"\" vào danh sách ưa thích", Toast.LENGTH_SHORT).show();
            isSongLiked = true;
        }
        // Gửi thông báo cập nhật
        Bundle result = new Bundle();
        result.putBoolean("isUpdated", true);
        getParentFragmentManager().setFragmentResult("updateSongList", result);
    }

    private void closePlayerFragment() {
        View view = getView();
        if (view != null) {
            // Ẩn MiniPlayer và BottomNavigation trước khi chạy animation
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.toggleMiniPlayerVisibility(false);
                mainActivity.toggleBottomNavigationVisibility(false);
            }

            // Tạo Animation trượt xuống
            ObjectAnimator slideDown = ObjectAnimator.ofFloat(view, "translationY", 0, view.getHeight());
            slideDown.setDuration(300);
            slideDown.setInterpolator(new AccelerateInterpolator());

            slideDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // Khi animation kết thúc, xóa Fragment ngay lập tức
                    view.postOnAnimation(() -> {
                        if (mainActivity != null) {
                            mainActivity.getSupportFragmentManager().popBackStack();
                            // Hiển thị lại MiniPlayer & BottomNavigation sau khi Fragment bị loại bỏ
                            mainActivity.toggleMiniPlayerVisibility(true);
                            mainActivity.toggleBottomNavigationVisibility(true);
                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            slideDown.start();
        }
    }


    private void setupUI() {
        Song song = musicPlayerManager.getCurrentSong();
        musicPlayerManager.setOnSongChangedListener(() -> setupUI());
        if (isAdded() && getActivity() != null) {
            if (song != null) {
                playerTitle.setText(song.getTitle());
                playerArtist.setText(song.getArtist());
                Glide.with(this)
                        .asBitmap()
                        .load("file:///android_asset/" + song.getImage())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                playerImage.setImageBitmap(resource);
                                setBackgroundFromAlbumArt(resource); // Cập nhật màu nền dựa vào ảnh
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
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
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void updatePlayPauseButton() {
        playerPlayPause.setImageResource(musicPlayerManager.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
    }

    private void togglePlayPause() {
        musicPlayerManager.togglePlayPause();
        updatePlayPauseButton();
    }


    private void toggleRepeat() {
        repeatMode = (repeatMode + 1) % 3;
        musicPlayerManager.setRepeatMode(repeatMode);
        Log.d("PlayerFragment", "Repeat mode: " + repeatMode); // Kiểm tra trạng thái

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
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        musicPlayerManager.removeOnUIUpdateListener(song -> setupUI());
    }
}