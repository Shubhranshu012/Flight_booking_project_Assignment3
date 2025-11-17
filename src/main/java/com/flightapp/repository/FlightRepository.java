package com.flightapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.flightapp.entity.Flight;


public interface FlightRepository extends JpaRepository<Flight, String> {
}
