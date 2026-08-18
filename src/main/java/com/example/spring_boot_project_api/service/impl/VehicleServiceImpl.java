package com.example.spring_boot_project_api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.spring_boot_project_api.dto.request.vehicle.VehicleRequestDTO;
import com.example.spring_boot_project_api.dto.response.vehicle.VehicleResponseDTO;
import com.example.spring_boot_project_api.model.Vehicle;
import com.example.spring_boot_project_api.repository.VehicleRepository;
import com.example.spring_boot_project_api.service.VehicleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
  private final VehicleRepository vehicleRepository;

  @Override
  public VehicleResponseDTO createVehicle(VehicleRequestDTO dto) {
    if (vehicleRepository.findByLicensePlate(dto.getLicensePlate()).isPresent()) {
      throw new RuntimeException("Vehicle with that license plate already exists");
    }

    Vehicle vehicle = new Vehicle();
    vehicle.setBrand(dto.getBrand());
    vehicle.setModel(dto.getModel());
    vehicle.setYearOfManufacture(dto.getYearOfManufacture());
    vehicle.setLicensePlate(dto.getLicensePlate());
    vehicle.setColor(dto.getColor());
    vehicle.setType(dto.getType());
    vehicle.setTransmission(dto.getTransmission());
    vehicle.setFuelType(dto.getFuelType());
    vehicle.setSeats(dto.getSeats());
    vehicle.setPricePerDay(dto.getPricePerDay());
    vehicle.setMileAge(dto.getMileAge());
    vehicle.setDescription(dto.getDescription());
    vehicle.setStatus(dto.getStatus());

    Vehicle saved = vehicleRepository.save(vehicle);
    return toResponse(saved);
  }

  @Override
  public VehicleResponseDTO getVehicleById(Long id) {
    Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehicle not found"));

    return toResponse(vehicle);
  }

  @Override
  public List<VehicleResponseDTO> getAllVehicles() {
    return vehicleRepository.findAll().stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public VehicleResponseDTO updateVehicle(Long id, VehicleRequestDTO dto) {
    Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehicle not found"));

    vehicle.setBrand(dto.getBrand());
    vehicle.setModel(dto.getModel());
    vehicle.setYearOfManufacture(dto.getYearOfManufacture());
    vehicle.setLicensePlate(dto.getLicensePlate());
    vehicle.setColor(dto.getColor());
    vehicle.setType(dto.getType());
    vehicle.setTransmission(dto.getTransmission());
    vehicle.setFuelType(dto.getFuelType());
    vehicle.setSeats(dto.getSeats());
    vehicle.setPricePerDay(dto.getPricePerDay());
    vehicle.setMileAge(dto.getMileAge());
    vehicle.setDescription(dto.getDescription());
    vehicle.setStatus(dto.getStatus());

    Vehicle saved = vehicleRepository.save(vehicle);
    return toResponse(saved);
  }

  @Override
  public void deleteVehicle(Long id) {
    if (!vehicleRepository.existsById(id)) {
      throw new RuntimeException("Vehicle not found");
    }
    vehicleRepository.deleteById(id);
  }

  private VehicleResponseDTO toResponse(Vehicle v) {
    return new VehicleResponseDTO(v.getId(), v.getBrand(), v.getModel(), v.getYearOfManufacture(), v.getLicensePlate(),
        v.getColor(), v.getType(), v.getTransmission(), v.getFuelType(), v.getSeats(), v.getPricePerDay(),
        v.getMileAge(), v.getDescription(), v.getStatus(), v.getCreatedAt(), v.getUpdatedAt());
  }
}
