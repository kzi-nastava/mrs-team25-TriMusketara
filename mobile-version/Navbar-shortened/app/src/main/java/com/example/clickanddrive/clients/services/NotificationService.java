package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.responses.NotificationResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface NotificationService {
    @GET("notifications/{userId}/unread")
    Call<List<NotificationResponse>> getUnread(@Path("userId") Long userId);

    @PATCH("notifications/{notificationId}/read")
    Call<Void> markAsRead(@Path("notificationId") Long notificationId);

    @PATCH("notifications/{userId}/read-all")
    Call<Void> markAllAsRead(@Path("userId") Long userId);
}
