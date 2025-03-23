package com.example.musicapplicationtemplate.ui.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.adapter.DetailPlaylistAdapter;
import com.example.musicapplicationtemplate.api.ViewModel.PlaylistViewModel;
import com.example.musicapplicationtemplate.api.ViewModel.RecentlyPlayedViewModel;
import com.example.musicapplicationtemplate.api.ViewModel.SongViewModel;
import com.example.musicapplicationtemplate.model.Playlist;
import com.example.musicapplicationtemplate.model.Song;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;
import com.example.musicapplicationtemplate.utils.UserSession;

import java.util.List;

public class DetailPlaylistFragment extends Fragment {
    private TextView tvPlaylistTitle, tvPlaylistSongCount;
    private ImageView btnListPlaylistBack, btnRemovePlaylist;
    private DetailPlaylistAdapter adapter;
    private UserSession userSession;
    private RecyclerView rvDetailPlaylist;
    private MusicPlayerManager musicPlayerManager;
    private RecentlyPlayedViewModel recentlyPlayedViewModel;

    private PlaylistViewModel playlistViewModel;
    private SongViewModel songViewModel;
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Runnable updateSeekBarRunnable;
    private MiniPlayerFragment miniPlayerFragment;
    private Playlist playlist;
    private MainActivity mainActivity;

    public DetailPlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        musicPlayerManager = MusicPlayerManager.getInstance();
        userSession = new UserSession(requireContext());
        recentlyPlayedViewModel = new ViewModelProvider(this).get(RecentlyPlayedViewModel.class);
        playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

        if (getArguments() != null) {
            playlist = (Playlist) getArguments().getSerializable("playlist_data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_playlist, container, false);

        tvPlaylistTitle = view.findViewById(R.id.tvPlaylistTitle);
        tvPlaylistSongCount = view.findViewById(R.id.tvPlaylistSongCount);
        btnListPlaylistBack = view.findViewById(R.id.btnListPlaylistBack);
        btnListPlaylistBack.setOnClickListener(v -> toggleBack());
        btnRemovePlaylist = view.findViewById(R.id.btnRemovePlaylist);
        btnRemovePlaylist.setOnClickListener(v -> toggleRemovePlaylist());

        rvDetailPlaylist = view.findViewById(R.id.rvDetailPlaylist);
        rvDetailPlaylist.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo Adapter trước
        adapter = new DetailPlaylistAdapter(getContext(), playlist.getPlaylist_id(), List.of(), this::playSong);
        rvDetailPlaylist.setAdapter(adapter);

        // Hiển thị thông tin Playlist
        if (playlist != null) {
            tvPlaylistTitle.setText(playlist.getPlaylist_name());
            tvPlaylistSongCount.setText(playlist.getSong_count() + " bài hát");
        }

        // Load danh sách bài hát khi mở Fragment
        loadSongs();

        getParentFragmentManager().setFragmentResultListener("updateSongList", this, (requestKey, bundle) -> {
            boolean isUpdated = bundle.getBoolean("isUpdated", false);
            if (isUpdated) {
                loadSongs(); // Tải lại danh sách bài hát ngay lập tức
            }
        });

        return view;
    }

    private void playSong(Song song) {
        if (musicPlayerManager != null) {
            songViewModel.fetchListSongsByPlaylistId(playlist.getPlaylist_id());
            songViewModel.getListSongsByPlaylistId().observe(getViewLifecycleOwner(), listAllSongs -> {
                if (listAllSongs != null && !listAllSongs.isEmpty()) {
                    int index = -1;
                    for (int i = 0; i < listAllSongs.size(); i++) {
                        if (listAllSongs.get(i).getSong_id() == song.getSong_id()) {
                            index = i;
                            break;
                        }
                    }
                    if (index != -1) {
                        musicPlayerManager.setPlaylist(listAllSongs, index);
                        musicPlayerManager.playSong(getContext(), listAllSongs.get(index));
                    } else {
                        Log.e("playSong", "Song not found in playlist!");
                    }
                } else {
                    Log.e("playSong", "Playlist is empty or null!");
                }
            });

            recentlyPlayedViewModel.fetchIsAddRecentlyPlayed(userSession.getUserSession().getId(), song.getSong_id());
            loadSongs();

            // Cập nhật MiniPlayer
            if (getActivity() != null) {
                MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) getActivity()
                        .getSupportFragmentManager()
                        .findFragmentById(R.id.miniPlayerFragment);
                if (miniPlayerFragment != null) {
                    miniPlayerFragment.updateMiniPlayerUI();
                    miniPlayerFragment.getView().setVisibility(View.VISIBLE);
                }
            }

            if (mainActivity != null) {
                mainActivity.toggleMiniPlayerVisibility(true);
            }

            handler.removeCallbacks(updateSeekBarRunnable);
            handler.post(updateSeekBarRunnable);
        }
    }

    private void loadSongs() {
        songViewModel.fetchListSongsByPlaylistId(playlist.getPlaylist_id());
        songViewModel.getListSongsByPlaylistId().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                Log.d("PLAYLIST List", "PLAYLIST List: " + list.size());
                adapter.updateData(list);
                tvPlaylistSongCount.setText(list.size() + " bài hát");
            } else {
                Log.d("list 5 new songs", "list 5 new songs is null");
            }
        });
    }

    private void toggleRemovePlaylist() {
        playlistViewModel.deletePlaylist(playlist.getPlaylist_id());
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.fragment_container, new LibraryFragment());
        ft.addToBackStack(null);
        ft.commit();
        Toast.makeText(getContext(),"Đã xóa danh sách phát "+playlist.getPlaylist_name(),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        handler.removeCallbacks(updateSeekBarRunnable);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void toggleBack() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.fragment_container, new LibraryFragment());
        ft.addToBackStack(null);
        ft.commit();
    }
}
