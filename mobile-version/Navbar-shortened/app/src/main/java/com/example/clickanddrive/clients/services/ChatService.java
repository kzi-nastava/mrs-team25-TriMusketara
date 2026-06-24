package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.ChatMessageDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ChatService {

    @GET("chat/{userEmail}/{adminEmail}")
    Call<List<ChatMessageDTO>> getHistory(
            @Path("userEmail") String userEmail,
            @Path("adminEmail") String adminEmail
    );

    @POST("chat/send")
    Call<ChatMessageDTO> sendMessage(@Body ChatMessageDTO message);

    @GET("chat/admin/users/{adminEmail}")
    Call<List<String>> getChatUsers(@Path("adminEmail") String adminEmail);

    @PUT("chat/seen/{receiverEmail}/{senderEmail}")
    Call<Void> markAsSeen(
            @Path("receiverEmail") String receiverEmail,
            @Path("senderEmail") String senderEmail
    );
}