package com.example.musicapplicationtemplate.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.adapter.ListPlaylistAdapter;
import com.example.musicapplicationtemplate.api.ViewModel.PlaylistViewModel;
import com.example.musicapplicationtemplate.model.Playlist;
import com.example.musicapplicationtemplate.model.User;
import com.example.musicapplicationtemplate.utils.UserSession;

import java.util.ArrayList;
import java.util.List;


public class LibraryFragment extends Fragment {

    private RecyclerView rvListPlaylist;
    private PlaylistViewModel playlistViewModel;
    private UserSession userSession;
    private ImageView addPlaylist;
    private List<Playlist> playlists;


    public LibraryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlists = new ArrayList<>();
        userSession = new UserSession(requireContext());
        playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);
        playlistViewModel.fetchAllPlaylist(userSession.getUserSession().getId());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        addPlaylist = view.findViewById(R.id.addPlaylist);
        addPlaylist.setOnClickListener(v->openCreatePlaylistDialog());
        rvListPlaylist = view.findViewById(R.id.rvListPlaylist);

        rvListPlaylist.setLayoutManager(new LinearLayoutManager(getContext()));
        ListPlaylistAdapter adapter = new ListPlaylistAdapter(getContext(), new ArrayList<>());
        rvListPlaylist.setAdapter(adapter);
        playlistViewModel.getAllPlaylist().observe(getViewLifecycleOwner(), newPlaylists -> {
            if (newPlaylists != null && !newPlaylists.isEmpty()) {
                adapter.updateData(newPlaylists);
            }
        });

        // Xử lý sự kiện click vào item playlist
        adapter.setOnItemClickListener(playlist -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("playlist_data", playlist);

            DetailPlaylistFragment detailFragment = new DetailPlaylistFragment();
            detailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.fragment_container, detailFragment) // ID của container chứa Fragment
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }

    private void openCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Tạo Playlist Mới");

        // Tạo EditText để nhập tên playlist
        final EditText input = new EditText(requireContext());
        input.setHint("Nhập tên playlist");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String playlistName = input.getText().toString().trim();
            if (!playlistName.isEmpty()) {
                createNewPlaylist(playlistName);
            } else {
                Toast.makeText(requireContext(), "Tên playlist không được để trống!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createNewPlaylist(String playlistName) {
        playlistViewModel.createPlaylist(userSession.getUserSession().getId(), playlistName);
        playlistViewModel.getIsAdd().observe(getViewLifecycleOwner(), playlist -> {
            if (playlist == true) {
                Toast.makeText(requireContext(), "Playlist " + playlistName + " đã được tạo!", Toast.LENGTH_SHORT).show();
                refreshPlaylists();
            }
        });
    }

    private void refreshPlaylists() {
        playlistViewModel.fetchAllPlaylist(userSession.getUserSession().getId());
    }
}