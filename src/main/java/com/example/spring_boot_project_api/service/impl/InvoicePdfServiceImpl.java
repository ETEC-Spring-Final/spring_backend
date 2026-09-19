package com.example.spring_boot_project_api.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.model.Brand;
import com.example.spring_boot_project_api.model.Invoice;
import com.example.spring_boot_project_api.model.Rental;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.model.Vehicle;
import com.example.spring_boot_project_api.service.InvoicePdfService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class InvoicePdfServiceImpl implements InvoicePdfService {

  private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

  private final String companyName;
  private final String address;
  private final String phone;

  public InvoicePdfServiceImpl(
      @Value("${app.invoice-pdf.company-name:Spring Boot Car Rental}") String companyName,
      @Value("${app.invoice-pdf.address:Phnom Penh, Cambodia}") String address,
      @Value("${app.invoice-pdf.phone:+855 23 000 000}") String phone) {
    this.companyName = companyName;
    this.address = address;
    this.phone = phone;
  }

  @Override
  public byte[] generate(Invoice invoice) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      Document document = new Document(PageSize.A4, 40, 40, 40, 40);
      PdfWriter.getInstance(document, baos);
      document.open();

      Rental rental = invoice.getRental();
      User customer = rental.getUser();
      Vehicle vehicle = rental.getVehicle();
      Brand brand = vehicle.getBrand();

      // --- Header ---
      title(document, companyName);
      Paragraph addr = new Paragraph(address + "  |  " + phone,
          new Font(Font.HELVETICA, 9, Font.NORMAL));
      addr.setAlignment(Element.ALIGN_CENTER);
      document.add(addr);
      blank(document);

      title(document, "INVOICE");
      blank(document);

      // --- Invoice meta ---
      footer(document, "Invoice " + invoice.getInvoiceNumber());
      footer(document, "Issued " + invoice.getIssueDate().format(DTF));
      if (invoice.getDueDate() != null) {
        footer(document, "Due " + invoice.getDueDate().format(DTF));
      }
      footer(document, "Status: " + invoice.getStatus()
          + (invoice.getPaymentMethod() != null ? "  |  Payment: " + invoice.getPaymentMethod() : ""));
      if (invoice.getPaidAt() != null) {
        footer(document, "Paid at: " + invoice.getPaidAt().format(DTF));
      }
      blank(document);

      // --- Vehicle & customer info ---
      footer(document, "Vehicle: " + brand.getName() + " " + vehicle.getModel()
          + " (" + vehicle.getLicensePlate() + ")");
      footer(document, "Pick-up: " + rental.getPickUpDateTime().format(DTF));
      footer(document, "Return:  " + rental.getExpectedReturnDateTime().format(DTF));
      if (customer.getFirstName() != null) {
        footer(document, "Customer: " + customer.getFirstName()
            + (customer.getLastName() != null ? " " + customer.getLastName() : ""));
      }
      footer(document, "Email: " + (customer.getEmail() != null ? customer.getEmail() : "-"));
      footer(document, "Phone: " + (customer.getPhone() != null ? customer.getPhone() : "-"));
      blank(document);

      // --- Price breakdown table ---
      amountTitle(document);
      PdfPTable table = new PdfPTable(2);
      table.setWidthPercentage(100);
      table.setWidths(new float[] { 3f, 2f });

      Font rowFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
      Font totalFont = new Font(Font.HELVETICA, 11, Font.BOLD);

      breakdown(table, "Subtotal", invoice.getSubtotal(), rowFont, false);
      if (invoice.getAdditionalServicesTotal() != null
          && invoice.getAdditionalServicesTotal().compareTo(BigDecimal.ZERO) > 0) {
        breakdown(table, "Additional Services", invoice.getAdditionalServicesTotal(), rowFont, false);
      }
      if (invoice.getDiscountAmount() != null
          && invoice.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
        breakdown(table, "Discount", invoice.getDiscountAmount().negate(), rowFont, false);
      }
      if (invoice.getTaxAmount() != null
          && invoice.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
        breakdown(table, "Tax", invoice.getTaxAmount(), rowFont, false);
      }
      if (invoice.getLateFee() != null
          && invoice.getLateFee().compareTo(BigDecimal.ZERO) > 0) {
        breakdown(table, "Late Fee", invoice.getLateFee(), rowFont, false);
      }
      breakdown(table, "TOTAL (USD)", invoice.getTotalAmount(), totalFont, true);

      document.add(table);
      blank(document);

      Paragraph thanks = new Paragraph("Thank you for choosing " + companyName + "!",
          new Font(Font.HELVETICA, 9, Font.ITALIC));
      thanks.setAlignment(Element.ALIGN_CENTER);
      document.add(thanks);

      document.close();
      return baos.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate invoice PDF", e);
    }
  }

  private void title(Document doc, String text) {
    Paragraph p = new Paragraph(text, new Font(Font.HELVETICA, 20, Font.BOLD));
    p.setAlignment(Element.ALIGN_CENTER);
    doc.add(p);
  }

  private void blank(Document doc) throws Exception {
    doc.add(new Paragraph(" ", new Font(Font.HELVETICA, 6)));
  }

  private void footer(Document doc, String text) throws Exception {
    doc.add(new Paragraph(text, new Font(Font.HELVETICA, 9)));
  }

  private void label(Document doc, String text) throws Exception {
    doc.add(new Paragraph(text, new Font(Font.HELVETICA, 11, Font.BOLD)));
  }

  private void amountTitle(Document doc) throws Exception {
    label(doc, "PRICE BREAKDOWN");
    blank(doc);
  }

  private String money(BigDecimal v) {
    if (v == null) {
      return "0.00";
    }
    return v.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }

  private void breakdown(PdfPTable table, String label, BigDecimal value, Font font, boolean total) {
    PdfPCell left = new PdfPCell(new Phrase(label, font));
    left.setBorder(total ? Rectangle.TOP : Rectangle.NO_BORDER);
    left.setPadding(4);
    table.addCell(left);

    PdfPCell right = new PdfPCell(new Phrase(money(value), font));
    right.setBorder(total ? Rectangle.TOP : Rectangle.NO_BORDER);
    right.setPadding(4);
    right.setHorizontalAlignment(Element.ALIGN_RIGHT);
    table.addCell(right);
  }
}