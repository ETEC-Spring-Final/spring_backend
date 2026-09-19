package com.example.spring_boot_project_api.dto.response.reservation;

import java.time.LocalDateTime;

/**
 * One already-booked window for a vehicle, returned by
 * GET /api/vehicles/{id}/booked-dates.
 *
 * @param startDate when the existing reservation starts
 * @param endDate   when the existing reservation ends
 */
public record BookedDateDTO(LocalDateTime startDate, LocalDateTime endDate) {
}