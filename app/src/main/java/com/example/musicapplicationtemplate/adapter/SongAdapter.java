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
import com.example.musicapplicationtemplate.model.Like;
import com.example.musicapplicationtemplate.sqlserver.LikeDAO;
import com.example.musicapplicationtemplate.utils.UserSession;

import java.util.List;

import com.example.musicapplicationtemplate.model.RecentlyPlayed;
import com.example.musicapplicationtemplate.model.Song;
import com.example.musicapplicationtemplate.model.User;
import com.example.musicapplicationtemplate.sqlserver.RecentlyPlayedDAO;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    Context context;
    List<Song> songs;
    private final int layoutId;
    private final OnSongClickListener songClickListener;

    private UserSession usersession;

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
            RecentlyPlayed rp = new RecentlyPlayed();
            RecentlyPlayedDAO rpd = new RecentlyPlayedDAO();
            rpd.addSongPlayed(u,song);
            Log.d("add song to recently played list", "add song to recently played list: "+song.getTitle());
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

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.option_play) {
                Toast.makeText(context, "Play " + song.getTitle(), Toast.LENGTH_SHORT).show();
                songClickListener.onSongClick(song);
                return true;
            } else if (item.getItemId() == R.id.option_add_to_playlist) {
                Toast.makeText(context, "Added to Playlist: " + song.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.option_delete) {
                LikeDAO ldb = new LikeDAO();
                ldb.removeSongInListLike(usersession.getUserSession().getId(),song.getSong_id());
                //update rv
                int position = songs.indexOf(song);
                if (position != -1) {
                    songs.remove(position);
                    notifyItemRemoved(position); // Cập nhật RecyclerView
                }
                // Gửi thông báo cập nhật
                if (context instanceof AppCompatActivity) {
                    Bundle result = new Bundle();
                    result.putBoolean("isUpdated", true);
                    AppCompatActivity activity = (AppCompatActivity) context;
                    activity.getSupportFragmentManager().setFragmentResult("updateSongList", result);
                }
                Toast.makeText(context, "Deleted: " + song.getTitle(), Toast.LENGTH_SHORT).show();
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
