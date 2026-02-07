package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.requests.ChangePasswordRequest;
import com.example.clickanddrive.dtosample.requests.UpdateProfileRequest;
import com.example.clickanddrive.dtosample.responses.ProfileImageResponse;
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    // Allow user to upload profile photo
    @Multipart
    @POST("user/{id}/profile-image")
    Call<ProfileImageResponse> uploadProfileImage(@Path("id") Long userId, @Part MultipartBody.Part file);

    // Allow user to remove profile photo
    @DELETE("user/{id}/delete-profile-image")
    Call<Void> deleteProfileImage(@Path("id") Long userId);

    // Get profile photo
    @GET("user/profile-images/{filename}")
    Call<ResponseBody> getProfileImage(@Path("filename") String filename);

}
