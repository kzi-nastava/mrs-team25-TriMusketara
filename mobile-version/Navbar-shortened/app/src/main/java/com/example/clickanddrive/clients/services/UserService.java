package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.requests.ChangePasswordRequest;
import com.example.clickanddrive.dtosample.requests.UpdateProfileRequest;
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    // Get user profile information
    @GET("user/{id}/profile")
    Call<UserProfileResponse> getUserProfile(@Path("id") Long id);

    // Update user profile information
    @PUT("user/{id}/profile-update")
    Call<UserProfileResponse> updateUserProfile(@Path("id") Long id, @Body UpdateProfileRequest request);

    // Change password
    @POST("user/change-password")
    Call<Map<String, String>> changePassword(@Body ChangePasswordRequest request);
}
