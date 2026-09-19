package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.invoice.InvoicePaymentConfirmDTO;
import com.example.spring_boot_project_api.dto.request.invoice.InvoicePaymentMethodDTO;
import com.example.spring_boot_project_api.dto.request.invoice.InvoiceRequestDTO;
import com.example.spring_boot_project_api.dto.response.invoice.InvoiceResponseDTO;

public interface InvoiceService {
  InvoiceResponseDTO createInvoice(InvoiceRequestDTO dto);

  InvoiceResponseDTO getInvoiceById(Long id);

  List<InvoiceResponseDTO> getMyInvoices();

  List<InvoiceResponseDTO> getAllInvoices();

  InvoiceResponseDTO updateInvoice(Long id, InvoiceRequestDTO dto);

  void deleteInvoice(Long id);

  /**
   * Marks an invoice as PAID once the Bakong transaction (matched by the QR's
   * md5) comes back successful. Only the invoice owner may do this.
   */
  InvoiceResponseDTO confirmPayment(Long id, InvoicePaymentConfirmDTO dto);

  /**
   * Records the customer's chosen payment method on an unpaid invoice.
   * Choosing CASH keeps the invoice UNPAID (staff mark it PAID later) while
   * choosing KHQR keeps the existing Bakong QR payment flow. Owner only.
   */
  InvoiceResponseDTO setPaymentMethod(Long id, InvoicePaymentMethodDTO dto);

  /**
   * Staff/admin: mark an unpaid CASH invoice as PAID (sets paidAt). This is
   * the only way a CASH payment moves to PAID.
   */
  InvoiceResponseDTO markPaid(Long id);

  /**
   * Downloads a printable PDF for an invoice (owner or staff). Content:
   * invoice number, customer info, vehicle info, rental dates, price
   * breakdown (base + additional services + discount), total, payment
   * method and status.
   */
  byte[] downloadInvoicePdf(Long id);
}
