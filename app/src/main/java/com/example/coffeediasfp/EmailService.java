package com.example.coffeediasfp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService extends IntentService {
    private static final String TAG = "EmailService";

    // Callback interface definition
    public interface EmailCallback {
        void onEmailSent();
        void onEmailFailed();
    }

    private EmailCallback callback;

    public EmailService() {
        super("EmailService");
    }

    // Setter method to set the callback instance
    public void setCallback(EmailCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Check if the intent is null
        if (intent == null) {
            Log.e(TAG, "Intent is null");
            return;
        }

        // Get email details from intent
        String recipientEmail = intent.getStringExtra("recipientEmail");
        String subject = intent.getStringExtra("subject");
        String body = intent.getStringExtra("body");

        // Check if any of the necessary extras are null
        if (recipientEmail == null || subject == null || body == null) {
            Log.e(TAG, "One or more extras are null");
            return;
        }

        // Email sender credentials
        final String username = "wawerufndegwa@gmail.com";
        final String password = "wgir fdwx luth izak";

        // Email server properties
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // Create a mail session with authentication
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create a MimeMessage
            MimeMessage message = new MimeMessage(session);
            // Set From: header field
            message.setFrom(new InternetAddress(username));
            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            // Set Subject: header field
            message.setSubject(subject);
            // Set the actual message
            message.setText(body);
            // Send the message
            Transport.send(message);
            Log.d(TAG, "Email sent successfully to: " + recipientEmail);
            // Invoke the onEmailSent method of the callback
            if (callback != null) {
                callback.onEmailSent();
            }
        } catch (MessagingException e) {
            Log.e(TAG, "Failed to send email", e);
            if (callback != null) {
                callback.onEmailFailed();
            }
        }
    }
}
