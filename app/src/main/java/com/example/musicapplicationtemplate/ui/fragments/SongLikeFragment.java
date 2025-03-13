package com.example.musicapplicationtemplate.ui.fragments;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
        Log.d("ListSongLike","number of song like: "+listSongLike.size());

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


        // Lắng nghe kết quả từ AddSongLikeFragment
        getParentFragmentManager().setFragmentResultListener("updateSongList", this, (requestKey, result) -> {
            if (result.getBoolean("isUpdated", false)) {
                refreshSongList(); // Cập nhật danh sách bài hát
            }
        });

        //tao margin cho item cuoi
        rvListSongLike.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int itemCount = state.getItemCount();

                // Nếu là item cuối cùng, thêm margin bottom
                if (position == itemCount - 1) {
                    outRect.bottom = 500; // Điều chỉnh khoảng trống tùy theo MiniPlayer
                }
            }
        });
        if (listSongLike != null) {
            songAdapter = new SongAdapter(getContext(),listSongLike,R.layout.item_song_1,this::playSong);
            rvListSongLike.setAdapter(songAdapter);
            rvListSongLike.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        return view;
    }

    private void refreshSongList() {
        listSongLike.clear();
        listSongLike.addAll(getLikedSongs());
        songAdapter.notifyDataSetChanged();
    }
    private List<Song> getLikedSongs() {
        User user = new UserSession(requireContext()).getUserSession();
        LikeDAO likeDAO = new LikeDAO();
        List<Like> likedList = likeDAO.getListSongLikeByUserId(user.getId());

        List<Song> likedSongs = new ArrayList<>();
        for (Like like : likedList) {
            likedSongs.add(like.getSong());
        }
        return likedSongs;
    }
    public void toggleSongLikeBack(){
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
        ft.replace(R.id.fragment_container, new HomeFragment());
        ft.addToBackStack(null);
        ft.commit();
    }
    private void playSong(Song song) {
        List<Like> listLike = new LikeDAO().getListSongLikeByUserId(usersession.getUserSession().getId());
        List<Song> listLikedSong = new ArrayList<>(); // Hàm này lấy danh sách tất cả bài hát
        for(Like like:listLike){
            listLikedSong.add(like.getSong());
        }
        int index = -1;
        for (int i = 0; i < listLikedSong.size(); i++) {
            if (listLikedSong.get(i).getSong_id() == song.getSong_id() ) {
                index = i;
                break;
            }
        }
        Log.d("list all song","Number of song and index of song: "+listLikedSong.size()+" - index: "+index);
        if (index != -1) {
            musicPlayerManager.setPlaylist(listLikedSong, index);
        }
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
        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, 0)
                .add(R.id.fragment_container, new AddSongLikeFragment())
                .addToBackStack(null) // Lưu trạng thái fragment trước đó
                .commit();
    }

}

