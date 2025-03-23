package com.example.musicapplicationtemplate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.model.Playlist;

import java.util.ArrayList;
import java.util.List;

public class ListPlaylistAdapter extends RecyclerView.Adapter<ListPlaylistAdapter.ListPlaylistViewHolder> {
    private Context context;
    private List<Playlist> playlistList;
    private OnItemClickListener onItemClickListener;

    public ListPlaylistAdapter(Context context, List<Playlist> playlistList) {
        this.context = context;
        this.playlistList = new ArrayList<>(playlistList); //
    }

    public void updateData(List<Playlist> newList) {
        playlistList.clear();
        playlistList.addAll(newList);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ListPlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_playlist_item, parent, false);
        return new ListPlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListPlaylistViewHolder holder, int position) {
        Playlist playlist = playlistList.get(position);
        holder.tvTitle.setText(playlist.getPlaylist_name());
        holder.tvSongCount.setText(playlist.getSong_count() != 0 ? playlist.getSong_count() + " bài hát" : "Trống");
        holder.imgCover.setImageResource(R.drawable.ic_music_note);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(playlist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistList != null ? playlistList.size() : 0; // 🔥 Tránh lỗi NullPointerException
    }

    public static class ListPlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvTitle, tvSongCount;

        public ListPlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgListPlaylistCover);
            tvTitle = itemView.findViewById(R.id.tvListPlaylistTitle);
            tvSongCount = itemView.findViewById(R.id.tvListPlaylistSongCount);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Playlist playlist);
    }
}
