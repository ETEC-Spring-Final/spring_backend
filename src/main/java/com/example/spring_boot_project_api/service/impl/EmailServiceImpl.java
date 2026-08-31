package com.example.spring_boot_project_api.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
  private final JavaMailSender mailSender;

  @Override
  public void sendPasswordResetEmail(String toEmail, String token) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    String resetLink = "http://localhost:3000/reset-password?token=" + token;

    helper.setTo(toEmail);
    helper.setSubject("Password Reset Request");
    helper.setText(
        "<p>You requested a password reset.</p>" +
            "<a href='" + resetLink
            + "' style='padding:10px 20px;background:#4CAF50;color:white;text-decoration:none;'>Reset My Password</a>" +
            "<p>This link expires in 15 minutes.</p>",
        true // true = isHtml
    );

    mailSender.send(message);
  }
}