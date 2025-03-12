package com.example.musicapplicationtemplate.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.model.Song;

import java.util.ArrayList;
import java.util.List;

public class UnlikedSongAdapter extends RecyclerView.Adapter<UnlikedSongAdapter.ViewHolder> {
    private Context context;
    private List<Song> songList;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private OnSelectionChangeListener selectionChangeListener;

    public interface OnSelectionChangeListener {
        void onSelectionChanged(int count);
    }

    public void setOnSelectionChangeListener(OnSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    public UnlikedSongAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_unliked_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        Glide.with(context).load("file:///android_asset/" + song.getImage()).into(holder.image);

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedItems.get(position, false));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.put(position, true);
            } else {
                selectedItems.delete(position);
            }
            if (selectionChangeListener != null) {
                selectionChangeListener.onSelectionChanged(getSelectedSongs().size());
            }
        });

        //click vao item view
        holder.itemView.setOnClickListener(v->{
            boolean newState = !selectedItems.get(position, false);
            holder.checkBox.setChecked(newState);
            if(newState){
                selectedItems.put(position, true);
            }else{
                selectedItems.delete(position);
            }
            if(selectionChangeListener != null){
                selectionChangeListener.onSelectionChanged(getSelectedSongs().size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public List<Song> getSelectedSongs() {
        List<Song> selectedSongs = new ArrayList<>();
        for (int i = 0; i < songList.size(); i++) {
            if (selectedItems.get(i, false)) {
                selectedSongs.add(songList.get(i));
            }
        }
        return selectedSongs;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, artist;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.add_song_like_img);
            title = itemView.findViewById(R.id.add_song_like_title);
            artist = itemView.findViewById(R.id.add_song_like_artist);
            checkBox = itemView.findViewById(R.id.add_song_like_checkbox);
        }
    }
}
