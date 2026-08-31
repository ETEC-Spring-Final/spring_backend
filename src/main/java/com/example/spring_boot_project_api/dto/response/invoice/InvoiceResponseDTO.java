package com.example.spring_boot_project_api.dto.response.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.InvoiceStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponseDTO {

  private Long id;
  private Long rentalId;
  private String invoiceNumber;
  private LocalDateTime issueDate;
  private LocalDateTime dueDate;
  private BigDecimal subtotal;
  private BigDecimal discountAmount;
  private BigDecimal taxAmount;
  private BigDecimal lateFee;
  private BigDecimal totalAmount;
  private InvoiceStatusEnum status;
  private LocalDateTime createdAt;
}