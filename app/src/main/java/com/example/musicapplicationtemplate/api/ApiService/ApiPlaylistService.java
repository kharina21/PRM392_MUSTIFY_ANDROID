package com.example.musicapplicationtemplate.api.ApiService;

import com.example.musicapplicationtemplate.model.Playlist;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiPlaylistService {

    @POST("/api/playlist/getListPlaylistContainSong")
    @FormUrlEncoded
    Call<List<Playlist>> getListPlaylistContainSong(
            @Field("userId") int userId,
            @Field("songId") int songId
    );

    @POST("/api/playlist/getListPlaylistNotContainSong")
    @FormUrlEncoded
    Call<List<Playlist>> getListPlaylistNotContainSong(
            @Field("userId") int userId,
            @Field("songId") int songId
    );

    @POST("/api/playlist/addSongToPlaylist")
    @FormUrlEncoded
    Call<ApiResponse> addSongToPlaylist(
            @Field("playlistId") int pId,
            @Field("songId") int sId
    );

    @POST("/api/playlist/deleteSongToPlaylist")
    @FormUrlEncoded
    Call<ApiResponse> deleteSongToPlaylist(
            @Field("playlistId") int pId,
            @Field("songId") int sId
    );

   @POST("/api/playlist/createPlaylist")
    @FormUrlEncoded
    Call<ApiResponse>createPlaylist(
           @Field("userId") int uId,
            @Field("playlistName") String name

   );

    @POST("/api/playlist/deletePlaylist")
    @FormUrlEncoded
    Call<ApiResponse>deletePlaylist(
            @Field("playlistId") int playlistId
    );

    @POST("/api/playlist/getAllPlaylist")
    @FormUrlEncoded
    Call<List<Playlist>> getAllPlaylist(
            @Field("userId") int uId
    );
}
