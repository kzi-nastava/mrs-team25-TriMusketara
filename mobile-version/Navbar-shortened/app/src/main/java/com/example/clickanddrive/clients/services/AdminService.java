package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.requests.DriverRegistrationRequest;
import com.example.clickanddrive.dtosample.requests.NoteRequest;
import com.example.clickanddrive.dtosample.responses.DriverRegistrationResponse;
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;
import com.example.clickanddrive.dtosample.responses.UserProfileResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminService {

    @POST("admin/drivers")
    Call<DriverRegistrationResponse> registerDriver(@Body DriverRegistrationRequest request, @Query("platform") String platform);

    // GET all drivers from database
    @GET("admin/drivers/all")
    Call<List<UserProfileResponse>> getAllDrivers();

    // GET all passengers from database
    @GET("admin/passengers/all")
    Call<List<UserProfileResponse>> getAllPassengers();

    // Block a user
    @PUT("admin/users/{id}/block")
    Call<UserProfileResponse> blockUser(@Path("id") Long id, @Body NoteRequest request);

    // Unblock a blocked user
    @PUT("admin/users/{id}/unblock")
    Call<UserProfileResponse> unblockUser(@Path("id") Long id);

    @PUT("admin/users/{id}/note")
    Call<UserProfileResponse> leaveNote(@Path("id") Long id, @Body NoteRequest request);
}
