package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.responses.UserProfileResponse;
import com.example.clickanddrive.dtosample.responses.VehicleResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DriverService {

    // Get driver vehicle information
    @GET("drivers/{id}/vehicle")
    Call<VehicleResponse> getVehicle(@Path("id") Long id);

}
