package com.example.spring_boot_project_api.enums;

public enum RentalStatusEnum {
  PENDING, // rental record created, car not yet picked up
  CONFIRMED, // unused in practice — reservation already confirms before rental exists
  PICKED_UP, // staff handed over the keys, car has left the lot
  ACTIVE, // customer currently has the car (may overlap with PICKED_UP)
  RETURNED, // customer brought the car back, staff inspected it
  COMPLETED // rental fully closed — payment/paperwork settled
}