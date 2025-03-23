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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.api.ApiService.ApiClient;
import com.example.musicapplicationtemplate.api.ApiService.ApiLikeService;
import com.example.musicapplicationtemplate.api.ApiService.ApiRecentlyPlayedService;
import com.example.musicapplicationtemplate.api.ApiService.ApiResponse;
import com.example.musicapplicationtemplate.api.ApiService.ApiSongService;
import com.example.musicapplicationtemplate.ui.activities.MainActivity;
import com.example.musicapplicationtemplate.ui.fragments.AddToPlaylistFragment;
import com.example.musicapplicationtemplate.ui.fragments.PlayerFragment;
import com.example.musicapplicationtemplate.utils.UserSession;

import java.util.ArrayList;
import java.util.List;

import com.example.musicapplicationtemplate.model.Song;
import com.example.musicapplicationtemplate.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPlaylistAdapter extends RecyclerView.Adapter<DetailPlaylistAdapter.ListPlayListViewHolder> {
    Context context;
    List<Song> songs;
    private int playlistId;
    private final OnSongClickListener songClickListener;

    private UserSession usersession;
    private ApiRecentlyPlayedService arps;
    private ApiLikeService als;

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public DetailPlaylistAdapter(Context context, int playlistId, List<Song> songs, OnSongClickListener songClickListener) {
        this.context = context;
        this.songs = songs;
        this.playlistId = playlistId;
        this.songClickListener = songClickListener;
        this.usersession = new UserSession(context);

    }

    @NonNull
    @Override
    public DetailPlaylistAdapter.ListPlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_song_1, parent, false);
        return new ListPlayListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailPlaylistAdapter.ListPlayListViewHolder holder, int position) {
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
            arps.addSongPlayed(u.getId(), song.getSong_id()).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Log.d("add song to recently played list", "add song to recently played list: " + song.getTitle());
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
    public void updateData(List<Song> newSongs) {
        if (songs instanceof ArrayList) {
            songs.clear();
            songs.addAll(newSongs);
        } else {
            songs = new ArrayList<>(newSongs); // Nếu không phải ArrayList, tạo mới
        }
        notifyDataSetChanged();
    }
    private void showOptionsMenu(View v, Song song) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.inflate(R.menu.song_options_menu);

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.option_play) {
                Toast.makeText(context, "Play " + song.getTitle(), Toast.LENGTH_SHORT).show();
                songClickListener.onSongClick(song);
                return true;
            } else if (item.getItemId() == R.id.option_add_to_playlist) {
                AddToPlaylistFragment addToPlaylistFragment = new AddToPlaylistFragment();
                // Đóng gói dữ liệu vào Bundle
                Bundle bundle = new Bundle();
                bundle.putSerializable("selected_song", song); // Hoặc putParcelable nếu Song là Parcelable
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
//                Toast.makeText(context, "Added to Playlist: " + song.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.option_delete) {
                ApiSongService apiSongService = ApiClient.getClient().create(ApiSongService.class);
                apiSongService.deleteSongFromPlaylist(playlistId, song.getSong_id()).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            int position = songs.indexOf(song);
                            if (position != -1) {
                                songs.remove(position);
                                notifyItemRemoved(position);
                            }

                            // Gửi thông báo để DetailPlaylistFragment cập nhật danh sách
                            if (context instanceof AppCompatActivity) {
                                AppCompatActivity activity = (AppCompatActivity) context;
                                Bundle result = new Bundle();
                                result.putBoolean("isUpdated", true);
                                activity.getSupportFragmentManager().setFragmentResult("updateSongList", result);
                            }

                            Toast.makeText(context, "Đã xóa bài hát khỏi playlist", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Lỗi khi xóa bài hát khỏi playlist", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(context, "Lỗi khi xóa bài hát", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            } else {
                return false;
            }
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ListPlayListViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong, imgOptions;
        TextView tvTitle, tvArtist;

        public ListPlayListViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSong = itemView.findViewById(R.id.imgSong);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            imgOptions = itemView.findViewById(R.id.imgOptions);

        }
    }
}
