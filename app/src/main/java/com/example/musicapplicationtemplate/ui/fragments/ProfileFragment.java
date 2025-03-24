package com.example.musicapplicationtemplate.ui.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.musicapplicationtemplate.R;
import com.example.musicapplicationtemplate.model.User;
import com.example.musicapplicationtemplate.ui.activities.LoginActivity;
import com.example.musicapplicationtemplate.utils.MusicPlayerManager;
import com.example.musicapplicationtemplate.utils.UserSession;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class ProfileFragment extends Fragment {

private LinearLayout btnLogout;
private TextView tvFullName,tvUsername,tvPhone,tvEmail,tvJoinedDate;

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
        User user = us.getUserSession();
        musicPlayerManager = MusicPlayerManager.getInstance();
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v->toggleLogout());

        tvFullName = view.findViewById(R.id.tvFullName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvJoinedDate = view.findViewById(R.id.tvJoinedDate);

        tvFullName.setText(user.getFirst_name()+" "+user.getLast_name());
        tvUsername.setText(user.getUsername());
        tvPhone.setText(user.getPhone());
        tvEmail.setText(user.getEmail());
        // Format Date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(user.getJoined_date());
        tvJoinedDate.setText(formattedDate);


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