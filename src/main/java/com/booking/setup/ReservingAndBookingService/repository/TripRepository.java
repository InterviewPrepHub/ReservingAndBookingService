package com.booking.setup.ReservingAndBookingService.repository;

import com.booking.setup.ReservingAndBookingService.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
