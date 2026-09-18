package com.example.spring_boot_project_api.dto.request.invoice;

import jakarta.validation.constraints.NotBlank;

public record InvoicePaymentConfirmDTO(
    @NotBlank String md5) {
}