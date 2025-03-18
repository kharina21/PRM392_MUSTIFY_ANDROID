package com.example.musicapplicationtemplate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapplicationtemplate.api.ApiClient;
import com.example.musicapplicationtemplate.api.ApiSongService;
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

    private final ApiSongService ass = ApiClient.getClient().create(ApiSongService.class);

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
