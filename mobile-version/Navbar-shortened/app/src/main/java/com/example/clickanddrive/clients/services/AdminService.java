package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.requests.DriverRegistrationRequest;
import com.example.clickanddrive.dtosample.requests.NoteRequest;
import com.example.clickanddrive.dtosample.responses.AdminRideDetailsResponse;
import com.example.clickanddrive.dtosample.responses.AdminRideHistoryResponse;
import com.example.clickanddrive.dtosample.responses.AdminUserResponse;
import com.example.clickanddrive.dtosample.responses.DriverRegistrationResponse;
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;

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
    Call<DriverRegistrationResponse> registerDriver(
            @Body DriverRegistrationRequest request,
            @Query("platform") String platform
    );

    @GET("admin/drivers/all")
    Call<List<UserProfileResponse>> getAllDrivers();

    @GET("admin/passengers/all")
    Call<List<UserProfileResponse>> getAllPassengers();

    @PUT("admin/users/{id}/block")
    Call<UserProfileResponse> blockUser(
            @Path("id") Long id,
            @Body NoteRequest request
    );

    @PUT("admin/users/{id}/unblock")
    Call<UserProfileResponse> unblockUser(@Path("id") Long id);

    @PUT("admin/users/{id}/note")
    Call<UserProfileResponse> leaveNote(
            @Path("id") Long id,
            @Body NoteRequest request
    );

    @GET("admin/ride-history")
    Call<List<AdminRideHistoryResponse>> getRideHistory(
            @Query("id") Long id,
            @Query("role") String role,
            @Query("sortBy") String sortBy
    );

    @GET("admin/users")
    Call<List<AdminUserResponse>> getAllUsers();

    @GET("admin/rides/{id}")
    Call<AdminRideDetailsResponse> getRideDetails(@Path("id") Long rideId);
}