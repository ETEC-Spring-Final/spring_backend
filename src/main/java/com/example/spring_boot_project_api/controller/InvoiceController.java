package com.example.spring_boot_project_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_project_api.dto.request.invoice.InvoicePaymentConfirmDTO;
import com.example.spring_boot_project_api.dto.request.invoice.InvoicePaymentMethodDTO;
import com.example.spring_boot_project_api.dto.request.invoice.InvoiceRequestDTO;
import com.example.spring_boot_project_api.dto.response.invoice.InvoiceResponseDTO;
import com.example.spring_boot_project_api.service.InvoiceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
  @Autowired
  private InvoiceService invoiceService;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PostMapping
  public InvoiceResponseDTO createInvoice(@Valid @RequestBody InvoiceRequestDTO dto) {
    return invoiceService.createInvoice(dto);
  }

  @GetMapping("/{id}")
  public InvoiceResponseDTO getInvoiceById(@PathVariable Long id) {
    return invoiceService.getInvoiceById(id);
  }

  @GetMapping("/my-invoices")
  public List<InvoiceResponseDTO> getMyInvoices() {
    return invoiceService.getMyInvoices();
  }

  @PostMapping("/{id}/confirm-payment")
  public InvoiceResponseDTO confirmPayment(@PathVariable Long id,
      @Valid @RequestBody InvoicePaymentConfirmDTO dto) {
    return invoiceService.confirmPayment(id, dto);
  }

  @PatchMapping("/{id}/payment-method")
  public InvoiceResponseDTO setPaymentMethod(@PathVariable Long id,
      @Valid @RequestBody InvoicePaymentMethodDTO dto) {
    return invoiceService.setPaymentMethod(id, dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PostMapping("/{id}/mark-paid")
  public InvoiceResponseDTO markPaid(@PathVariable Long id) {
    return invoiceService.markPaid(id);
  }

  @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {
    byte[] pdf = invoiceService.downloadInvoicePdf(id);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice-" + id + ".pdf\"")
        .contentType(MediaType.APPLICATION_PDF)
        .body(pdf);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @GetMapping
  public List<InvoiceResponseDTO> getAllInvoices() {
    return invoiceService.getAllInvoices();
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @PutMapping("/{id}")
  public InvoiceResponseDTO updateInvoice(@PathVariable Long id, @Valid @RequestBody InvoiceRequestDTO dto) {
    return invoiceService.updateInvoice(id, dto);
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
  @DeleteMapping("/{id}")
  public void deleteInvoice(@PathVariable Long id) {
    invoiceService.deleteInvoice(id);
  }
}
