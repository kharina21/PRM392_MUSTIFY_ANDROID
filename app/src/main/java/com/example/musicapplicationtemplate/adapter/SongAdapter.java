package com.example.musicapplicationtemplate.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.api.ApiService.ApiClient;
import com.example.musicapplicationtemplate.api.ApiService.ApiLikeService;
import com.example.musicapplicationtemplate.api.ApiService.ApiRecentlyPlayedService;
import com.example.musicapplicationtemplate.api.ApiService.ApiResponse;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.ui.fragments.AddToPlaylistFragment;
import com.example.musicapplicationtemplate.ui.fragments.PlayerFragment;
import com.example.musicapplicationtemplate.ui.fragments.SearchFragment;
import com.example.musicapplicationtemplate.utils.UserSession;
import java.util.List;
import com.example.musicapplicationtemplate.model.Song;
import com.example.musicapplicationtemplate.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    Context context;
    List<Song> songs;
    private final int layoutId;
    private final OnSongClickListener songClickListener;

    private UserSession usersession;
    private ApiRecentlyPlayedService arps;
    private ApiLikeService als;

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public SongAdapter(Context context, List<Song> songs, int layoutId, OnSongClickListener songClickListener) {
        this.context = context;
        this.songs = songs;
        this.layoutId = layoutId;
        this.songClickListener = songClickListener;
        this.usersession = new UserSession(context);

    }

    @NonNull
    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutId, parent, false);
        return new SongViewHolder(view, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.SongViewHolder holder, int position) {
        Song song = songs.get(position);

        if (holder.tvTitle != null) holder.tvTitle.setText(song.getTitle());
        if (holder.tvArtist != null) holder.tvArtist.setText(song.getArtist());

        String file_path = "file:///android_asset/" + song.getImage();
        if (holder.imgSong != null) {
            Glide.with(context).load(file_path).into(holder.imgSong);
        }

        holder.itemView.setOnClickListener(v -> {

            //add vao recently played
            User u = usersession.getUserSession();
            arps = ApiClient.getClient().create(ApiRecentlyPlayedService.class);
            arps.addSongPlayed(u.getId(),song.getSong_id()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Log.d("add song to recently played list", "add song to recently played list: "+song.getTitle());
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                }
            });
            if (songClickListener != null) {
                songClickListener.onSongClick(song);
            }
        });

        if (holder.imgOptions != null) {
            holder.imgOptions.setOnClickListener(v -> showOptionsMenu(v, song));
        }
    }

    private void showOptionsMenu(View v, Song song) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.inflate(R.menu.song_options_menu);

        // Kiểm tra nếu Fragment hiện tại là SearchFragment thì ẩn option_delete
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof SearchFragment) {
                popup.getMenu().findItem(R.id.option_delete).setVisible(false);
            }
        }

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.option_play) {
                Toast.makeText(context, "Play " + song.getTitle(), Toast.LENGTH_SHORT).show();
                songClickListener.onSongClick(song);
                return true;
            } else if (item.getItemId() == R.id.option_add_to_playlist) {
                AddToPlaylistFragment addToPlaylistFragment = new AddToPlaylistFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("selected_song", song);
                addToPlaylistFragment.setArguments(bundle);

                if (context instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    activity.getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_up, 0)
                            .add(R.id.fragment_container, addToPlaylistFragment)
                            .addToBackStack(null)
                            .commit();
                }
                if (context instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) context;
                    mainActivity.toggleMiniPlayerVisibility(false);
                    mainActivity.toggleBottomNavigationVisibility(false);
                }
                return true;
            } else if (item.getItemId() == R.id.option_delete) {
                als = ApiClient.getClient().create(ApiLikeService.class);
                als.deleteSongInListLike(usersession.getUserSession().getId(), song.getSong_id()).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        int position = songs.indexOf(song);
                        if (position != -1) {
                            songs.remove(position);
                            notifyItemRemoved(position);
                        }
                        if (context instanceof AppCompatActivity) {
                            Bundle result = new Bundle();
                            result.putBoolean("isUpdated", true);
                            AppCompatActivity activity = (AppCompatActivity) context;
                            activity.getSupportFragmentManager().setFragmentResult("updateSongList", result);
                        }
                        Toast.makeText(context, "Deleted: " + song.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                    }
                });
                return true;
            }
            return false;
        });

        popup.show();
    }

    public void updateList(List<Song> newSongs) {
        this.songs = newSongs;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong, imgOptions;
        TextView tvTitle, tvArtist;

        public SongViewHolder(@NonNull View itemView, int layoutId) {
            super(itemView);

            if (layoutId == R.layout.item_song_1) {
                imgSong = itemView.findViewById(R.id.imgSong);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvArtist = itemView.findViewById(R.id.tvArtist);
                imgOptions = itemView.findViewById(R.id.imgOptions);
            } else if (layoutId == R.layout.item_song_2) {
                imgSong = itemView.findViewById(R.id.itemSong2Img);
                tvTitle = itemView.findViewById(R.id.itemSong2Title);
                tvArtist = itemView.findViewById(R.id.itemSong2Artist);
                imgOptions = null; // Không có imgOptions trong item_song_2
            }
        }
    }
}
