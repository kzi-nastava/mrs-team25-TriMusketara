package com.example.clickanddrive.dtosample.responses;

import androidx.annotation.NonNull;

public class ProfileImageResponse {
    private String profileImageUrl;
    private String message;

    public ProfileImageResponse() {}

    public ProfileImageResponse(String profileImageUrl, String message) {
        this.profileImageUrl = profileImageUrl;
        this.message = message;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public String toString() {
        return "ProfileImageResponse{" +
                "profileImageUrl='" + profileImageUrl + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
