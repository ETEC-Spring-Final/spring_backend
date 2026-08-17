package com.example.spring_boot_project_api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

  @GetMapping("/customer")
  @PreAuthorize("hasRole('CUSTOMER')")
  public String customerOnly() {
    return "Hello CUSTOMER — you're authorized";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminOnly() {
    return "Hello ADMIN — you're authorized";
  }

  @GetMapping("/manager")
  @PreAuthorize("hasRole('MANAGER')")
  public String managerOnly() {
    return "Hello MANAGER - you're authorized";
  }

  @GetMapping("/staff")
  @PreAuthorize("hasRole('STAFF')")
  public String staffOnly() {
    return "Hello STAFF - you're authorized";
  }

  @GetMapping("/any")
  public String anyAuthenticatedUser() {
    return "Hello — you're just logged in, no specific role needed";
  }
}