package com.example.clickanddrive;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.clickanddrive.clients.ClientUtils;
import com.example.clickanddrive.dtosample.responses.NotificationResponse;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Manages polling notifications from backend
public class NotificationPollingManager {

    private static final String TAG = "NotificationPolling";
    private static final String CHANNEL_ID = "clickanddrive_notifications";
    private static final int POLL_INTERVAL_MS = 15_000; // Xs

    private static Handler handler;
    private static Runnable pollRunnable;
    private static boolean isRunning = false;
    private static final AtomicInteger notifIdCounter = new AtomicInteger(1000);

    public static void start(Context context) {
        if (isRunning) return;
        isRunning = true;

        createNotificationChannel(context);

        handler = new Handler(Looper.getMainLooper());
        pollRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;
                poll(context);
                handler.postDelayed(this, POLL_INTERVAL_MS);
            }
        };

        // First check immediately
        handler.post(pollRunnable);
        Log.d(TAG, "Polling started for user: " + SessionManager.userId);
    }

    public static void stop() {
        isRunning = false;
        if (handler != null && pollRunnable != null) {
            handler.removeCallbacks(pollRunnable);
        }
        Log.d(TAG, "Polling stopped");
    }

    private static void poll(Context context) {
        Long userId = SessionManager.userId;
        String token = SessionManager.token;
        //Log.d(TAG, "Polling userId=" + userId + " token=" + (token != null ? token.substring(0, 20) + "..." : "NULL"));
        if (userId == null) return;

        ClientUtils.notificationService.getUnread(userId).enqueue(new Callback<List<NotificationResponse>>() {
            @Override
            public void onResponse(Call<List<NotificationResponse>> call,
                                   Response<List<NotificationResponse>> response) {
                //Log.d(TAG, "Poll response: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationResponse> notifications = response.body();
                    //Log.d(TAG, "Received " + notifications.size() + " notifications");
                    for (NotificationResponse notif : notifications) {
                        //Log.d(TAG, "Showing notif: " + notif.getContent());
                        showSystemNotification(context, notif);
                        markAsRead(notif.getId());
                    }
                } else {
                    Log.w(TAG, "Poll error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<NotificationResponse>> call, Throwable t) {
                Log.w(TAG, "Poll failed: " + t.getMessage());
            }
        });
    }

    private static void showSystemNotification(Context context, NotificationResponse notif) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (notif.getRideId() != null) {
            intent.putExtra("NOTIFICATION_RIDE_ID", notif.getRideId());
        }
        intent.putExtra("FROM_NOTIFICATION", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, notif.getRideId() != null ? notif.getRideId().intValue() : 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentTitle("ClickAndDrive")
                .setContentText(notif.getContent())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notif.getContent()))
                .setColor(0xFFF5CB5C)
                .setColorized(false)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        manager.notify(notifIdCounter.getAndIncrement(), builder.build());
    }

    private static void markAsRead(Long notificationId) {
        if (notificationId == null) {
            Log.w(TAG, "markAsRead called with null id");
            return;
        }
        Log.d(TAG, "Calling markAsRead for id=" + notificationId);
        ClientUtils.notificationService.markAsRead(notificationId).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "markAsRead response: " + response.code() + " for id=" + notificationId);
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                Log.w(TAG, "markAsRead failed: " + t.getMessage());
            }
        });
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "ClickAndDrive",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Ride notifications");
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }
}
