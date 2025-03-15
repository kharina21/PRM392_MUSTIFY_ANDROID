package com.example.musicapplicationtemplate.ui.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.ui.activities.LoginActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;
import com.example.musicapplicationtemplate.utils.UserSession;


public class ProfileFragment extends Fragment {

private LinearLayout btnLogout;

private MusicPlayerManager musicPlayerManager;
private UserSession us;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        us = new UserSession(requireContext());
        musicPlayerManager = MusicPlayerManager.getInstance();
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v->toggleLogout());

        return view;
    }

    private void toggleLogout(){
        us.clearUserSession();
        //chuyen ve login activity
       musicPlayerManager.clearCurrentSong();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        //xóa tất cả các activity trước đó
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}