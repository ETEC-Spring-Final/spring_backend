package com.example.spring_boot_project_api.service;

import java.util.List;

import com.example.spring_boot_project_api.dto.request.invoice.InvoiceRequestDTO;
import com.example.spring_boot_project_api.dto.response.invoice.InvoiceResponseDTO;

public interface InvoiceService {
  InvoiceResponseDTO createInvoice(InvoiceRequestDTO dto);

  InvoiceResponseDTO getInvoiceById(Long id);

  List<InvoiceResponseDTO> getMyInvoices();

  List<InvoiceResponseDTO> getAllInvoices();

  InvoiceResponseDTO updateInvoice(Long id, InvoiceRequestDTO dto);

  void deleteInvoice(Long id);
}
