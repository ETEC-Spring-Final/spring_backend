package com.example.spring_boot_project_api.dto.response.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.spring_boot_project_api.enums.InvoiceStatusEnum;
import com.example.spring_boot_project_api.enums.PaymentMethodEnum;

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
  private String customerName;   // ថ្មី
  private String customerEmail;  // ថ្មី
  private String customerPhone;  // ថ្មី
  private String invoiceNumber;
  private LocalDateTime issueDate;
  private LocalDateTime dueDate;
  private BigDecimal subtotal;
  private BigDecimal additionalServicesTotal;
  private BigDecimal discountAmount;
  private BigDecimal taxAmount;
  private BigDecimal lateFee;
  private BigDecimal totalAmount;
  private InvoiceStatusEnum status;
  private PaymentMethodEnum paymentMethod;
  private LocalDateTime paidAt;
  private LocalDateTime createdAt;
}