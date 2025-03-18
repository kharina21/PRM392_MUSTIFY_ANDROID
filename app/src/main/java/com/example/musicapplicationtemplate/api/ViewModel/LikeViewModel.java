package com.example.musicapplicationtemplate.api.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapplicationtemplate.api.ApiService.ApiClient;
import com.example.musicapplicationtemplate.api.ApiService.ApiLikeService;
import com.example.musicapplicationtemplate.api.ApiService.ApiResponse;
import com.example.musicapplicationtemplate.model.Like;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeViewModel extends ViewModel {

    private MutableLiveData<List<Like>> songsLikeByUserId = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private MutableLiveData<Boolean> isAdded = new MutableLiveData<>();

    private MutableLiveData<Boolean> isDelete = new MutableLiveData<>();

    private ApiLikeService als = ApiClient.getClient().create(ApiLikeService.class);

    public void fetchSongsLikeByUserId(int uId) {
        als.getListSongLikeByUserId(uId).enqueue(new Callback<List<Like>>() {
            @Override
            public void onResponse(Call<List<Like>> call, Response<List<Like>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    songsLikeByUserId.postValue(response.body());
                } else {
                    errorMessage.postValue("fail to get list songs like");
                }
            }

            @Override
            public void onFailure(Call<List<Like>> call, Throwable t) {
                errorMessage.postValue("connection error: " + t);
            }
        });
    }

    public void fetchAddSongToListLike(int userId, int songId){
        als.addSongToListLike(userId,songId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
                    isAdded.postValue(true);
                }else{
                    isAdded.postValue(false);
                    errorMessage.postValue("fail to add song to like list");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                    isAdded.postValue(false);
                    errorMessage.postValue("connection error: "+t);
            }
        });
    }

    public void fetchDeleteSongInListLike(int uId, int sId){
        als.deleteSongInListLike(uId,sId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
                    isDelete.postValue(true);
                }else{
                    isDelete.postValue(false);
                    errorMessage.postValue("fail to delete song in list like");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isDelete.postValue(false);
                errorMessage.postValue("connection erre: "+t);
            }
        });
    }

    public LiveData<List<Like>> getSongsLikeByUserId() {
        return songsLikeByUserId;
    }

    public LiveData<String> errorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> isAdded(){
        return isAdded;
    }

    public LiveData<Boolean> isDelete(){
        return isDelete;
    }

}
