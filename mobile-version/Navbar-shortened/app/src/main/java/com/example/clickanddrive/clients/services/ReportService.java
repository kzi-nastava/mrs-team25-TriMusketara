package com.example.clickanddrive.clients.services;

import com.example.clickanddrive.dtosample.requests.ReportRequest;
import com.example.clickanddrive.dtosample.responses.ReportResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ReportService {

    @POST("reports/generate")
    Call<ReportResponse> generateReport(@Body ReportRequest request);
}
