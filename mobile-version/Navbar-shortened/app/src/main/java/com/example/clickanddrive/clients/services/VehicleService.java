package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.responses.ActiveVehicleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface VehicleService {

    @GET("vehicles/active")
    Call<List<ActiveVehicleResponse>> getActiveVehicles();
}