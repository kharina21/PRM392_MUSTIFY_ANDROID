package com.example.musicapplicationtemplate.api.ApiService;

import com.example.musicapplicationtemplate.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiSongService {
    @GET("api/songs/getLastestSongs")
    Call<List<Song>> getLastestSongs();

    @GET("api/songs/getAllSongs")
    Call<List<Song>> getAllSongs();
}
