package com.example.musicapplicationtemplate.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.adapter.SongAdapter;
import com.example.musicapplicationtemplate.model.Like;
import com.example.musicapplicationtemplate.model.Song;
import com.example.musicapplicationtemplate.model.User;
import com.example.musicapplicationtemplate.sqlserver.LikeDAO;
import com.example.musicapplicationtemplate.sqlserver.RecentlyPlayedDAO;
import com.example.musicapplicationtemplate.sqlserver.SongDAO;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;
import com.example.musicapplicationtemplate.utils.UserSession;

import java.util.ArrayList;
import java.util.List;


public class SongLikeFragment extends Fragment {

    private List<Song> listSongLike;
    private ImageView btnSongLikeBack;
    private RecyclerView rvListSongLike;

    private LinearLayout btnAddSongToLikeList;

    private UserSession usersession;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private SongAdapter songAdapter;

    private MusicPlayerManager musicPlayerManager;

    public SongLikeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicPlayerManager = MusicPlayerManager.getInstance();
        usersession = new UserSession(requireContext());
        listSongLike = new ArrayList<>();
        LikeDAO ldb = new LikeDAO();
        List<Like> listLike = ldb.getListSongLikeByUserId(usersession.getUserSession().getId());
        for(Like l : listLike){
            if(l.getSong() != null){

                listSongLike.add(l.getSong());
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_like, container, false);

        btnAddSongToLikeList = view.findViewById(R.id.btnAddSongToLikeList);
        btnAddSongToLikeList.setOnClickListener(v->showAddSongLikeFragment());
        btnSongLikeBack = view.findViewById(R.id.btnSongLikeBack);
        rvListSongLike = view.findViewById(R.id.rvListSongLike);
        btnSongLikeBack.setOnClickListener(v->toggleSongLikeBack());
        if (listSongLike != null) {
            songAdapter = new SongAdapter(getContext(),listSongLike,R.layout.item_song_1,this::playSong);
            rvListSongLike.setAdapter(songAdapter);
            rvListSongLike.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        return view;
    }

    public void toggleSongLikeBack(){
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
        ft.replace(R.id.fragment_container, new HomeFragment());
        ft.addToBackStack(null);
        ft.commit();
    }
    private void playSong(Song song) {
        musicPlayerManager.playSong(getContext(), song);
        // Thêm bài hát vào Recently Played
        RecentlyPlayedDAO rpd = new RecentlyPlayedDAO();
        rpd.addSongPlayed(usersession.getUserSession(), song);

        // Lấy MiniPlayerFragment từ MainActivity
        MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.miniPlayerFragment);

        if (miniPlayerFragment != null) {
            miniPlayerFragment.updateMiniPlayerUI();
            ((MainActivity) getActivity()).toggleMiniPlayerVisibility(true);
        }
    }
    private void showAddSongLikeFragment(){
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_up,R.anim.slide_down);
        ft.replace(R.id.fragment_container,new AddSongLikeFragment());
        ft.commit();
    }

}

