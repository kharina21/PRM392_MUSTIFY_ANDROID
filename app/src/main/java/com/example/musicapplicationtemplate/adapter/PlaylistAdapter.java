package com.example.musicapplicationtemplate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.model.Playlist;

import java.util.HashMap;
import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    private Context context;
    private List<Playlist> playlists;
    private boolean isSavedList;
    private HashMap<Integer, Boolean> selectedItems = new HashMap<>();

    public PlaylistAdapter(Context context, List<Playlist> playlists, boolean isSavedList) {
        super(context, R.layout.playlist_item, playlists);
        this.context = context;
        this.playlists = playlists;
        this.isSavedList = isSavedList;

        // Khởi tạo trạng thái checkbox: true nếu là danh sách đã lưu
        for (Playlist playlist : playlists) {
            selectedItems.put(playlist.getPlaylist_id(), isSavedList);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false);
        }

        Playlist playlist = playlists.get(position);
        TextView tvPlaylistName = convertView.findViewById(R.id.tvPlaylistTitle);
        CheckBox checkBox = convertView.findViewById(R.id.checkboxSelectPlaylist);
        TextView tvSongCount = convertView.findViewById(R.id.tvSongCount);

        int song_count = playlist.getSong_count();
        tvSongCount.setText(song_count == 0 ? "trống" : song_count + " bài hát");

        tvPlaylistName.setText(playlist.getPlaylist_name());
        checkBox.setChecked(selectedItems.get(playlist.getPlaylist_id()));

        // Khi nhấn vào item (ngoại trừ CheckBox), sẽ toggle trạng thái check
        convertView.setOnClickListener(v -> {
            boolean newState = !checkBox.isChecked(); // Đảo trạng thái
            checkBox.setChecked(newState);
            selectedItems.put(playlist.getPlaylist_id(), newState);
        });

        // Khi nhấn trực tiếp vào CheckBox, cập nhật selectedItems
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedItems.put(playlist.getPlaylist_id(), isChecked);
        });

        return convertView;
    }

    public HashMap<Integer, Boolean> getSelectedItems() {
        return selectedItems;
    }
}
