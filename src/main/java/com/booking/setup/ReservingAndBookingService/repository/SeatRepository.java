package com.booking.setup.ReservingAndBookingService.repository;

import com.booking.setup.ReservingAndBookingService.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    // Find the first available seat (no locking)
    @Query(value = """
        SELECT * FROM seats
        WHERE trip_id = :tripId
          AND user_id IS NULL
        ORDER BY name
        LIMIT 1
        """, nativeQuery = true)
    Optional<Seat> findNextFreeSeat(@Param("tripId") Long tripId);

    // Update a seat by setting the user_id (no conditional guard — unsafe intentionally)
    @Modifying
    @Query(value = """
        UPDATE seats
        SET user_id = :userId
        WHERE id = :seatId
        """, nativeQuery = true)
    int assignSeatToUser(@Param("seatId") Long seatId, @Param("userId") Long userId);

}
