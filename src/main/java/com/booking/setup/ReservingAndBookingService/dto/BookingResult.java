package com.booking.setup.ReservingAndBookingService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResult {

    private boolean success;
    private Long userId;
    private Long seatId;
    private String seatName;
    private String error;

    //Success factory method
    public static BookingResult success(Long userId, Long seatId, String seatName) {
        return new BookingResult(true, userId, seatId, seatName, null);
    }

    //Failure factory method
    public static BookingResult failure(Long userId, String error) {
        return new BookingResult(false, userId, null, null, error);
    }
}
