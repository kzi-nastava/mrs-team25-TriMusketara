package com.example.clickanddrive.clients;

import com.example.clickanddrive.BuildConfig;
import com.example.clickanddrive.SessionManager;
import com.example.clickanddrive.clients.services.AdminService;
import com.example.clickanddrive.clients.services.DriverService;
import com.example.clickanddrive.clients.services.RideService;
import com.example.clickanddrive.clients.services.UserService;
import com.example.clickanddrive.clients.services.GuestRideService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {
    public static final String SERVICE_API_PATH = "http://" + BuildConfig.IP_ADDR + ":8080/api/";

    /*
     * Ovo ce nam sluziti za debug, da vidimo da li zahtevi i odgovori idu
     * odnosno dolaze i kako izgeldaju.
     * */
    public static OkHttpClient createHttpClient(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor);

        builder.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();

            if (SessionManager.token != null && !SessionManager.token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + SessionManager.token);
            }

            return chain.proceed(requestBuilder.build());
        });

        return builder.build();
    }

    /*
     * Prvo je potrebno da definisemo retrofit instancu preko koje ce komunikacija ici
     * */
    static Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
        new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))).create();

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(createHttpClient())
            .build();

    // Instances of services
    // ...
    public static UserService userService = retrofit.create(UserService.class);
    public static DriverService driverService = retrofit.create(DriverService.class);
    public static RideService rideService = retrofit.create(RideService.class);
    public static GuestRideService guestRideService = retrofit.create(GuestRideService.class);
    public static AdminService adminService = retrofit.create(AdminService.class);
}
