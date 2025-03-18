package com.example.musicapplicationtemplate.ui.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.adapter.UnlikedSongAdapter;
import com.example.musicapplicationtemplate.model.Like;
import com.example.musicapplicationtemplate.model.Song;
import com.example.musicapplicationtemplate.model.User;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.utils.UserSession;
import com.example.musicapplicationtemplate.api.ViewModel.LikeViewModel;
import com.example.musicapplicationtemplate.api.ViewModel.SongViewModel;


import java.util.ArrayList;
import java.util.List;

public class AddSongLikeFragment extends Fragment {
    private RecyclerView rvAddSongList;
    private UnlikedSongAdapter adapter;
    private List<Song> songList;
    private ImageView btnAddSongLikeClose;
    private ImageView btnConfirmAddSong;
    private SongViewModel songViewModel;
    private LikeViewModel likeViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songList = new ArrayList<>();
        adapter = new UnlikedSongAdapter(requireContext(), songList);
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        likeViewModel = new ViewModelProvider(this).get(LikeViewModel.class);

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_song_like, container, false);
        getUnlikedSongs();
        rvAddSongList = view.findViewById(R.id.rvAddSongList);
        btnAddSongLikeClose = view.findViewById(R.id.btnAddSongLikeClose);
        btnConfirmAddSong = view.findViewById(R.id.btnConfirmAddSong);
        rvAddSongList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAddSongList.setAdapter(adapter);
        btnAddSongLikeClose.setOnClickListener(v -> toggleAddSongLikeClose());
        btnConfirmAddSong.setVisibility(View.GONE);
        adapter.setOnSelectionChangeListener(count -> {
            if (count > 0) {
                btnConfirmAddSong.setVisibility(View.VISIBLE);
            } else {
                btnConfirmAddSong.setVisibility(View.GONE);
            }
        });
        btnConfirmAddSong.setOnClickListener(v -> addSelectedSongsToLiked());

        return view;
    }

    private void getUnlikedSongs() {
        User user = new UserSession(requireContext()).getUserSession();
        songViewModel.fetchAllSongs();
        songViewModel.getAllSongs().observe(getViewLifecycleOwner(), allSongs->{
            likeViewModel.fetchSongsLikeByUserId(user.getId());
            likeViewModel.getSongsLikeByUserId().observe(getViewLifecycleOwner(), listLiked->{
                List<Song> likedSongs = new ArrayList<>();
                for (Like like : listLiked) likedSongs.add(like.getSong());
                List<Song> songsToShow = new ArrayList<>();
                for (Song song : allSongs) {
                    boolean isLiked = false;
                    for (Song likedSong : likedSongs) {
                        if (likedSong.getSong_id() == song.getSong_id()) {
                            isLiked = true;
                            break;
                        }
                    }
                    if (!isLiked) {
                        songsToShow.add(song);
                    }
                }
                if (songsToShow.isEmpty()) {
                    Toast.makeText(requireContext(), "Tất cả bài hát đã có trong danh sách yêu thích!", Toast.LENGTH_SHORT).show();
                }
                songList.clear();
                songList.addAll(songsToShow);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void addSelectedSongsToLiked() {
        List<Song> selectedSongs = adapter.getSelectedSongs();
        if (!selectedSongs.isEmpty()) {
            for (Song s : selectedSongs) {
                likeViewModel.fetchAddSongToListLike(new UserSession(requireContext()).getUserSession().getId(), s.getSong_id());
                Log.d("ADD SONG","ADD SONG ID: "+s.getSong_id());
            }
            Toast.makeText(requireContext(), "Đã thêm " + selectedSongs.size() + " bài hát vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            // Gửi thông báo cập nhật
            Bundle result = new Bundle();
            result.putBoolean("isUpdated", true);
            getParentFragmentManager().setFragmentResult("updateSongList", result);
            toggleAddSongLikeClose();
        }
    }

    private void toggleAddSongLikeClose() {
        View view = getView();
        if (view != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
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
}
