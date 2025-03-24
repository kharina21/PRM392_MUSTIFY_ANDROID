package com.example.musicapplicationtemplate.ui.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.adapter.SongAdapter;
import com.example.musicapplicationtemplate.api.ViewModel.RecentlyPlayedViewModel;
import com.example.musicapplicationtemplate.api.ViewModel.SongViewModel;
import com.example.musicapplicationtemplate.model.Song;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;
import com.example.musicapplicationtemplate.utils.UserSession;

import java.util.List;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView rvSearchResult;  // Thêm RecyclerView
    private SongAdapter songAdapter;
    private SongViewModel songViewModel;
    private RecentlyPlayedViewModel recentlyPlayedViewModel;
    private MusicPlayerManager musicPlayerManager;
    private UserSession userSession;
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    private MiniPlayerFragment miniPlayerFragment;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        recentlyPlayedViewModel = new ViewModelProvider(this).get(RecentlyPlayedViewModel.class);
        musicPlayerManager = MusicPlayerManager.getInstance();
        userSession = new UserSession(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        rvSearchResult = view.findViewById(R.id.RvSearchResult);  // Ánh xạ RecyclerView
        rvSearchResult.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo adapter rỗng
        songAdapter = new SongAdapter(getContext(), List.of(), R.layout.item_song_1, this::playSong);
        rvSearchResult.setAdapter(songAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    searchSongs(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    songAdapter.updateList(List.of()); // Xóa danh sách khi search rỗng
                } else {
                    searchSongs(newText);
                }
                return true;
            }
        });

        return view;
    }

    private void searchSongs(String query) {
        Log.d("SearchFragment", "Đang tìm kiếm bài hát: " + query);

        songViewModel.fetchListSongByTitle(query);
        songViewModel.getListSongByTitle().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                songAdapter.updateList(list);  // Cập nhật dữ liệu vào adapter
            }
        });
    }

    private void playSong(Song song) {
        if (musicPlayerManager == null) return;

        songViewModel.fetchAllSongs();
        songViewModel.getAllSongs().observe(getViewLifecycleOwner(), listAllSongs -> {
            if (listAllSongs != null) {
                int index = -1;
                for (int i = 0; i < listAllSongs.size(); i++) {
                    if (listAllSongs.get(i).getSong_id() == song.getSong_id()) {
                        index = i;
                        break;
                    }
                }
                musicPlayerManager.setPlaylist(listAllSongs, index);
            }
        });

        musicPlayerManager.playSong(getContext(), song);
        recentlyPlayedViewModel.fetchIsAddRecentlyPlayed(userSession.getUserSession().getId(), song.getSong_id());

        updateMiniPlayer();
    }

    private void updateMiniPlayer() {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) activity.getSupportFragmentManager().findFragmentById(R.id.miniPlayerFragment);

            if (miniPlayerFragment != null) {
                miniPlayerFragment.updateMiniPlayerUI();
                miniPlayerFragment.getView().setVisibility(View.VISIBLE);
                activity.toggleMiniPlayerVisibility(true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}
