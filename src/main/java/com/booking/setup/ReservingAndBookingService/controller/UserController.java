package com.booking.setup.ReservingAndBookingService.controller;

import com.booking.setup.ReservingAndBookingService.entities.Seat;
import com.booking.setup.ReservingAndBookingService.entities.Trip;
import com.booking.setup.ReservingAndBookingService.entities.User;
import com.booking.setup.ReservingAndBookingService.repository.SeatRepository;
import com.booking.setup.ReservingAndBookingService.repository.TripRepository;
import com.booking.setup.ReservingAndBookingService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/create-users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TripRepository tripRepository;


    // POST /users - Create multiple users at once
    @PostMapping("/addUser")
    public ResponseEntity<List<User>> createUsers(@RequestBody List<User> users) {
        List<User> saved = userRepository.saveAll(users);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/fill/{tripId}")
    public ResponseEntity<List<Seat>> fillSeats(@PathVariable Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));

        List<Seat> seats = new ArrayList<>();
        String[] letters = {"A", "B", "C", "D", "E"};
        int seatNumber = 1;

        // 20 rows * 5 seats = 100 seats
        for (int row = 1; row <= 20; row++) {
            for (String letter : letters) {
                Seat seat = new Seat();
                seat.setName(row + "-" + letter);
                seat.setTrip(trip);
                seat.setUser(null);  // not booked yet
                seats.add(seat);
                seatNumber++;
                if (seatNumber > 100) break;
            }
            if (seatNumber > 100) break;
        }

        List<Seat> saved = seatRepository.saveAll(seats);
        return ResponseEntity.ok(saved);
    }
}
