package com.example.demo.services;

import com.example.demo.model.EmailDetails;
import com.example.demo.model.Passenger;
import com.example.demo.services.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String sender;

    @Override
    public String sendsSimpleMail(EmailDetails details) {
        try {
            // Creating a simple mail message
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            // Sending the email
            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }
        catch (Exception e) {
            return "Error while Sending Mail";
        }
    }

    @Override
    public String sendMailWithAttachment(EmailDetails details) {
        // Creating a MimeMessage
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(details.getSubject());

            // Adding attachment
            FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(file.getFilename(), file);

            // Sending mail
            javaMailSender.send(mimeMessage);
            return "Mail sent successfully";
        }
        catch(MessagingException e) {
            return "Error while sending mail";
        }
    }

    @Override
    public void sendActivationEmail(Passenger passenger) {
        try {
            String activationLink = "http://localhost:4200/activate-account?token=" + passenger.getActivationToken();
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(passenger.getEmail());
            helper.setSubject("Activate your account");
            helper.setText(
                    "<p>Hello " + passenger.getName() + ",</p>" +
                            "<p>Please click the link below to activate your account (valid for 24h):</p>" +
                            "<a href=\"" + activationLink + "\">Activate Account</a>",
                    true
            );
            javaMailSender.send(message);
            System.out.println(activationLink);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send activation email", e);
        }
    }
}
