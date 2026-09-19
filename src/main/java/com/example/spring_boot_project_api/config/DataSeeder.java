package com.example.spring_boot_project_api.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.spring_boot_project_api.model.AdditionalService;
import com.example.spring_boot_project_api.repository.AdditionalServiceRepository;

import lombok.RequiredArgsConstructor;

/**
 * Seeds the 4 default per-day additional services on first boot (only when
 * the table is empty, so it never overwrites admin edits). Prices per day:
 * Additional Driver $10, GPS Navigation $5, Child Seat $8, Full Insurance $15.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
  private final AdditionalServiceRepository additionalServiceRepository;

  @Override
  public void run(String... args) {
    if (additionalServiceRepository.count() > 0) {
      return;
    }

    seed("Additional Driver", "អ្នកបើកបរបន្ថែម", "Hire a professional driver for your trip.", 10.00, "driver");
    seed("GPS Navigation", "GPS រុករកផ្លូវ", "Built-in navigation for stress-free directions.", 5.00, "gps");
    seed("Child Seat", "កៅអីកុមារ", "Safe and comfy seat for little passengers.", 8.00, "child-seat");
    seed("Full Insurance", "ការធានារ៉ាប់រងពេញ", "Complete cover with zero excess.", 15.00, "insurance");
  }

  private void seed(String name, String nameKh, String description, double price, String icon) {
    AdditionalService service = new AdditionalService();
    service.setName(name);
    service.setNameKh(nameKh);
    service.setDescription(description);
    service.setPricePerDay(BigDecimal.valueOf(price));
    service.setIcon(icon);
    service.setActive(true);
    additionalServiceRepository.save(service);
  }
}