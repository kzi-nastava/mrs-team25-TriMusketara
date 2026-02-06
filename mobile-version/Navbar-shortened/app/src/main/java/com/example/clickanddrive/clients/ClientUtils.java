package com.example.clickanddrive.clients;

import com.example.clickanddrive.BuildConfig;
import com.example.clickanddrive.clients.services.DriverService;
import com.example.clickanddrive.clients.services.UserService;

import okhttp3.logging.HttpLoggingInterceptor;
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
    public static OkHttpClient test(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();

        return client;
    }

    /*
     * Prvo je potrebno da definisemo retrofit instancu preko koje ce komunikacija ici
     * */
    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .client(test())
            .build();

    // Instances of services
    // ...
    public static UserService userService = retrofit.create(UserService.class);
    public static DriverService driverService = retrofit.create(DriverService.class);
}
