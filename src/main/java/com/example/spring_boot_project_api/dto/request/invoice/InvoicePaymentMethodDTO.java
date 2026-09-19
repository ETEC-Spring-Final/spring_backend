package com.example.spring_boot_project_api.dto.request.invoice;

import com.example.spring_boot_project_api.enums.PaymentMethodEnum;

import jakarta.validation.constraints.NotNull;

/**
 * Body for PATCH /api/invoices/{id}/payment-method — lets the invoice owner
 * record how they intend to pay (e.g. CASH on pickup) without paying online.
 */
public record InvoicePaymentMethodDTO(@NotNull(message = "Payment method is required") PaymentMethodEnum paymentMethod) {
}