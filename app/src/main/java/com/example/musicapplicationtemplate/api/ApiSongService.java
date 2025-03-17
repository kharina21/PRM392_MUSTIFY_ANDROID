package com.example.musicapplicationtemplate.api;

import com.example.musicapplicationtemplate.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiSongService {
    @GET("api/songs/getLastest5Songs")
    Call<List<Song>> getLastest5Songs();

    @GET("api/songs/getAllSongs")
    Call<List<Song>> getAllSongs();
}
