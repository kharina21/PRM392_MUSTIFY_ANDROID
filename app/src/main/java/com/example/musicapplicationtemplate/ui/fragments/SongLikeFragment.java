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
    private ImageButton btnSongLikeBack;
    private RecyclerView rvListSongLike;

    private ImageButton btnAddSongToLikeList;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_like, container, false);

        btnAddSongToLikeList = view.findViewById(R.id.btnAddSongToLikeList);
        btnAddSongToLikeList.setOnClickListener(v->showAddSongDialog());
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

            // Cập nhật SeekBar
            int duration = musicPlayerManager.getDuration();
            miniPlayerFragment.setSeekBarMax(duration);
            miniPlayerFragment.updateSeekBar(0);

            handler.removeCallbacks(updateSeekBarRunnable);
            handler.post(updateSeekBarRunnable);
        }
    }
    private void showAddSongDialog() {
        SongDAO songDAO = new SongDAO();
        LikeDAO likeDAO = new LikeDAO();
        User user = new UserSession(requireContext()).getUserSession();

        // Lấy toàn bộ bài hát từ database
        List<Song> allSongs = songDAO.getAllSongs();

        // Lấy danh sách bài hát đã thích
        List<Like> listLiked = likeDAO.getListSongLikeByUserId(user.getId());
        List<Song> likedSongs = new ArrayList<>();
        for(Like like : listLiked) likedSongs.add(like.getSong());

        // Lọc ra danh sách bài hát chưa thích
        List<Song> songsToShow = new ArrayList<>();
        for (Song song : allSongs) {
            boolean isLiked = false;
            for (Song likedSong : likedSongs) {
                if (likedSong.getSong_id() == song.getSong_id()) { // So sánh song_id thay vì contains()
                    isLiked = true;
                    break;
                }
            }
            if (!isLiked) {
                songsToShow.add(song);
            }
        }

        // Nếu không có bài hát nào mới để thêm
        if (songsToShow.isEmpty()) {
            Toast.makeText(requireContext(), "Tất cả bài hát đã có trong danh sách yêu thích!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị danh sách bài hát để chọn (sử dụng Dialog hoặc RecyclerView tùy ý)
        showSongSelectionDialog(songsToShow);
    }

    private void showSongSelectionDialog(List<Song> songsToShow) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn bài hát để thêm");

        String[] songTitles = new String[songsToShow.size()];
        boolean[] checkedItems = new boolean[songsToShow.size()];
        for (int i = 0; i < songsToShow.size(); i++) {
            songTitles[i] = songsToShow.get(i).getTitle();
        }

        builder.setMultiChoiceItems(songTitles, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
        });

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            LikeDAO likeDAO = new LikeDAO();
            User user = new UserSession(requireContext()).getUserSession();

            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    likeDAO.addSongToListLike(user.getId(), songsToShow.get(i).getSong_id());
                }
            }

            // Cập nhật danh sách sau khi thêm bài hát
            loadLikedSongs();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void loadLikedSongs() {
        LikeDAO likeDAO = new LikeDAO();
        // Xóa danh sách cũ trước khi load lại
        listSongLike.clear();
        // Lấy danh sách bài hát yêu thích
        List<Like> likedSongs = likeDAO.getListSongLikeByUserId(usersession.getUserSession().getId());
        for (Like like : likedSongs) {
            if (like.getSong() != null) {
                listSongLike.add(like.getSong());
            }
        }
        // Cập nhật dữ liệu cho Adapter
        songAdapter.notifyDataSetChanged();
    }
}

