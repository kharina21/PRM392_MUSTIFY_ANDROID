package com.example.musicapplicationtemplate.api.ApiService;

import com.example.musicapplicationtemplate.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiUserService {
    @POST("api/users/getExistUser")
    @FormUrlEncoded
    Call<User> getExistUser(
            @Field("usernameOrEmail")String usernameOrEmail,
            @Field("password")String password
    );

    @POST("api/users/getUserByUsernameOrEmail")
    @FormUrlEncoded
    Call<User> getUserByUsernameOrEmail(
            @Field("usernameOrEmail")String usernameOrEmail
    );

    @POST("api/users/changePasswordByUsername")
    @FormUrlEncoded
    Call<ApiResponse> changePasswordByUsername (
            @Field("username")String username,
            @Field("password")String newPassword
    );

    @POST("api/users/checkEmailExist")
    @FormUrlEncoded
    Call<ApiResponse> checkEmailExist(
            @Field("email")String email
    );

    @POST("api/users/checkUsernameExist")
    @FormUrlEncoded
    Call<ApiResponse> checkUsernameExist(
            @Field("username") String username
    );

    @POST("api/users/addUser")
    Call<ApiResponse> addUser (@Body User user);

    @POST("api/users/getAllUser")
    Call<List<User>> getAllUser();

    @POST("api/users/changeActiveStatusByUsername")
    @FormUrlEncoded
    Call<ApiResponse> changeActiveStatusByUsername(
            @Field("username") String username,
            @Field("active") boolean active
    );
}
