package com.flightapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.flightapp.dto.InventoryRequestDto;
import com.flightapp.dto.SearchRequestDto;
import com.flightapp.entity.FlightInventory;
import com.flightapp.exception.AvaliableSeatMoreThanTotal;
import com.flightapp.exception.ExceptionDuetoTiming;
import com.flightapp.exception.FlightAlreadyExist;
import com.flightapp.exception.FlightNotFoundException;
import com.flightapp.repository.FlightInventoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlightInventoryService {

    private final FlightInventoryRepository inventoryRepo;
    
    
    
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
        FlightInventory fi = FlightInventory.builder()
                .airlineName(dto.getAirlineName())
                .airlineLogoUrl(dto.getAirlineLogo())
                .fromPlace(dto.getFromPlace())
                .toPlace(dto.getToPlace())
                .departureTime(dto.getDepartureTime())
                .arrivalTime(dto.getArrivalTime())
                .price(dto.getPrice())
                .totalSeats(dto.getTotalSeats())
                .availableSeats(dto.getAvailableSeats())
                .flightNumber(dto.getFlightNumber()) 
                .active(true)
                .build();

        return inventoryRepo.save(fi);
    }

    public List<FlightInventory> searchFlights(SearchRequestDto dto) {

        LocalDateTime start = dto.getJourneyDate().atStartOfDay();
        LocalDateTime end = dto.getJourneyDate().atTime(23, 59, 59);

        List<FlightInventory> result =inventoryRepo.findByFromPlaceAndToPlaceAndDepartureTimeBetween(dto.getFromPlace(),dto.getToPlace(),start,end);

        if (result.isEmpty()) {
            throw new FlightNotFoundException("No flights found for given search criteria");
        }

        return result;
    }

}