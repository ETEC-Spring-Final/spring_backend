package com.example.spring_boot_project_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
