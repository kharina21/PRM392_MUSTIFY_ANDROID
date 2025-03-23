package com.example.musicapplicationtemplate.api.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapplicationtemplate.api.ApiService.ApiClient;
import com.example.musicapplicationtemplate.api.ApiService.ApiPlaylistService;
import com.example.musicapplicationtemplate.api.ApiService.ApiRecentlyPlayedService;
import com.example.musicapplicationtemplate.api.ApiService.ApiResponse;
import com.example.musicapplicationtemplate.model.Playlist;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistViewModel extends ViewModel {
    private final ApiPlaylistService apls = ApiClient.getClient().create(ApiPlaylistService.class);

    private MutableLiveData<List<Playlist>> listPlaylistContainSong = new MutableLiveData<>();
    private MutableLiveData<List<Playlist>> listPlaylistNotContainSong = new MutableLiveData<>();
    private MutableLiveData<List<Playlist>> allPlaylist = new MutableLiveData<>();

    private MutableLiveData<Boolean> isAdd = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDelete = new MutableLiveData<>();

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public void fetchAllPlaylist(int uId) {
        apls.getAllPlaylist(uId).enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allPlaylist.postValue(response.body());
                    Log.d("PlaylistViewModel", "list playlist: "+response.body());
                } else {
                    String error = "Failed to get playlist. Code: " + response.code();
                    Log.e("PlaylistViewModel", error);
                    errorMessage.postValue(error);
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                String error = "Connection error: " + t.getMessage();
                Log.e("PlaylistViewModel", error);
                errorMessage.postValue(error);
            }
        });
    }

    public void fetchListPlaylistContainSong(int userId, int songId) {
        apls.getListPlaylistContainSong(userId, songId).enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listPlaylistContainSong.postValue(response.body());
                } else {
                    errorMessage.postValue("fail to get listPlaylistContainSong");
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                errorMessage.postValue("connection error: " + t);
            }
        });
    }

    public void addSongToPlaylist(int playlistId, int songId) {
        apls.addSongToPlaylist(playlistId, songId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    public void createPlaylist(int uId, String pName) {
        apls.createPlaylist(uId, pName).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    isAdd.postValue(true);
                } else {
                    isAdd.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isAdd.postValue(false);
            }
        });
    }

    public void deletePlaylist(int pId) {
        apls.deletePlaylist(pId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    isDelete.postValue(true);
                } else {
                    isDelete.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isDelete.postValue(false);
            }
        });
    }

    public void deleteSongToPlaylist(int playlistId, int songId) {
        apls.deleteSongToPlaylist(playlistId, songId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    public LiveData<List<Playlist>> getListPlaylistContainSong() {
        return listPlaylistContainSong;
    }

    public void fetchListPlaylistNotContainSong(int userId, int songId) {
        apls.getListPlaylistNotContainSong(userId, songId).enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listPlaylistNotContainSong.postValue(response.body());
                } else {
                    errorMessage.postValue("fail to get listPlaylistNotContainSong");
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                errorMessage.postValue("connection error: " + t);
            }
        });
    }

 public LiveData<List<Playlist>> getAllPlaylist(){
        return allPlaylist;
 }
    public LiveData<List<Playlist>> getListPlaylistNotContainSong() {
        return listPlaylistNotContainSong;
    }

    public LiveData<Boolean> getIsAdd() {
        return isAdd;
    }

    public LiveData<Boolean> getIsDelete() {
        return isDelete;
    }
}
