package com.example.spring_boot_project_api.dto.request.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.InvoiceStatusEnum;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceRequestDTO {

  @NotNull(message = "Rental ID is required")
  private Long rentalId;

  private LocalDateTime dueDate;

  @NotNull(message = "Subtotal is required")
  @DecimalMin(value = "0.0", inclusive = true, message = "Subtotal cannot be negative")
  private BigDecimal subtotal;

  @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount cannot be negative")
  private BigDecimal discountAmount;

  @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount cannot be negative")
  private BigDecimal taxAmount;

  @DecimalMin(value = "0.0", inclusive = true, message = "Late fee cannot be negative")
  private BigDecimal lateFee;

  private InvoiceStatusEnum status;
}