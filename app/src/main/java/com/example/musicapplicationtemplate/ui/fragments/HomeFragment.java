package com.example.musicapplicationtemplate.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;

import java.util.List;

import adapter.SongAdapter;
import model.Song;
import sqlserver.SongDAO;

public class HomeFragment extends Fragment implements MusicPlayerManager.OnPlaybackChangeListener {

    private RecyclerView rvSongs;
    private MusicPlayerManager musicPlayerManager;
    private MiniPlayerFragment miniPlayerFragment;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rvSongs = view.findViewById(R.id.rvSongs);
        musicPlayerManager = MusicPlayerManager.getInstance();
        miniPlayerFragment = (MiniPlayerFragment) getChildFragmentManager().findFragmentById(R.id.miniPlayerFragment);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadSongs();
        musicPlayerManager.setOnPlaybackChangeListener(this);

        if (miniPlayerFragment != null) {
            miniPlayerFragment.getView().setVisibility(View.GONE);
        }

        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (musicPlayerManager.getCurrentSong() != null && musicPlayerManager.isPlaying()) {
                    int currentPosition = musicPlayerManager.getCurrentPosition();
                    MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) getActivity()
                            .getSupportFragmentManager()
                            .findFragmentById(R.id.miniPlayerFragment);

                    if (miniPlayerFragment != null) {
                        miniPlayerFragment.updateSeekBar(currentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            }
        };
    }

    private void loadSongs() {
        SongDAO songDAO = new SongDAO();
        List<Song> songs = songDAO.getAllSongs();
        SongAdapter songAdapter = new SongAdapter(getContext(), songs, this::playSong);
        rvSongs.setAdapter(songAdapter);
        rvSongs.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void playSong(Song song) {
        musicPlayerManager.playSong(getContext(), song);

        // Lấy MiniPlayerFragment từ MainActivity
        MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.miniPlayerFragment);

        if (miniPlayerFragment != null) {
            miniPlayerFragment.updateMiniPlayerUI();
            ((MainActivity) getActivity()).toggleMiniPlayerVisibility(true);

            // Cập nhật SeekBar
            int duration = musicPlayerManager.getDuration(); // Lấy từ MusicPlayerManager
            miniPlayerFragment.setSeekBarMax(duration);
            miniPlayerFragment.updateSeekBar(0);

            handler.removeCallbacks(updateSeekBarRunnable);
            handler.post(updateSeekBarRunnable);
        }
    }


    @Override
    public void onSongComplete() {
        MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.miniPlayerFragment);

        if (miniPlayerFragment != null) {
            miniPlayerFragment.updateMiniPlayerUI();
            ((MainActivity) getActivity()).toggleMiniPlayerVisibility(false);
            handler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateSeekBarRunnable);
    }
}
