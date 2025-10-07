package com.booking.setup.ReservingAndBookingService.controller;

import com.booking.setup.ReservingAndBookingService.dto.BookingResult;
import com.booking.setup.ReservingAndBookingService.entities.Seat;
import com.booking.setup.ReservingAndBookingService.entities.User;
import com.booking.setup.ReservingAndBookingService.helper.BookingHelper;
import com.booking.setup.ReservingAndBookingService.repository.UserRepository;
import com.booking.setup.ReservingAndBookingService.service.SeatBookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private final UserRepository userRepository;
    private final SeatBookingService seatBookingService;
    private final BookingHelper bookingHelper;

    public BookingController(UserRepository userRepository, SeatBookingService seatBookingService, BookingHelper bookingHelper) {
        this.userRepository = userRepository;
        this.seatBookingService = seatBookingService;
        this.bookingHelper = bookingHelper;
    }

    @PostMapping("/book-all-concurrent")
    public ResponseEntity<?> bookAllConcurrent(@RequestParam Long tripId) throws InterruptedException {

        // Load all users (e.g., 100 users)
        List<User> users = userRepository.findAll();
        int n = users.size();

        // Create exactly one worker per user so everyone can start together
        ExecutorService pool = Executors.newFixedThreadPool(Math.max(1, n));

        // Gate to synchronize the start of all tasks
        CountDownLatch startGate = new CountDownLatch(1);

        // Submit one task per user and keep the futures
        List<Future<BookingResult>> futures = new ArrayList<>(n);
        for (User u : users) {
            futures.add(pool.submit(() -> {
                startGate.await(); // all tasks proceed together
                try {
                    Seat s = seatBookingService.bookNextSeatForTrip(tripId, u.getId());
                    return BookingResult.success(u.getId(), s.getId(), s.getName());
                } catch (Exception e) {
                    return BookingResult.failure(u.getId(), e.getMessage());
                }
            }));
        }

        // Release everyone at once
        startGate.countDown();

        // Stop accepting new tasks; let submitted tasks finish
        pool.shutdown();
        pool.awaitTermination(120, TimeUnit.SECONDS);

        // Collect results
        List<BookingResult> results = new ArrayList<>(n);
        for (Future<BookingResult> f : futures) {
            try {
                results.add(f.get());
            } catch (ExecutionException e) {
                results.add(BookingResult.failure(null, "Executor error: " + e.getCause()));
            }
        }

        Map<String, Object> summary = bookingHelper.summarize(results);
        return ResponseEntity.ok(Map.of("summary", summary, "results", results));

    }


}
