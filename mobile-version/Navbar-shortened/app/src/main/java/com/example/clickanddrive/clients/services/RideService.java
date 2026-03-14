package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.requests.CreateRideRequest;
import com.example.clickanddrive.dtosample.requests.RideCancellationRequest;
import com.example.clickanddrive.dtosample.responses.RideResponse;
import com.example.clickanddrive.dtosample.responses.ScheduledRideResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RideService {

    @POST("rides/create-ride")
    Call<RideResponse> createRide(@Body CreateRideRequest request);

    @GET("rides/driver/{driverId}")
    Call<PageResponse<ScheduledRideResponse>> getScheduledRides(@Path("driverId") Long driverId);

    @POST("rides/{id}/start")
    Call<Void> startRide(@Path("id") Long rideId, @Body Map<String, Boolean> body);

    @POST("rides/cancel/{id}")
    Call<Void> cancelRide(@Path("id") Long rideId, @Body RideCancellationRequest request);

    class PageResponse<T> {
        private List<T> content;
        private int totalPages;

        public List<T> getContent() {
            return content;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }
}