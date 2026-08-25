package com.example.spring_boot_project_api.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.reservation.ReservationRequestDTO;
import com.example.spring_boot_project_api.dto.response.reservation.ReservationResponseDTO;
import com.example.spring_boot_project_api.enums.ReservationStatusEnum;
import com.example.spring_boot_project_api.enums.RoleEnum;
import com.example.spring_boot_project_api.model.Location;
import com.example.spring_boot_project_api.model.Reservation;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.model.Vehicle;
import com.example.spring_boot_project_api.repository.LocationRepository;
import com.example.spring_boot_project_api.repository.ReservationRepository;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.repository.VehicleRepository;
import com.example.spring_boot_project_api.service.ReservationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
  private final ReservationRepository reservationRepository;
  private final VehicleRepository vehicleRepository;
  private final UserRepository userRepository;
  private final LocationRepository locationRepository;

  @Override
  public ReservationResponseDTO createReservation(ReservationRequestDTO dto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();

    User currentUser = userRepository.findByEmail(currentUsername)
        .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

    Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
        .orElseThrow(() -> new RuntimeException("Vehicle not found"));

    Location pickUpLocation = locationRepository.findById(dto.getPickUpLocationId())
        .orElseThrow(() -> new RuntimeException("Pick-up location not found"));

    Location returnLocation = locationRepository.findById(dto.getReturnLocationId())
        .orElseThrow(() -> new RuntimeException("Return location not found"));

    long days = Duration.between(dto.getPickUpDateTime(), dto.getReturnDateTime()).toDays();

    if (days <= 0) {
      throw new RuntimeException("Return date must be after pick-up date");
    }

    BigDecimal totalPrice = vehicle.getPricePerDay().multiply(BigDecimal.valueOf(days));

    Reservation reservation = new Reservation();
    reservation.setUser(currentUser);
    reservation.setVehicle(vehicle);
    reservation.setPickUpLocation(pickUpLocation);
    reservation.setReturnLocation(returnLocation);
    reservation.setPickUpDateTime(dto.getPickUpDateTime());
    reservation.setReturnDateTime(dto.getReturnDateTime());
    reservation.setTotalPrice(totalPrice);
    reservation.setDepositAmount(dto.getDepositAmount() != null ? dto.getDepositAmount() : BigDecimal.ZERO);
    reservation.setDiscountAmount(dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO);
    reservation.setAdditionalCharges(dto.getAdditionalCharges() != null ? dto.getAdditionalCharges() : BigDecimal.ZERO);
    reservation.setNotes(dto.getNotes());

    Reservation saved = reservationRepository.save(reservation);
    return toResponse(saved);
  }

  @Override
  public ReservationResponseDTO getReservationById(Long id) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Reservation not found"));

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();
    User currentUser = userRepository.findByEmail(currentUsername)
        .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

    boolean isOwner = reservation.getUser().getId().equals(currentUser.getId());
    boolean isStaff = currentUser.getRole() != RoleEnum.CUSTOMER;

    if (!isOwner && !isStaff) {
      throw new RuntimeException("You are not authorized to view this reservation");
    }

    return toResponse(reservation);
  }

  @Override
  public List<ReservationResponseDTO> getAllReservations() {
    return reservationRepository.findAll().stream().map(this::toResponse).toList();
  }

  // Customer get their reservations
  @Override
  public List<ReservationResponseDTO> getMyReservations() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();
    User currentUser = userRepository.findByEmail(currentUsername)
        .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

    return reservationRepository.findByUserId(currentUser.getId()).stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public ReservationResponseDTO changeReservationStatus(Long id, ReservationStatusEnum status) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Reservation not found"));

    reservation.setStatus(status);
    Reservation saved = reservationRepository.save(reservation);
    return toResponse(saved);
  }

  @Override
  public ReservationResponseDTO updateReservation(Long id, ReservationRequestDTO dto) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Reservation not found"));

    Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
        .orElseThrow(() -> new RuntimeException("Vehicle not found"));

    Location pickUpLocation = locationRepository.findById(dto.getPickUpLocationId())
        .orElseThrow(() -> new RuntimeException("Pick-up location not found"));

    Location returnLocation = locationRepository.findById(dto.getReturnLocationId())
        .orElseThrow(() -> new RuntimeException("Return location not found"));

    long days = Duration.between(dto.getPickUpDateTime(), dto.getReturnDateTime()).toDays();

    if (days <= 0) {
      throw new RuntimeException("Return date must be after pick-up date");
    }

    BigDecimal totalPrice = vehicle.getPricePerDay().multiply(BigDecimal.valueOf(days));

    reservation.setVehicle(vehicle);
    reservation.setPickUpLocation(pickUpLocation);
    reservation.setReturnLocation(returnLocation);
    reservation.setPickUpDateTime(dto.getPickUpDateTime());
    reservation.setReturnDateTime(dto.getReturnDateTime());
    reservation.setTotalPrice(totalPrice);
    reservation
        .setDepositAmount(dto.getDepositAmount() != null ? dto.getDepositAmount() : reservation.getDepositAmount());
    reservation
        .setDiscountAmount(dto.getDiscountAmount() != null ? dto.getDiscountAmount() : reservation.getDiscountAmount());
    reservation.setAdditionalCharges(
        dto.getAdditionalCharges() != null ? dto.getAdditionalCharges() : reservation.getAdditionalCharges());
    reservation.setNotes(dto.getNotes());

    Reservation saved = reservationRepository.save(reservation);
    return toResponse(saved);
  }

  @Override
  public void deleteReservation(Long id) {
    if (!reservationRepository.existsById(id)) {
      throw new RuntimeException("Reservation not found");
    }

    reservationRepository.deleteById(id);
  }

  private ReservationResponseDTO toResponse(Reservation r) {
    return new ReservationResponseDTO(
        r.getId(), r.getUser().getId(), r.getVehicle().getId(), r.getPickUpLocation().getId(),
        r.getReturnLocation().getId(), r.getPickUpDateTime(), r.getReturnDateTime(), r.getStatus(), r.getTotalPrice(),
        r.getDepositAmount(),
        r.getDiscountAmount(), r.getAdditionalCharges(), r.getNotes(), r.getCreatedAt(), r.getUpdatedAt());
  }
}
