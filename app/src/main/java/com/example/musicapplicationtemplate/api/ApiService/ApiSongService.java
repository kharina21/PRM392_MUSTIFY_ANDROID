package com.example.musicapplicationtemplate.api.ApiService;

import com.example.musicapplicationtemplate.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiSongService {
    @GET("api/songs/getLastestSongs")
    Call<List<Song>> getLastestSongs();

    @GET("api/songs/getAllSongs")
    Call<List<Song>> getAllSongs();

    @POST("api/songs/getSongLikeByUserIdAndSongId")
    @FormUrlEncoded
    Call<Song> getSongLikeByUserIdAndSongId(
            @Field("userId") int userId,
            @Field("songId") int songId
    );

    @POST("api/songs/getListSongsByPlaylistId")
    @FormUrlEncoded
    Call<List<Song>> getListSongsByPlaylistId(@Field("playlistId") int pId);

    @POST("api/songs/deleteSongFromPlaylist")
    @FormUrlEncoded
    Call<ApiResponse> deleteSongFromPlaylist(
            @Field("playlistId") int playlistId,
            @Field("songId") int songId);

    @POST("api/songs/getListSongByTitle")
    @FormUrlEncoded
    Call<List<Song>> getListSongByTitle(@Field("title") String title);
}
