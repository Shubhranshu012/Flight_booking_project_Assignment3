package com.flightapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.flightapp.dto.InventoryRequestDto;
import com.flightapp.dto.SearchRequestDto;
import com.flightapp.entity.Flight;
import com.flightapp.entity.FlightInventory;
import com.flightapp.exception.AvaliableSeatMoreThanTotal;
import com.flightapp.exception.ExceptionDuetoTiming;
import com.flightapp.exception.FlightAlreadyExist;
import com.flightapp.exception.FlightNotFoundException;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.repository.FlightRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlightInventoryService {

    private final FlightInventoryRepository inventoryRepo;
    
    private final FlightRepository flightRepo;
    
    public FlightInventory addInventory(InventoryRequestDto dto) {
    	
    	if (dto.getAvailableSeats() > dto.getTotalSeats()) {
            throw new AvaliableSeatMoreThanTotal("Available seats cannot be greater than total seats");
        }
    	if (dto.getArrivalTime().isBefore(dto.getDepartureTime())) {
    	    throw new ExceptionDuetoTiming("Arrival time cannot be before departure time");
    	}
    	
    	Optional<FlightInventory> duplicate = inventoryRepo.findDuplicateFlight(dto.getAirlineName(),dto.getFlightNumber(),dto.getFromPlace(),dto.getToPlace(),dto.getDepartureTime());

    	if (duplicate.isPresent()) {
    	        throw new FlightAlreadyExist("Flight already exists with same details (airline, flightNumber, route, departureTime)");
    	}
    	
    	Flight flight = flightRepo.findById(dto.getFlightNumber()).orElseGet(() -> {
    			Flight f = Flight.builder().flightNumber(dto.getFlightNumber()).airlineName(dto.getAirlineName()).fromPlace(dto.getFromPlace()).toPlace(dto.getToPlace()).build();
    			return flightRepo.save(f);
    	});


    	FlightInventory fi = FlightInventory.builder().flight(flight).departureTime(dto.getDepartureTime()).arrivalTime(dto.getArrivalTime()).price(dto.getPrice())
    			.totalSeats(dto.getTotalSeats()).availableSeats(dto.getAvailableSeats()).active(true).build();


    	return inventoryRepo.save(fi);
    }

    public Map<String, List<FlightInventory>> searchFlights(SearchRequestDto dto) {

        Map<String, List<FlightInventory>> response = new HashMap<>();

        LocalDateTime onwardStart = dto.getJourneyDate().atStartOfDay();
        LocalDateTime onwardEnd = dto.getJourneyDate().atTime(23, 59, 59);

        List<FlightInventory> onwardFlights =inventoryRepo.findByFromPlaceAndToPlaceAndDepartureTimeBetween(dto.getFromPlace(), dto.getToPlace(),onwardStart,onwardEnd );

        if (onwardFlights.isEmpty()) {
            throw new FlightNotFoundException("No onward flights found");
        }

        response.put("onwardFlights", onwardFlights);

        if (dto.getTripType().equalsIgnoreCase("ROUND_TRIP")) {

            if (dto.getReturnDate() == null) {
                throw new ExceptionDuetoTiming("Return date is required for ROUND_TRIP");
            }

            LocalDateTime returnStart = dto.getReturnDate().atStartOfDay();
            LocalDateTime returnEnd = dto.getReturnDate().atTime(23, 59, 59);

            List<FlightInventory> returnFlights =inventoryRepo.findByFromPlaceAndToPlaceAndDepartureTimeBetween(dto.getToPlace(),dto.getFromPlace(),returnStart,returnEnd);

            if (returnFlights.isEmpty()) {
                throw new FlightNotFoundException("No return flights found");
            }

            response.put("returnFlights", returnFlights);
        }

        return response;
    }
}