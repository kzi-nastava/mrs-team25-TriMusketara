package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.requests.DriverRegistrationRequest;
import com.example.clickanddrive.dtosample.responses.DriverRegistrationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AdminService {

    @POST("admin/drivers")
    Call<DriverRegistrationResponse> registerDriver(@Body DriverRegistrationRequest request, @Query("platform") String platform);
}
