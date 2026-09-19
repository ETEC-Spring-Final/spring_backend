package com.example.spring_boot_project_api.enums;

/**
 * How an invoice is (or will be) paid.
 *
 * KHQR  - Bakong KHQR scan-to-pay (default for customer bookings).
 * CASH  - "Cash on pickup": stays UNPAID until staff/admin marks it PAID.
 * VISA / ABA_PAY - reserved for future card rails; not wired to any gateway yet.
 */
public enum PaymentMethodEnum {
  VISA,
  ABA_PAY,
  KHQR,
  CASH
}