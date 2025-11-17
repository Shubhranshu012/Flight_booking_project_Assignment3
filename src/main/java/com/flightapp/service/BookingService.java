package com.flightapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.flightapp.dto.BookingRequestDto;
import com.flightapp.entity.Booking;
import com.flightapp.entity.FlightInventory;
import com.flightapp.entity.Passenger;
import com.flightapp.exception.ExceptionDueToSeat;
import com.flightapp.exception.ExceptionDuetoTiming;
import com.flightapp.exception.NotFoundException;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightInventoryRepository;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final FlightInventoryRepository inventoryRepo;
    
    private final BookingRepository bookingRepo;
    
    
    @Transactional
    public Booking bookTicket(Long flightId, BookingRequestDto dto) {

        FlightInventory flight = inventoryRepo.findById(flightId).orElseThrow(() -> new NotFoundException("Flight not found"));

        if (flight.getAvailableSeats() < dto.getNumberOfSeats()) {
            throw new ExceptionDueToSeat("Not enough seats available");
        }
        
        List<String> bookedSeats = bookingRepo.getBookedSeats(flightId);

        if (dto.getSeatNumbers().size() != dto.getPassengers().size()) {
            throw new ExceptionDueToSeat("Seat numbers count must match passenger count");
        }
        
        for (String seat : dto.getSeatNumbers()) {
            if (bookedSeats.contains(seat)) {
                throw new ExceptionDueToSeat("Seat " + seat + " is already booked");
            }
        }
        Set<String> uniqueSeats = new HashSet<>(dto.getSeatNumbers());
        if (uniqueSeats.size() != dto.getSeatNumbers().size()) {
            throw new ExceptionDueToSeat("Duplicate seat numbers in the request");
        }
        
        flight.setAvailableSeats(flight.getAvailableSeats() - dto.getNumberOfSeats());
        inventoryRepo.save(flight);

        
        String pnr = "PNR" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        
        List<Passenger> passengers = dto.getPassengers().stream()
        		.map(p -> Passenger.builder().name(p.getName()).gender(p.getGender())
        		.age(p.getAge()).seatNumber(p.getSeatNumber()).mealOption(p.getMealOption())
        		.build()).toList();


        Double totalPrice = dto.getNumberOfSeats() * flight.getPrice();


        Booking booking = Booking.builder().pnr(pnr).email(dto.getEmail()).bookingTime(LocalDateTime.now())
                .departureTime(flight.getDepartureTime()).arrivalTime(flight.getArrivalTime())
                .journeyDateTime(flight.getDepartureTime())
                .flight(flight.getFlight()).inventoryId(flight.getId()).totalPrice(totalPrice)
                .passengers(passengers).cancelled(false).build();

        return bookingRepo.save(booking);
    }

    public Booking getByPnr(String pnr) {
        Booking booking = bookingRepo.findByPnr(pnr).orElseThrow(() -> new NotFoundException("PNR not found"));

        if (booking.isCancelled()) {
            throw new NotFoundException("This ticket has been cancelled");
        }

        return booking;
    }

    public List<Booking> history(String email) {
        List<Booking> list = bookingRepo.findActiveBookingsByEmail(email);

        if (list.isEmpty()) {
            throw new NotFoundException("No booking history found for: " + email);
        }

        return list;
    }
    
    @Transactional
    public void cancelBooking(String pnr) {

        Booking booking = bookingRepo.findByPnr(pnr).orElseThrow(() -> new NotFoundException("PNR not found"));

        if (!booking.getJourneyDateTime().isAfter(LocalDateTime.now().plusHours(24))) {
            throw new ExceptionDuetoTiming("Cannot cancel within 24 hours of journey");
        }

        if (booking.isCancelled()) {
            throw new ExceptionDuetoTiming("Ticket already cancelled");
        }

        booking.setCancelled(true);
        booking.setCancelledAt(LocalDateTime.now());
        bookingRepo.save(booking);

        FlightInventory fi = inventoryRepo.findById(booking.getInventoryId()).orElseThrow(() -> new NotFoundException("Inventory not found"));

        fi.setAvailableSeats(fi.getAvailableSeats() + booking.getPassengers().size());
        inventoryRepo.save(fi);
    }
}