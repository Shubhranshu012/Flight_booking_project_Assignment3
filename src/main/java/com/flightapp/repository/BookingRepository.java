package com.flightapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.flightapp.entity.Booking;

import java.util.Optional;
import java.util.List;

import org.springframework.data.repository.query.Param;


public interface BookingRepository extends JpaRepository<Booking, Long> {
	Optional<Booking> findByPnr(String pnr);
	
	@Query("SELECT b FROM Booking b WHERE b.email = :email AND b.cancelled = false ORDER BY b.bookingTime DESC")
	List<Booking> findActiveBookingsByEmail(@Param("email") String email);
	
	
	@Query("select p.seatNumber from Booking b join b.passengers p where b.flight.id = :flightId and b.cancelled = false")
	List<String> getBookedSeats(@Param("flightId") Long flightId);
}