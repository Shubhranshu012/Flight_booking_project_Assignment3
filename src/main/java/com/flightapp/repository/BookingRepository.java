package com.flightapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.flightapp.entity.Booking;

import java.util.Optional;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPnr(String pnr);
    List<Booking> findByEmailOrderByBookingTimeDesc(String email);
    
    @Query("SELECT p.seatNumber FROM Booking b JOIN b.passengers p WHERE b.flightId = :flightId AND b.cancelled = false")
    List<String> getBookedSeats(Long flightId);
}