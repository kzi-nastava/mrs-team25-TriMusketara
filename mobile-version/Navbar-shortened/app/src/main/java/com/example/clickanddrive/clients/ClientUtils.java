package com.example.clickanddrive.clients;

import com.example.clickanddrive.BuildConfig;
import com.example.clickanddrive.SessionManager;
import com.example.clickanddrive.clients.services.AdminService;
import com.example.clickanddrive.clients.services.DriverService;
import com.example.clickanddrive.clients.services.NotificationService;
import com.example.clickanddrive.clients.services.PanicService;
import com.example.clickanddrive.clients.services.PassengerService;
import com.example.clickanddrive.clients.services.ReportService;
import com.example.clickanddrive.clients.services.RideService;
import com.example.clickanddrive.clients.services.UserService;
import com.example.clickanddrive.clients.services.GuestRideService;
import com.example.clickanddrive.clients.services.VehicleService;
import com.example.clickanddrive.clients.services.ChatService;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

import java.time.LocalDate;
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
    static Gson gson = new GsonBuilder()
            // LocalDate: prima JSON array [year, month, day] sa backenda
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, ctx) -> {
                if (json.isJsonArray()) {
                    JsonArray arr = json.getAsJsonArray();
                    return LocalDate.of(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());
                } else {
                    return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
                }
            })
            // LocalDateTime: jedan TypeAdapter koji radi i slanje i primanje
            .registerTypeAdapter(LocalDateTime.class, new com.google.gson.TypeAdapter<LocalDateTime>() {
                @Override
                public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value) throws java.io.IOException {
                    if (value == null) { out.nullValue(); return; }
                    out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
                @Override
                public LocalDateTime read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
                    if (in.peek() == com.google.gson.stream.JsonToken.NULL) { in.nextNull(); return null; }
                    if (in.peek() == com.google.gson.stream.JsonToken.BEGIN_ARRAY) {
                        in.beginArray();
                        int year = in.nextInt(), month = in.nextInt(), day = in.nextInt();
                        int hour = 0, min = 0, sec = 0;
                        if (in.hasNext()) hour = in.nextInt();
                        if (in.hasNext()) min  = in.nextInt();
                        if (in.hasNext()) sec  = in.nextInt();
                        while (in.hasNext()) in.skipValue(); // nano i ostalo
                        in.endArray();
                        return LocalDateTime.of(year, month, day, hour, min, sec);
                    } else {
                        return LocalDateTime.parse(in.nextString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                }
            })
            .create();

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
    public static PassengerService passengerService = retrofit.create(PassengerService.class);
    public static PanicService panicService = retrofit.create(PanicService.class);

    public static VehicleService vehicleService = retrofit.create(VehicleService.class);

    public static ChatService chatService = retrofit.create(ChatService.class);

    public static ReportService reportService = retrofit.create(ReportService.class);

    public static NotificationService notificationService = retrofit.create(NotificationService.class);
}
