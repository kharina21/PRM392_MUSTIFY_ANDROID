package com.example.musicapplicationtemplate.api;

import com.example.musicapplicationtemplate.model.Like;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiLikeService {
    @POST("/api/like/removeSongInListLike")
    @FormUrlEncoded
    Call<ApiResponse> removeSongInListLike(
            @Field("userId") int userId,
            @Field("songId") int songId
    );

    @POST("/api/like/getListSongLikeByUserId")
    @FormUrlEncoded
    Call<List<Like>> getListSongLikeByUserId(
            @Field("userId") int userId
    );
}
