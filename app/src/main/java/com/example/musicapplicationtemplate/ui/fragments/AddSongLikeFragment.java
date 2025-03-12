package com.example.musicapplicationtemplate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.adapter.UnlikedSongAdapter;
import com.example.musicapplicationtemplate.model.Like;
import com.example.musicapplicationtemplate.model.Song;
import com.example.musicapplicationtemplate.model.User;
import com.example.musicapplicationtemplate.sqlserver.LikeDAO;
import com.example.musicapplicationtemplate.sqlserver.SongDAO;
import com.example.musicapplicationtemplate.utils.UserSession;

import java.util.ArrayList;
import java.util.List;

public class AddSongLikeFragment extends Fragment {
    private RecyclerView rvAddSongList;
    private UnlikedSongAdapter adapter;
    private List<Song> songList;
    private ImageView btnAddSongLikeClose;
    private ImageView btnConfirmAddSong;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_song_like, container, false);

        rvAddSongList = view.findViewById(R.id.rvAddSongList);
        btnAddSongLikeClose = view.findViewById(R.id.btnAddSongLikeClose);
        btnConfirmAddSong = view.findViewById(R.id.btnConfirmAddSong);
        songList = getUnlikedSongs();
        adapter = new UnlikedSongAdapter(requireContext(), songList);
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

    private List<Song> getUnlikedSongs() {
        User user = new UserSession(requireContext()).getUserSession();
        SongDAO songDAO = new SongDAO();
        List<Song> allSongs = songDAO.getAllSongs();
        LikeDAO likeDAO = new LikeDAO();
        List<Like> listLiked = likeDAO.getListSongLikeByUserId(user.getId());
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
        return songsToShow;
    }

    private void addSelectedSongsToLiked() {
        List<Song> selectedSongs = adapter.getSelectedSongs();
        LikeDAO ldb = new LikeDAO();
        if (!selectedSongs.isEmpty()) {
            for (Song s : selectedSongs) {
                ldb.addSongToListLike(new UserSession(requireContext()).getUserSession().getId(), s.getSong_id());
            }
            Toast.makeText(requireContext(), "Đã thêm " + selectedSongs.size() + " bài hát vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_down, R.anim.slide_up);
            ft.replace(R.id.fragment_container, new SongLikeFragment());
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void toggleAddSongLikeClose() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_down, R.anim.slide_up);
        ft.replace(R.id.fragment_container, new SongLikeFragment());
        ft.addToBackStack(null);
        ft.commit();
    }
}
