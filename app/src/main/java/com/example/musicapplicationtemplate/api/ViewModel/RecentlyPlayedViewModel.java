package com.example.musicapplicationtemplate.api.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapplicationtemplate.api.ApiService.ApiClient;
import com.example.musicapplicationtemplate.api.ApiService.ApiRecentlyPlayedService;
import com.example.musicapplicationtemplate.api.ApiService.ApiResponse;
import com.example.musicapplicationtemplate.model.RecentlyPlayed;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentlyPlayedViewModel extends ViewModel {
    private final ApiRecentlyPlayedService arps = ApiClient.getClient().create(ApiRecentlyPlayedService.class);
    private final MutableLiveData<List<RecentlyPlayed>> songsRecentlyPlayedByUserId = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isAddSongRecentlyPlayed = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public void fetchSongsRecentlyPlayedByUserId(int uId) {
        isLoading.postValue(true);
        arps.get10SongsRecentlyPlayedByUserId(uId).enqueue(new Callback<List<RecentlyPlayed>>() {
            @Override
            public void onResponse(Call<List<RecentlyPlayed>> call, Response<List<RecentlyPlayed>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    isLoading.postValue(true);
                    songsRecentlyPlayedByUserId.postValue(response.body());
                } else {
                    errorMessage.postValue("fail to get recently played list");
                }
            }

            @Override
            public void onFailure(Call<List<RecentlyPlayed>> call, Throwable t) {
                errorMessage.postValue("connection error: " + t);
            }
        });
    }

    public void fetchIsAddRecentlyPlayed (int uId, int sId){
        arps.addSongPlayed(uId,sId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    isAddSongRecentlyPlayed.postValue(response.body().isSuccess());
                }else{
                    errorMessage.postValue("fail to add recently played song");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                    errorMessage.postValue("connection error: "+t);
            }
        });
    }

    public LiveData<List<RecentlyPlayed>> getSongsRecentlyPlayedByUserId() {
        return songsRecentlyPlayedByUserId;
    }

    public LiveData<Boolean> getIsAddSongRecentlyPlayed() {
        return isAddSongRecentlyPlayed;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

}
