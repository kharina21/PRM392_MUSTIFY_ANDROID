package com.example.musicapplicationtemplate.ui.fragments;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;
import java.util.ArrayList;
import java.util.List;
import com.example.musicapplicationtemplate.adapter.SongAdapter;
import com.example.musicapplicationtemplate.utils.UserSession;
import com.example.musicapplicationtemplate.model.RecentlyPlayed;
import com.example.musicapplicationtemplate.model.Song;
import com.example.musicapplicationtemplate.api.ViewModel.RecentlyPlayedViewModel;
import com.example.musicapplicationtemplate.api.ViewModel.SongViewModel;
public class HomeFragment extends Fragment implements MusicPlayerManager.OnPlaybackChangeListener {
    private RecyclerView rvList5Lastest, rvRecentlyPlayed;
    private TextView tvWelcomeTag;
    private MusicPlayerManager musicPlayerManager;
    private MiniPlayerFragment miniPlayerFragment;
    private LinearLayout btnSongLike;
    private MainActivity mainActivity;
    private SongViewModel songViewModel;
    private RecentlyPlayedViewModel recentlyPlayedViewModel;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private UserSession usersession;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //su dung usersession de lay user
        usersession = new UserSession(requireContext());

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        recentlyPlayedViewModel = new ViewModelProvider(this).get(RecentlyPlayedViewModel.class);
        mainActivity = new MainActivity();
        btnSongLike = view.findViewById(R.id.btnSongLike);
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
        tvWelcomeTag.setText("Hello " + usersession.getUserSession().getUsername());
        musicPlayerManager.setOnPlaybackChangeListener(this);

        //btnSongLike
        btnSongLike.setOnClickListener(v -> toggleSongLike());

        if (miniPlayerFragment != null) {
            if (musicPlayerManager.getCurrentSong() != null) {
                miniPlayerFragment.getView().setVisibility(View.VISIBLE);
            } else {
                miniPlayerFragment.getView().setVisibility(View.GONE);
            }
        }

        if (musicPlayerManager.checkMediaPlayer() ) {
            Log.d("GetCurrentSong","GetCurrentSong: "+musicPlayerManager.getCurrentSong());
            mainActivity.toggleMiniPlayerVisibility(true);
        } else {
            mainActivity.toggleMiniPlayerVisibility(false);
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
    private void toggleSongLike() {

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        //animation
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.fragment_container, new SongLikeFragment());
        // Thêm vào back stack để có thể quay lại
        transaction.addToBackStack(null);
        // Thực hiện transaction
        transaction.commit();
    }
    private void loadSongs() {
        //list 5 new songs
        songViewModel.fetchLastestSongs();
        songViewModel.getLatestSongs().observe(getViewLifecycleOwner(), lastestSongs ->{
            if(lastestSongs != null){
                SongAdapter songAdapter1 = new SongAdapter(getContext(), lastestSongs, R.layout.item_song_2, song -> playSong(song));
                rvList5Lastest.setAdapter(songAdapter1);
                //Recycle View theo chieu doc
                //rvList5Lastest.setLayoutManager(new LinearLayoutManager(getContext()));
                //theo chieu ngang
                rvList5Lastest.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            }else{
                Log.d("list 5 new songs", "list 5 new songs is null");
            }
        });

        recentlyPlayedViewModel.fetchSongsRecentlyPlayedByUserId(usersession.getUserSession().getId());
        recentlyPlayedViewModel.getSongsRecentlyPlayedByUserId().observe(getViewLifecycleOwner(), listRecentlyPlayed ->{
            List<Song> listSongRecentlyPlayed = new ArrayList<>();
            for (RecentlyPlayed rp : listRecentlyPlayed) {
                listSongRecentlyPlayed.add(rp.getSong());
            }
            SongAdapter songAdapter2 = new SongAdapter(getContext(), listSongRecentlyPlayed, R.layout.item_song_2, song->playSong(song));
            rvRecentlyPlayed.setAdapter(songAdapter2);
            rvRecentlyPlayed.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        });


    }
    private void playSong(Song song) {
        if (musicPlayerManager != null) {
            // Thiết lập danh sách bài hát trước khi phát
            songViewModel.fetchAllSongs();
            songViewModel.getAllSongs().observe(getViewLifecycleOwner(), listAllSongs ->{
                int index = -1;
                for (int i = 0; i < listAllSongs.size(); i++) {
                    if (listAllSongs.get(i).getSong_id() == song.getSong_id() ) {
                        index = i;
                        break;
                    }
                } musicPlayerManager.setPlaylist(listAllSongs, index);
            });

            musicPlayerManager.playSong(getContext(), song);
            // Thêm bài hát vào Recently Played
            recentlyPlayedViewModel.fetchIsAddRecentlyPlayed(usersession.getUserSession().getId(),song.getSong_id());
            // Cập nhật danh sách Recently Played ngay lập tức
            loadSongs();
            // Cập nhật MiniPlayer
            MiniPlayerFragment miniPlayerFragment = (MiniPlayerFragment) getActivity()
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.miniPlayerFragment);

            if (miniPlayerFragment != null) {
                miniPlayerFragment.updateMiniPlayerUI();
                miniPlayerFragment.getView().setVisibility(View.VISIBLE);
                ((MainActivity) getActivity()).toggleMiniPlayerVisibility(true);

                // Cập nhật SeekBar
                int duration = musicPlayerManager.getDuration();
                miniPlayerFragment.setSeekBarMax(duration);
                miniPlayerFragment.updateSeekBar(0);

                handler.removeCallbacks(updateSeekBarRunnable);
                handler.post(updateSeekBarRunnable);
            }
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
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBarRunnable);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
