package com.example.musicapplicationtemplate.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;
public class Utils {
    public String generateConfirmationCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    // Gửi email xác nhận trên một luồng nền
    public void sendEmailInBackground(String toEmail, String subject, String usernameApp, String confirmationCode, String content) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    sendEmailConfirmationCode(toEmail, subject, usernameApp, confirmationCode, content);
                    return true;
                } catch (Exception e) {
                    Log.e("SendEmail", "Error sending email", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Log.i("SendEmail", "Email sent successfully!");
                } else {
                    Log.e("SendEmail", "Failed to send email.");
                }
            }
        }.execute();
    }

    private void sendEmailConfirmationCode(String toEmail, String subject, String usernameApp, String confirmationCode, String content) throws MessagingException, UnsupportedEncodingException {
        final String username = "fcareinsurance@gmail.com";
        final String password = "cifxowsnfwdnywed"; // App Password từ Gmail

        // Cấu hình SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Tạo session với thông tin xác thực
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // Nội dung email HTML
        String messageContent = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head><meta charset='utf-8'></head>\n"
                + "<body>\n"
                + "<h1 style=\"font-weight: bold; color:#1DB954\">Mustify</h1>\n"
                + "<h5>Xin chào <b>" + usernameApp + "</b></h5>\n"
                + "<p>" + content + confirmationCode+" </p>\n"
                + "<p>Chúc bạn có một ngày vui vẻ!</p>\n"
                + "</body>\n"
                + "</html>";
        // Tạo email
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject(MimeUtility.encodeText(subject, "utf-8", "B")); // Encode tiêu đề
        msg.setContent(messageContent, "text/html; charset=utf-8");

        // Gửi email
        Transport.send(msg);
    }
}
