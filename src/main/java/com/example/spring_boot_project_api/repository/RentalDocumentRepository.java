package com.example.spring_boot_project_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.spring_boot_project_api.model.RentalDocument;

public interface RentalDocumentRepository extends JpaRepository<RentalDocument, Long> {
  List<RentalDocument> findByRentalId(Long rentalId);

  List<RentalDocument> findByRentalIdIn(List<Long> rentalIds);
}
