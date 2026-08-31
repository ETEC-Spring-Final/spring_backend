package com.example.spring_boot_project_api.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.invoice.InvoiceRequestDTO;
import com.example.spring_boot_project_api.dto.response.invoice.InvoiceResponseDTO;
import com.example.spring_boot_project_api.enums.InvoiceStatusEnum;
import com.example.spring_boot_project_api.enums.RoleEnum;
import com.example.spring_boot_project_api.model.Invoice;
import com.example.spring_boot_project_api.model.Rental;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.InvoiceRepository;
import com.example.spring_boot_project_api.repository.RentalRepository;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.InvoiceService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
  private final InvoiceRepository invoiceRepository;
  private final UserRepository userRepository;
  private final RentalRepository rentalRepository;

  @Override
  public InvoiceResponseDTO createInvoice(InvoiceRequestDTO dto) {
    if (invoiceRepository.existsByRentalId(dto.getRentalId())) {
      throw new RuntimeException("An invoice already exists for this rental");
    }

    Rental rental = rentalRepository.findById(dto.getRentalId())
        .orElseThrow(() -> new RuntimeException("Rental not found"));

    BigDecimal subtotal = dto.getSubtotal();
    BigDecimal discount = dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO;
    BigDecimal tax = dto.getTaxAmount() != null ? dto.getTaxAmount() : BigDecimal.ZERO;
    BigDecimal lateFee = dto.getLateFee() != null ? dto.getLateFee() : BigDecimal.ZERO;

    BigDecimal totalAmount = subtotal.subtract(discount).add(tax).add(lateFee);

    Invoice invoice = new Invoice();
    invoice.setRental(rental);
    invoice.setInvoiceNumber(generateInvoiceNumber());
    invoice.setDueDate(dto.getDueDate());
    invoice.setSubtotal(subtotal);
    invoice.setDiscountAmount(discount);
    invoice.setTaxAmount(tax);
    invoice.setLateFee(lateFee);
    invoice.setTotalAmount(totalAmount);
    invoice.setStatus(dto.getStatus() != null ? dto.getStatus() : InvoiceStatusEnum.UNPAID);

    Invoice saved = invoiceRepository.save(invoice);
    return toResponse(saved);
  }

  @Override
  public InvoiceResponseDTO getInvoiceById(Long id) {
    Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));

    User currentUser = getCurrentUser();
    boolean isOwner = invoice.getRental().getUser().getId().equals(currentUser.getId());
    boolean isStaff = currentUser.getRole() != RoleEnum.CUSTOMER;

    if (!isOwner && !isStaff) {
      throw new RuntimeException("You are not authorized to view this invoice");
    }

    return toResponse(invoice);
  }

  @Override
  public List<InvoiceResponseDTO> getMyInvoices() {
    User currentUser = getCurrentUser();
    return invoiceRepository.findByRentalUserEmail(currentUser.getEmail()) // find thru Rental -> User to get who's
                                                                           // invoices it is
        .stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public List<InvoiceResponseDTO> getAllInvoices() {
    return invoiceRepository.findAll().stream()
        .map(this::toResponse).toList();
  }

  @Override
  public InvoiceResponseDTO updateInvoice(Long id, InvoiceRequestDTO dto) {
    Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));

    Rental rental = rentalRepository.findById(dto.getRentalId())
        .orElseThrow(() -> new RuntimeException("Rental not found"));

    BigDecimal subtotal = dto.getSubtotal();
    BigDecimal discount = dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO;
    BigDecimal tax = dto.getTaxAmount() != null ? dto.getTaxAmount() : BigDecimal.ZERO;
    BigDecimal lateFee = dto.getLateFee() != null ? dto.getLateFee() : BigDecimal.ZERO;

    BigDecimal totalAmount = subtotal.subtract(discount).add(tax).add(lateFee);

    invoice.setRental(rental);
    invoice.setDueDate(dto.getDueDate());
    invoice.setSubtotal(subtotal);
    invoice.setDiscountAmount(discount);
    invoice.setTaxAmount(tax);
    invoice.setLateFee(lateFee);
    invoice.setTotalAmount(totalAmount);
    invoice.setStatus(dto.getStatus() != null ? dto.getStatus() : InvoiceStatusEnum.UNPAID);

    Invoice saved = invoiceRepository.save(invoice);
    return toResponse(saved);
  }

  @Override
  public void deleteInvoice(Long id) {
    if (!invoiceRepository.existsById(id)) {
      throw new RuntimeException("Invoice not found");
    }

    invoiceRepository.deleteById(id);
  }

  private InvoiceResponseDTO toResponse(Invoice i) {
    return new InvoiceResponseDTO(i.getId(), i.getRental().getId(), i.getInvoiceNumber(), i.getIssueDate(),
        i.getDueDate(), i.getSubtotal(), i.getDiscountAmount(), i.getTaxAmount(), i.getLateFee(), i.getTotalAmount(),
        i.getStatus(), i.getCreatedAt());
  }

  // Generate Invoice Number
  private String generateInvoiceNumber() {
    String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String invoiceNum;
    do {
      long randomPart = (long) (Math.random() * 9000L) + 1000L;
      invoiceNum = "INV-" + datePart + "-" + randomPart;
    } while (invoiceRepository.existsByInvoiceNumber(invoiceNum));
    return invoiceNum;
  }

  // Ownership function
  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();
    return userRepository.findByEmail(currentUsername)
        .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
  }
}
