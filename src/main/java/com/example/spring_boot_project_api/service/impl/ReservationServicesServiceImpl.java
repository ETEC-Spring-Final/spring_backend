package com.example.spring_boot_project_api.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.reservation_service.ReservationServicesRequestDTO;
import com.example.spring_boot_project_api.dto.response.reservation_service.ReservationServicesResponseDTO;
import com.example.spring_boot_project_api.enums.RoleEnum;
import com.example.spring_boot_project_api.model.Reservation;
import com.example.spring_boot_project_api.model.ReservationServices;
import com.example.spring_boot_project_api.model.Services;
import com.example.spring_boot_project_api.model.User;
import com.example.spring_boot_project_api.repository.ReservationRepository;
import com.example.spring_boot_project_api.repository.ReservationServicesRepository;
import com.example.spring_boot_project_api.repository.ServiceRepository;
import com.example.spring_boot_project_api.repository.UserRepository;
import com.example.spring_boot_project_api.service.ReservationServicesService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationServicesServiceImpl implements ReservationServicesService {
  private final ReservationServicesRepository reservationServicesRepository;
  private final ReservationRepository reservationRepository;
  private final ServiceRepository serviceRepository;
  private final UserRepository userRepository;

  @Override
  public ReservationServicesResponseDTO createReservationService(ReservationServicesRequestDTO dto) {
    Reservation reservation = reservationRepository.findById(dto.getReservationId())
        .orElseThrow(() -> new RuntimeException("Reservation not found"));
    Services services = serviceRepository.findById(dto.getServiceId())
        .orElseThrow(() -> new RuntimeException("Service not found"));

    User currentUser = getCurrentUser();

    if (!reservation.getUser().getId().equals(currentUser.getId())) {
      throw new RuntimeException("This reservation does not belong to you");
    }

    if (!services.getIsActive().equals(true)) {
      throw new RuntimeException("This service is currently unavailable");
    }

    BigDecimal priceAtBooking = services.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));

    ReservationServices reservationServices = new ReservationServices();
    reservationServices.setReservation(reservation);
    reservationServices.setService(services);
    reservationServices.setQuantity(dto.getQuantity());
    reservationServices.setPriceAtBooking(priceAtBooking);

    ReservationServices saved = reservationServicesRepository.save(reservationServices);
    return toResponse(saved);
  }

  @Override
  public ReservationServicesResponseDTO getReservationServiceById(Long id) {
    ReservationServices reservationServices = reservationServicesRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Reservation service not found"));

    User currentUser = getCurrentUser();
    boolean isOwner = reservationServices.getReservation().getUser().getId().equals(currentUser.getId());
    boolean isStaff = currentUser.getRole() != RoleEnum.CUSTOMER;

    if (!isOwner && !isStaff) {
      throw new RuntimeException("You are not authorized to view this reservation service");
    }

    return toResponse(reservationServices);
  }

  @Override
  public List<ReservationServicesResponseDTO> getAllReservationServices() {
    return reservationServicesRepository.findAll().stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public List<ReservationServicesResponseDTO> getMyReservationServices() {
    User currentUser = getCurrentUser();
    return reservationServicesRepository.findByReservationUserId(currentUser.getId()).stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public ReservationServicesResponseDTO updateReservationService(Long id, ReservationServicesRequestDTO dto) {
    ReservationServices reservationServices = reservationServicesRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Reservation service not found"));

    User currentUser = getCurrentUser();

    if (!reservationServices.getReservation().getUser().getId().equals(currentUser.getId())) {
      throw new RuntimeException("This reservation service does not belong to you");
    }

    Services service = serviceRepository.findById(dto.getServiceId())
        .orElseThrow(() -> new RuntimeException("Service not found"));

    if (!service.getIsActive().equals(true)) {
      throw new RuntimeException("This service is currently unavailable");
    }

    BigDecimal priceAtBooking = service.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));

    reservationServices.setService(service);
    reservationServices.setQuantity(dto.getQuantity());
    reservationServices.setPriceAtBooking(priceAtBooking);

    ReservationServices saved = reservationServicesRepository.save(reservationServices);
    return toResponse(saved);
  }

  @Override
  public void deleteReservationService(Long id) {
    if (!reservationServicesRepository.existsById(id)) {
      throw new RuntimeException("Reservation service not found");
    }
    reservationServicesRepository.deleteById(id);
  }

  private ReservationServicesResponseDTO toResponse(ReservationServices rs) {
    return new ReservationServicesResponseDTO(rs.getId(), rs.getReservation().getId(), rs.getService().getId(),
        rs.getQuantity(), rs.getPriceAtBooking());
  }

  // Ownership function
  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUsername = authentication.getName();
    return userRepository.findByEmail(currentUsername)
        .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
  }
}
