package com.example.musicapplicationtemplate.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;

import java.util.ArrayList;
import java.util.List;

import com.example.musicapplicationtemplate.adapter.SongAdapter;
import com.example.musicapplicationtemplate.utils.UserSession;

import model.RecentlyPlayed;
import model.Song;
import sqlserver.RecentlyPlayedDAO;
import sqlserver.SongDAO;

public class HomeFragment extends Fragment implements MusicPlayerManager.OnPlaybackChangeListener {

    private RecyclerView rvList5Lastest,rvRecentlyPlayed;
    private TextView tvWelcomeTag;
    private MusicPlayerManager musicPlayerManager;
    private MiniPlayerFragment miniPlayerFragment;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private UserSession usersession;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //su dung usersession de lay user
        usersession = new UserSession(requireContext());

        tvWelcomeTag = view.findViewById(R.id.tvWelcomeTag);
        rvList5Lastest = view.findViewById(R.id.rvList5Lastest);
        rvRecentlyPlayed = view.findViewById(R.id.rvRecentlyPlayed);
        musicPlayerManager = MusicPlayerManager.getInstance();
        miniPlayerFragment = (MiniPlayerFragment) getChildFragmentManager().findFragmentById(R.id.miniPlayerFragment);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadSongs();
        tvWelcomeTag.setText("Hello "+ usersession.getUserSession().getUsername());
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
        List<Song> List5Lastest = songDAO.getLastest5Songs();
        SongAdapter songAdapter1 = new SongAdapter(getContext(), List5Lastest,R.layout.item_song_2, this::playSong);
        rvList5Lastest.setAdapter(songAdapter1);
        //Recycle View theo chieu doc
//        rvList5Lastest.setLayoutManager(new LinearLayoutManager(getContext()));
        //theo chieu ngang
        rvList5Lastest.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        //list recently played
        RecentlyPlayedDAO rpd = new RecentlyPlayedDAO();
        List<RecentlyPlayed> listRecentlyPlayed = rpd.get10SongsRecentlyPlayedByUserId(usersession.getUserSession().getId());
        List<Song> listSongRecentlyPlayed = new ArrayList<>();
        Log.d("list RecentlyPLayed","list RecentlyPLayed: "+listRecentlyPlayed);

        SongDAO sdb = new SongDAO();
        Song s1 = sdb.getSongById(1);
        Log.d("Song by id 1","Song by id 1: "+ s1);
        for(RecentlyPlayed rp : listRecentlyPlayed){
            Log.d("Song in recentplay list","Song in recentplay list: "+ rp.getSong());
            listSongRecentlyPlayed.add(rp.getSong());
        }
        SongAdapter songAdapter2 = new SongAdapter(getContext(), listSongRecentlyPlayed,R.layout.item_song_2,this::playSong);
        rvRecentlyPlayed.setAdapter(songAdapter2);
        rvRecentlyPlayed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void playSong(Song song) {
        musicPlayerManager.playSong(getContext(), song);

        // Thêm bài hát vào Recently Played
        RecentlyPlayedDAO rpd = new RecentlyPlayedDAO();
        rpd.addSongPlayed(usersession.getUserSession(), song);

        // Cập nhật danh sách Recently Played ngay lập tức
        loadSongs();

        // Lấy MiniPlayerFragment từ MainActivity
        MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.miniPlayerFragment);

        if (miniPlayerFragment != null) {
            miniPlayerFragment.updateMiniPlayerUI();
            ((MainActivity) getActivity()).toggleMiniPlayerVisibility(true);

            // Cập nhật SeekBar
            int duration = musicPlayerManager.getDuration();
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
