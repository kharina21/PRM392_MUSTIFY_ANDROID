package com.example.musicapplicationtemplate.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplicationtemplate.R;


import java.util.List;

import model.Song;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    Context context;
    List<Song> songs;

    private final OnSongClickListener songClickListener;
    public interface OnSongClickListener {
        void onSongClick(Song song);
    }
    public SongAdapter(Context context, List<Song> songs, OnSongClickListener songClickListener) {
        this.context = context;
        this.songs = songs;
        this.songClickListener = songClickListener;
    }

    @NonNull
    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_song_1, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.tvTitle.setText(songs.get(position).getTitle());
        holder.tvArtist.setText(songs.get(position).getArtist());

        String file_path = "file:///android_asset/" + songs.get(position).getImage();
        Glide.with(context).load(file_path).into(holder.imgSong);

        holder.itemView.setOnClickListener(v -> {
            if (songClickListener != null) {
                songClickListener.onSongClick(song);
            }
        });
        holder.imgOptions.setOnClickListener(v -> showOptionsMenu(v, song));
    }

    private void showOptionsMenu(View v, Song song) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.inflate(R.menu.song_options_menu);

        popup.setOnMenuItemClickListener(item -> {
            Song selectedSong = song; // Lấy đúng bài hát theo vị trí

            if (item.getItemId() == R.id.option_play) {
                Toast.makeText(context, "Play " + selectedSong.getTitle(), Toast.LENGTH_SHORT).show();
                songClickListener.onSongClick(song);
                return true;
            } else if (item.getItemId() == R.id.option_add_to_playlist) {
                Toast.makeText(context, "Added to Playlist: " + selectedSong.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.option_delete) {
                Toast.makeText(context, "Deleted: " + selectedSong.getTitle(), Toast.LENGTH_SHORT).show();
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

    ImageView imgSong;
    TextView tvTitle;
    TextView tvArtist;
    ImageView imgOptions;

    public SongViewHolder(@NonNull View itemView) {
        super(itemView);
        imgSong = itemView.findViewById(R.id.imgSong);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        tvArtist = itemView.findViewById(R.id.tvArtist);
        imgOptions = itemView.findViewById(R.id.imgOptions);
    }
}
}
