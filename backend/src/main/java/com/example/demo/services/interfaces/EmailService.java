package com.example.demo.services.interfaces;

import com.example.demo.model.EmailDetails;

public interface EmailService {
    // Method to send a simple mail
    String sendsSimpleMail(EmailDetails details);

    // Send mail with attachment
    String sendMailWithAttachment(EmailDetails details);
}
