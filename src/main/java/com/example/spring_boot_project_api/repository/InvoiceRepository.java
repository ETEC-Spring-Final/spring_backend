package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
  boolean existsByInvoiceNumber(String invoiceNumber);

  boolean existsByRentalId(Long rentalId);

  List<Invoice> findByRentalUserEmail(String email);
}
