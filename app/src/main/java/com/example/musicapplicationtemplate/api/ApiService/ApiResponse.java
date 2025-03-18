package com.example.musicapplicationtemplate.api.ApiService;

public class ApiResponse {
    private boolean success;
    private boolean exists;
    private String message;

    public ApiResponse(boolean success, String message, boolean exists) {
        this.success = success;
        this.message = message;
        this.exists = exists;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isExists() {
        return exists;
    }

    public String getMessage() {
        return message;
    }
}
