package com.example.spring_boot_project_api.specification;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.example.spring_boot_project_api.enums.CarTypeEnum;
import com.example.spring_boot_project_api.enums.FuelTypeEnum;
import com.example.spring_boot_project_api.enums.TransmissionEnum;
import com.example.spring_boot_project_api.model.Vehicle;

// FIX: builds a single dynamic WHERE clause out of whichever filters were
// actually sent by the frontend — any null param is simply skipped.
public class VehicleSpecification {

  public static Specification<Vehicle> withFilters(
      Long brandId, CarTypeEnum type, TransmissionEnum transmission,
      FuelTypeEnum fuelType, BigDecimal minPrice, BigDecimal maxPrice, Integer seats) {

    return (root, query, cb) -> {
      var predicates = cb.conjunction();

      if (brandId != null) {
        predicates = cb.and(predicates, cb.equal(root.get("brand").get("id"), brandId));
      }
      if (type != null) {
        predicates = cb.and(predicates, cb.equal(root.get("type"), type));
      }
      if (transmission != null) {
        predicates = cb.and(predicates, cb.equal(root.get("transmission"), transmission));
      }
      if (fuelType != null) {
        predicates = cb.and(predicates, cb.equal(root.get("fuelType"), fuelType));
      }
      if (minPrice != null) {
        predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("pricePerDay"), minPrice));
      }
      if (maxPrice != null) {
        predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("pricePerDay"), maxPrice));
      }
      if (seats != null) {
        predicates = cb.and(predicates, cb.equal(root.get("seats"), seats));
      }
      return predicates;
    };
  }
}