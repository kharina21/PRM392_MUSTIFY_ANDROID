package com.example.musicapplicationtemplate.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.example.musicapplicationtemplate.model.User;

public class UserSession {
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public UserSession(Context context) {
        this.sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        this.gson = new GsonBuilder()
                .setDateFormat("MMM dd, yyyy") // Định dạng ngày tháng
                .create();
    }

    // Lưu user vào SharedPreferences
    public void saveUserSession(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userJson = gson.toJson(user);
        editor.putString("user_data", userJson);
        editor.apply();
    }

    // Kiểm tra xem user đã đăng nhập chưa
    public boolean isUserLoggedIn() {
        return sharedPreferences.contains("user_data");
    }

    // Lấy dữ liệu user từ SharedPreferences
    public User getUserSession() {
        String userJson = sharedPreferences.getString("user_data", null);
        if (userJson != null) {
            User user = gson.fromJson(userJson, User.class);
            Log.d("UserSession", "User logged in: " + user.getUsername());
            return user;
        }
        return null;
    }

    // Xóa session khi logout
    public void clearUserSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user_data");
        editor.apply();
    }
}
