package com.flightapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.flightapp.dto.BookingRequestDto;
import com.flightapp.entity.Booking;
import com.flightapp.service.BookingService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/api/v1.0/flight/booking/{flightId}")
    public ResponseEntity<Booking> book(@PathVariable Long flightId,@Valid  @RequestBody BookingRequestDto dto) {
    	
        return ResponseEntity.ok(bookingService.bookTicket(flightId, dto));
    }

    @GetMapping("/api/v1.0/flight/ticket/{pnr}")
    public ResponseEntity<Booking> getTicket(@PathVariable String pnr) {
    	
        return ResponseEntity.ok(bookingService.getByPnr(pnr));
    }

    @GetMapping("/api/v1.0/flight/booking/history/{email}")
    public ResponseEntity<List<Booking>> history(@PathVariable String email) {
    	
        return ResponseEntity.ok(bookingService.history(email));
    }

    @DeleteMapping("/api/v1.0/flight/booking/cancel/{pnr}")
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable String pnr) {
    	
        bookingService.cancelBooking(pnr);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Ticket cancelled successfully");
        return ResponseEntity.ok(response);
    }
}