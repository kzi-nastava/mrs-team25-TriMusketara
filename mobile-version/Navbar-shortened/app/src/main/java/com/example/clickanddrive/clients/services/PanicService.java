package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.requests.PanicRequest;
import com.example.clickanddrive.dtosample.responses.PanicResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PanicService {

    @POST("panic")
    Call<PanicResponse> triggerPanic(@Body PanicRequest request);

    @GET("panic/unresolved")
    Call<List<PanicResponse>> getUnresolvedPanics();

    @GET("panic/all")
    Call<List<PanicResponse>> getAllPanics();

    @PUT("panic/{id}/resolve")
    Call<Void> resolvePanic(@Path("id") Long panicId);
}