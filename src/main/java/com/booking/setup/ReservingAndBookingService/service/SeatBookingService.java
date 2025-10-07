package com.booking.setup.ReservingAndBookingService.service;

import com.booking.setup.ReservingAndBookingService.entities.Seat;
import com.booking.setup.ReservingAndBookingService.repository.SeatRepository;
import com.booking.setup.ReservingAndBookingService.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class SeatBookingService {

    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    public SeatBookingService(SeatRepository seatRepository, UserRepository userRepository) {
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
    }

    /**
     * Lock-free seat booking:
     * 1. Select the first free seat (no locks)
     * 2. Update it to assign to the user (no WHERE user_id IS NULL)
     */

    @Transactional
    public Seat bookNextSeatForTrip(long tripId, long userId) {

        //Fetch free seat
        Seat seat = seatRepository.findNextFreeSeat(tripId)
                .orElseThrow(() -> new IllegalStateException("No Free seat available"));

        //Assign it to the user (unsafe, no guard)
        int updatedRows = seatRepository.assignSeatToUser(seat.getId(), userId);

        //For demo: fetch the seat again to show who got it finally
        return seatRepository.findById(seat.getId())
                .orElseThrow(() -> new IllegalStateException("Seat not found after update"));

    }
}
