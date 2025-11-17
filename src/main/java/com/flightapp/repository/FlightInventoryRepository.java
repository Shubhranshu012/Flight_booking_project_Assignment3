package com.flightapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.flightapp.entity.FlightInventory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightInventoryRepository extends JpaRepository<FlightInventory, Long> {

    List<FlightInventory> findByFromPlaceAndToPlaceAndDepartureTimeBetween(
            String fromPlace,
            String toPlace,
            LocalDateTime start,
            LocalDateTime end
    );
    
    @Query("SELECT f FROM FlightInventory f WHERE " +"f.airlineName = :airlineName AND " +"f.flightNumber = :flightNumber AND " +
    	    "f.fromPlace = :fromPlace AND " +"f.toPlace = :toPlace AND " +"f.departureTime = :departureTime")
    Optional<FlightInventory> findDuplicateFlight(String airlineName,String flightNumber,String fromPlace,String toPlace,LocalDateTime departureTime);
}