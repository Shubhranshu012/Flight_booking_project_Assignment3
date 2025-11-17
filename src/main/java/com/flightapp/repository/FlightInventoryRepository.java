package com.flightapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.flightapp.entity.FlightInventory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

public interface FlightInventoryRepository extends JpaRepository<FlightInventory, Long> {
	@Query("select fi from FlightInventory fi where fi.flight.fromPlace = :from and fi.flight.toPlace = :to and fi.departureTime between :start and :end")
	List<FlightInventory> findByFromPlaceAndToPlaceAndDepartureTimeBetween(@Param("from") String from, @Param("to") String to, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
	
	
	@Query("select fi from FlightInventory fi where fi.flight.airlineName = :airline and fi.flight.flightNumber = :flightNumber and fi.flight.fromPlace = :from and fi.flight.toPlace = :to and fi.departureTime = :departure")
	Optional<FlightInventory> findDuplicateFlight(@Param("airline") String airline, @Param("flightNumber") String flightNumber, @Param("from") String from, @Param("to") String to, @Param("departure") LocalDateTime departure);
}