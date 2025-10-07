package com.booking.setup.ReservingAndBookingService.repository;

import com.booking.setup.ReservingAndBookingService.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
