package com.booking.setup.ReservingAndBookingService.helper;

import com.booking.setup.ReservingAndBookingService.dto.BookingResult;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BookingHelper {

    /**
     * Builds a detailed summary of the booking results for teaching/demo.
     * Helps visualize contention, duplicates, and success/failure distribution.
     */
    public Map<String, Object> summarize(List<BookingResult> results) {
        long totalAttempts = results.size();
        long successCount = results.stream().filter(BookingResult::isSuccess).count();
        long failureCount = totalAttempts - successCount;

        // 🧭 Map of seatName -> list of userIds who successfully booked it
        Map<String, List<Long>> seatToUsers = new TreeMap<>();

        for (BookingResult result : results) {
            if (result.isSuccess() && result.getSeatName() != null) {
                seatToUsers
                        .computeIfAbsent(result.getSeatName(), k -> new ArrayList<>())
                        .add(result.getUserId());
            }
        }

        // 📊 Calculate how many times each seat was "written" (duplicates show contention)
        Map<String, Integer> seatWriteCounts = new TreeMap<>();
        for (Map.Entry<String, List<Long>> entry : seatToUsers.entrySet()) {
            seatWriteCounts.put(entry.getKey(), entry.getValue().size());
        }

        // 🧩 Prepare human-readable examples (first 10 seats only for brevity)
        List<Map<String, Object>> seatExamples = new ArrayList<>();
        seatToUsers.entrySet().stream().limit(10).forEach(entry -> {
            Map<String, Object> seatInfo = new LinkedHashMap<>();
            seatInfo.put("seatName", entry.getKey());
            seatInfo.put("bookedByUsers", entry.getValue());
            seatInfo.put("writeCount", entry.getValue().size());
            seatExamples.add(seatInfo);
        });

        // 🧾 Build the final summary map
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalAttempts", totalAttempts);
        summary.put("successCount", successCount);
        summary.put("failureCount", failureCount);
        summary.put("distinctSeatsBooked", seatToUsers.keySet().size());
        summary.put("seatWriteCounts", seatWriteCounts);
        summary.put("sampleSeats", seatExamples);

        return summary;
    }
}
