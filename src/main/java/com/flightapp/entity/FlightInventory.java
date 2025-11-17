package com.flightapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "flight_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String airlineName;
    private String airlineLogoUrl;
    private String fromPlace;
    private String toPlace;
    
    private String flightNumber;
    
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    
    private Double price;
    private Integer totalSeats;
    private Integer availableSeats;
    private boolean active = true;
}
