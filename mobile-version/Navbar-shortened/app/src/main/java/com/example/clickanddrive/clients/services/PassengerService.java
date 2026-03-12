package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.responses.RouteFromFavoritesResponse;
import com.example.clickanddrive.dtosample.responses.PassengerRideDetailsResponse;
import com.example.clickanddrive.dtosample.responses.PassengerRideHistoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PassengerService {

    // GET for retrieving passengers favorite routes
    @GET("passenger/{passengerId}/favorite-routes")
    Call<List<RouteFromFavoritesResponse>> getFavoriteRoutes(@Path("passengerId") Long passengerId);

    // Remove a route from favorites list
    @DELETE("passenger/{passengerId}/{routeId}/remove-route")
    Call<Void> removeFromFavorites(@Path("passengerId") Long passengerId, @Path("routeId") Long routeId);

    @GET("passenger/{id}/ride-history")
    Call<List<PassengerRideHistoryResponse>> getPassengerRideHistory(@Path("id") Long id);

    @GET("passenger/rides/{rideId}/details")
    Call<PassengerRideDetailsResponse> getRideDetails(@Path("rideId") Long rideId);
}
