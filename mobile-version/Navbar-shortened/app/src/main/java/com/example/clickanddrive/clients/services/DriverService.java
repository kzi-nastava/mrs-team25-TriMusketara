package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.requests.CompleteRegistrationRequest;
import com.example.clickanddrive.dtosample.responses.DriverRideHistoryResponse;
import com.example.clickanddrive.dtosample.responses.UserProfileResponse;
import com.example.clickanddrive.dtosample.responses.VehicleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DriverService {

    // Get driver vehicle information
    @GET("drivers/{id}/vehicle")
    Call<VehicleResponse> getVehicle(@Path("id") Long id);

    @GET("drivers/{id}/ride-history")
    Call<List<DriverRideHistoryResponse>> getDriverHistory(@Path("id") Long id);

    // Complete driver registration
    @POST("drivers/complete-registration")
    Call<String> completeRegistration(@Body CompleteRegistrationRequest request);

}
