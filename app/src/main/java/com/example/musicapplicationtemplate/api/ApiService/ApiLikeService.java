package com.example.musicapplicationtemplate.api.ApiService;

import com.example.musicapplicationtemplate.model.Like;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiLikeService {
    @POST("/api/like/deleteSongInListLike")
    @FormUrlEncoded
    Call<ApiResponse> deleteSongInListLike(
            @Field("userId") int userId,
            @Field("songId") int songId
    );

    @POST("/api/like/getListSongLikeByUserId")
    @FormUrlEncoded
    Call<List<Like>> getListSongLikeByUserId(
            @Field("userId") int userId
    );

    @POST("/api/like/addSongToListLike")
    @FormUrlEncoded
    Call<ApiResponse> addSongToListLike(
            @Field("userId") int userId,
            @Field("songId") int songId
    );
}
