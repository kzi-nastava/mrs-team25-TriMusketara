package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.requests.CreateGuestRideRequest;
import com.example.clickanddrive.dtosample.responses.GuestRideResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GuestRideService {

    @POST("guest-rides/create")
    Call<GuestRideResponseDTO> createGuestRide(@Body CreateGuestRideRequest request);

    @POST("guest-rides/{id}/cancel")
    Call<Void> cancelGuestRide(@Path("id") Long rideId);
}