package com.example.musicapplicationtemplate.api.ApiService;

import com.example.musicapplicationtemplate.model.RecentlyPlayed;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiRecentlyPlayedService {
    @POST("api/recentlyPlayed/get10SongsRecentlyPlayedByUserId")
    @FormUrlEncoded
    Call<List<RecentlyPlayed>> get10SongsRecentlyPlayedByUserId(
            @Field("userId") int userId
    );

    @POST("api/recentlyPlayed/addSongPlayed")
    @FormUrlEncoded
    Call<ApiResponse>addSongPlayed(
            @Field("userId") int userId,
            @Field("songId") int songId
    );
}
