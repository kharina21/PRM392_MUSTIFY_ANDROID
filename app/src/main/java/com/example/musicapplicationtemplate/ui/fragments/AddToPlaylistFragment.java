package com.example.musicapplicationtemplate.ui.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.adapter.PlaylistAdapter;
import com.example.musicapplicationtemplate.api.ViewModel.PlaylistViewModel;
import com.example.musicapplicationtemplate.api.ViewModel.SongViewModel;
import com.example.musicapplicationtemplate.model.Playlist;
import com.example.musicapplicationtemplate.model.Song;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;
import com.example.musicapplicationtemplate.utils.UserSession;

import java.util.HashMap;
import java.util.List;


public class AddToPlaylistFragment extends Fragment {
    private TextView tvAddToPlaylistCancel;
    private LinearLayout addToSongLike, addToSongLikeRelated;
    private Button btnCreatePlaylistDone,btnNewPlaylist;
    private CheckBox addSongToLikes, addSongToLikesRelated;

    private MusicPlayerManager musicPlayerManager;
    private ListView listPlaylistSaved, listRelatedPlaylist;
    private SongViewModel songViewModel;
    private PlaylistViewModel playlistViewModel;
    private UserSession userSession;

    private boolean isLikedTemp = false;
    private boolean isRelatedTemp = false;
    private boolean initialLikedStatus = false;
    private boolean initialRelatedStatus = false;
    private Song selectedSong = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSession = new UserSession(requireContext());
        musicPlayerManager = MusicPlayerManager.getInstance();
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_to_playlist, container, false);

        btnCreatePlaylistDone = view.findViewById(R.id.btnCreatePlaylistDone);
        tvAddToPlaylistCancel = view.findViewById(R.id.tvAddToPlaylistCancel);
        addToSongLike = view.findViewById(R.id.addToSongLike);
        addToSongLikeRelated = view.findViewById(R.id.addToSongLikeRelated);
        addSongToLikes = view.findViewById(R.id.addSongToLikes);
        addSongToLikesRelated = view.findViewById(R.id.addSongToLikesRelated);

        btnNewPlaylist = view.findViewById(R.id.btnNewPlaylist);
        btnNewPlaylist.setOnClickListener(v->openCreatePlaylistDialog());

        listPlaylistSaved = view.findViewById(R.id.listPlaylistSaved);
        listRelatedPlaylist = view.findViewById(R.id.listRelatedPlaylist);

        btnCreatePlaylistDone.setOnClickListener(v -> togglePlaylistDone());
        tvAddToPlaylistCancel.setOnClickListener(v -> toggleAddPlaylistCancel());

        if (getArguments() != null) {
            selectedSong = (Song) getArguments().getSerializable("selected_song");
        }

        if (selectedSong != null) {
            songViewModel.fetchSongLikeByUserIdAndSongId(userSession.getUserSession().getId(), selectedSong.getSong_id());
            songViewModel.getSongLikeByUserIdAndSongId().observe(getViewLifecycleOwner(), song -> {
                isLikedTemp = (song != null);
                updateUIBasedOnSongLikeStatus(isLikedTemp);
            });

            // Ngăn chặn vòng lặp vô hạn do `setChecked()`
            addSongToLikes.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) { // Chỉ xử lý khi người dùng nhấn
                    isLikedTemp = isChecked;
                    updateUIBasedOnSongLikeStatus(isChecked);
                }
            });

            addSongToLikesRelated.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) {
                    isRelatedTemp = isChecked;
                    updateUIBasedOnSongRelatedStatus(isChecked);
                }
            });

            Bundle result = new Bundle();
            result.putBoolean("isUpdated", true);
            getParentFragmentManager().setFragmentResult("updateSongList", result);
        }

        //list view contain song
        playlistViewModel.fetchListPlaylistContainSong(userSession.getUserSession().getId(), selectedSong.getSong_id());
        playlistViewModel.getListPlaylistContainSong().observe(getViewLifecycleOwner(), list -> {
            if (!list.isEmpty()) {
                PlaylistAdapter adapter = new PlaylistAdapter(requireContext(), list, true);
                listPlaylistSaved.setAdapter(adapter);
            }else{
                listPlaylistSaved.setVisibility(View.GONE);
            }
        });


        //líst view not contain song
        playlistViewModel.fetchListPlaylistNotContainSong(userSession.getUserSession().getId(), selectedSong.getSong_id());
        playlistViewModel.getListPlaylistNotContainSong().observe(getViewLifecycleOwner(), list -> {
            if (!list.isEmpty()) {
                PlaylistAdapter adapter = new PlaylistAdapter(requireContext(), list, false);
                listRelatedPlaylist.setAdapter(adapter);
            }else{
                listRelatedPlaylist.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private void updateUIBasedOnSongLikeStatus(boolean isLiked) {
        addSongToLikes.setChecked(isLiked);
        addToSongLike.setVisibility(isLiked ? View.VISIBLE : View.GONE);
        addToSongLikeRelated.setVisibility(isLiked ? View.GONE : View.VISIBLE);
    }

    private void updateUIBasedOnSongRelatedStatus(boolean isRelated) {
        addSongToLikesRelated.setChecked(isRelated);
        // Có thể thêm xử lý UI khác nếu cần
    }

    private void openCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Tạo Playlist Mới");

        // Tạo EditText để nhập tên playlist
        final EditText input = new EditText(requireContext());
        input.setHint("Nhập tên playlist");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String playlistName = input.getText().toString().trim();
            if (!playlistName.isEmpty()) {
                createNewPlaylist(playlistName);
            } else {
                Toast.makeText(requireContext(), "Tên playlist không được để trống!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void createNewPlaylist(String playlistName) {
        playlistViewModel.createPlaylist(userSession.getUserSession().getId(), playlistName);
        playlistViewModel.getIsAdd().observe(getViewLifecycleOwner(), playlist -> {
            if (playlist == true) {
                Toast.makeText(requireContext(), "Playlist " + playlistName + " đã được tạo!", Toast.LENGTH_SHORT).show();
                refreshPlaylists();
            }
        });
    }

    private void refreshPlaylists() {
        playlistViewModel.fetchListPlaylistContainSong(userSession.getUserSession().getId(), selectedSong.getSong_id());
        playlistViewModel.fetchListPlaylistNotContainSong(userSession.getUserSession().getId(), selectedSong.getSong_id());
    }
    private void togglePlaylistDone() {
        if (selectedSong == null) return;

        // Lấy adapter từ listPlaylistSaved
        PlaylistAdapter savedAdapter = (PlaylistAdapter) listPlaylistSaved.getAdapter();
        if (savedAdapter != null) {
            HashMap<Integer, Boolean> savedSelection = savedAdapter.getSelectedItems();
            for (Integer playlistId : savedSelection.keySet()) {
                if (!savedSelection.get(playlistId)) { // Nếu bị uncheck => Xóa khỏi playlist
                    playlistViewModel.deleteSongToPlaylist(playlistId, selectedSong.getSong_id());
                }
            }
        }

        // Lấy adapter từ listRelatedPlaylist
        PlaylistAdapter relatedAdapter = (PlaylistAdapter) listRelatedPlaylist.getAdapter();
        if (relatedAdapter != null) {
            HashMap<Integer, Boolean> relatedSelection = relatedAdapter.getSelectedItems();
            for (Integer playlistId : relatedSelection.keySet()) {
                if (relatedSelection.get(playlistId)) { // Nếu được check => Thêm vào playlist
                    playlistViewModel.addSongToPlaylist(playlistId, selectedSong.getSong_id());
                }
            }
        }
        updateDatabaseWithFinalSelection(selectedSong);
        toggleAddPlaylistCancel();
        playlistViewModel.fetchListPlaylistContainSong(userSession.getUserSession().getId(), selectedSong.getSong_id()); // Cập nhật ngay lập tức

        // Gửi thông báo cập nhậtv
        Bundle result = new Bundle();
        result.putBoolean("isUpdated", true);
        getParentFragmentManager().setFragmentResult("updateSongList", result);
    }

    private void updateDatabaseWithFinalSelection(Song song) {
        if (isLikedTemp) {
            songViewModel.addSongToLikes(userSession.getUserSession().getId(), song.getSong_id());
        } else {
            songViewModel.removeSongFromLikes(userSession.getUserSession().getId(), song.getSong_id());
        }
        // Chỉ cập nhật trạng thái "Related" nếu phần này đang hiển thị
        if (addToSongLikeRelated.getVisibility() == View.VISIBLE) {
            if (isRelatedTemp) {
                songViewModel.addSongToLikes(userSession.getUserSession().getId(), song.getSong_id());
            } else {
                songViewModel.removeSongFromLikes(userSession.getUserSession().getId(), song.getSong_id());
            }
        }
        Log.d("Playlist", "Updated song: " + song.getTitle() + " | Liked: " + isLikedTemp + " | Related: " + isRelatedTemp);
    }
    private void toggleAddPlaylistCancel() {
        View view = getView();
        if (view != null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.toggleMiniPlayerVisibility(false);
                mainActivity.toggleBottomNavigationVisibility(false);
            }

            ObjectAnimator slideDown = ObjectAnimator.ofFloat(view, "translationY", 0, view.getHeight());
            slideDown.setDuration(300);
            slideDown.setInterpolator(new AccelerateInterpolator());

            slideDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.postOnAnimation(() -> {
                        if (mainActivity != null) {
                            // Lấy Fragment trước đó trong BackStack
                            int backStackCount = mainActivity.getSupportFragmentManager().getBackStackEntryCount();
                            boolean isPreviousPlayerFragment = false;

                            if (backStackCount > 1) { // Kiểm tra xem có Fragment nào trước đó không
                                String previousFragmentName = mainActivity.getSupportFragmentManager()
                                        .getBackStackEntryAt(backStackCount - 2) // -2 để lấy fragment trước đó
                                        .getName();
                                isPreviousPlayerFragment = "PlayerFragment".equals(previousFragmentName);
                            }

                            mainActivity.getSupportFragmentManager().popBackStack();

                            // Nếu trước đó KHÔNG phải PlayerFragment, hiển thị MiniPlayer và BottomNavigation
                            if (!isPreviousPlayerFragment) {
                                if (musicPlayerManager.isPlaying() || musicPlayerManager.getCurrentSong() != null) {
                                    mainActivity.toggleMiniPlayerVisibility(true);
                                }
                                mainActivity.toggleBottomNavigationVisibility(true);
                            }
                        }
                    });
                }

                @Override
                public void onAnimationStart(Animator animation) {
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