package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.requests.CreateRideRequest;
import com.example.clickanddrive.dtosample.responses.RideResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RideService {

    // Registered user
    // Creating a ride order
    @POST("rides/create-ride")
    Call<RideResponse> createRide(@Body CreateRideRequest request);
}
