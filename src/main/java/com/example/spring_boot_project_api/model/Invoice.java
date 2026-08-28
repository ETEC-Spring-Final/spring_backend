package com.example.spring_boot_project_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.spring_boot_project_api.enums.InvoiceStatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rental_id", nullable = false)
  private Rental rental;

  @NotBlank
  @Column(name = "invoice_number", nullable = false, unique = true)
  private String invoiceNumber;

  @CreationTimestamp
  @Column(name = "issue_date", updatable = false)
  private LocalDateTime issueDate;

  @Column(name = "due_date")
  private LocalDateTime dueDate;

  @NotNull
  @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
  private BigDecimal subtotal;

  @Builder.Default
  @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal discountAmount = BigDecimal.ZERO;

  @Builder.Default
  @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal taxAmount = BigDecimal.ZERO;

  @Builder.Default
  @Column(name = "late_fee", nullable = false, precision = 10, scale = 2)
  private BigDecimal lateFee = BigDecimal.ZERO;

  @NotNull
  @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalAmount;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private InvoiceStatusEnum status = InvoiceStatusEnum.UNPAID;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}
