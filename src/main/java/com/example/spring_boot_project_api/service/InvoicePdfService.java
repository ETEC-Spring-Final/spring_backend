package com.example.spring_boot_project_api.service;

import com.example.spring_boot_project_api.model.Invoice;

/**
 * Renders a printable PDF for an invoice. The PDF contains the invoice
 * number, customer info, vehicle info, rental dates, price breakdown (base +
 * additional services + discount + tax + late fee), total, payment method and
 * status. The caller is responsible for authorization (owner or staff).
 */
public interface InvoicePdfService {

  byte[] generate(Invoice invoice);
}
