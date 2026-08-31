package com.example.spring_boot_project_api.service;

import jakarta.mail.MessagingException;

public interface EmailService {
  void sendPasswordResetEmail(String toEmail, String token) throws MessagingException;
}