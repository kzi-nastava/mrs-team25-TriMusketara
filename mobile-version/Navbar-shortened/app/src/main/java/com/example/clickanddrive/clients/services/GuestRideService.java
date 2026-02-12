package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.requests.CreateGuestRideRequest;
import com.example.clickanddrive.dtosample.requests.CreateRideRequest;
import com.example.clickanddrive.dtosample.responses.GuestRideResponseDTO;
import com.example.clickanddrive.dtosample.responses.RideResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GuestRideService {
    @POST("guest-rides/create")
    Call<GuestRideResponseDTO> createGuestRide(@Body CreateGuestRideRequest request);
}
