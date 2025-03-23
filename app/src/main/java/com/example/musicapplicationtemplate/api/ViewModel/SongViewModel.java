package com.example.musicapplicationtemplate.api.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapplicationtemplate.api.ApiService.ApiClient;
import com.example.musicapplicationtemplate.api.ApiService.ApiLikeService;
import com.example.musicapplicationtemplate.api.ApiService.ApiResponse;
import com.example.musicapplicationtemplate.api.ApiService.ApiSongService;
import com.example.musicapplicationtemplate.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongViewModel extends ViewModel {
    private final MutableLiveData<List<Song>> lastestSongs = new MutableLiveData<>();
    private final MutableLiveData<List<Song>> allSongs = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final MutableLiveData<Song> songLikeByUserIdAndSongId = new MutableLiveData<>();
    private final ApiSongService ass = ApiClient.getClient().create(ApiSongService.class);

    public void fetchSongLikeByUserIdAndSongId(int userId, int songId){
        ass.getSongLikeByUserIdAndSongId(userId, songId).enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response) {
                if(response.isSuccessful() && response.body() != null){
                    songLikeByUserIdAndSongId.postValue(response.body());
                }else{
                    errorMessage.postValue("fail to get songLikeByUserIdAndSongId");
                }
            }

            @Override
            public void onFailure(Call<Song> call, Throwable t) {
                errorMessage.postValue("connection error: "+t);
            }
        });
    }

    public void addSongToLikes(int uId, int sId){
        ApiLikeService als = ApiClient.getClient().create(ApiLikeService.class);
        als.addSongToListLike(uId,sId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    public void removeSongFromLikes(int uId, int sId){
        ApiLikeService als = ApiClient.getClient().create(ApiLikeService.class);
        als.deleteSongInListLike(uId,sId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }
    public void fetchLastestSongs() {
        isLoading.postValue(true);
        ass.getLastestSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    lastestSongs.postValue(response.body());
                } else {
                    errorMessage.postValue("fail to get list 5 songs");
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                isLoading.postValue(false);
                errorMessage.postValue("connection error: " + t);
            }
        });
    }

    public void fetchAllSongs() {
        isLoading.postValue(true);
        ass.getAllSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    isLoading.postValue(true);
                    allSongs.postValue(response.body());
                } else {
                    errorMessage.postValue("fail to get all songs");
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                errorMessage.postValue("connection error: " + t);
            }
        });
    }

    // Getter cho LiveData
    public LiveData<Song> getSongLikeByUserIdAndSongId(){
        return songLikeByUserIdAndSongId;
    }
    public LiveData<List<Song>> getLatestSongs() {
        return lastestSongs;
    }

    public LiveData<List<Song>> getAllSongs(){
        return allSongs;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }

}
