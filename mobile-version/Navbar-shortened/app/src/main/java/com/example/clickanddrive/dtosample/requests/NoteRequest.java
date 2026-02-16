package com.example.clickanddrive.dtosample.requests;

import androidx.annotation.NonNull;

public class NoteRequest {
    private String message;

    public NoteRequest() {}

    public NoteRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public String toString() {
        return "NoteRequest{" +
                "message='" + message + '\'' +
                '}';
    }
}
