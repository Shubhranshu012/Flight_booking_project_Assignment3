package com.flightapp.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
@Data
public class InventoryRequestDto {

    @NotBlank(message = "Airline name is required")
    private String airlineName;

    private String airlineLogo;

    @NotBlank(message = "From place is required")
    private String fromPlace;

    @NotBlank(message = "To place is required")
    private String toPlace;

    @NotNull(message = "Departure time is required")
    @Future(message="Time Must be in Future")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    @Future(message="Time Must be in Future")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be positive")
    private Integer totalSeats;

    @NotNull(message = "Available seats is required")
    @Positive(message = "Available seats must be positive")
    private Integer availableSeats;
    
    @NotBlank(message="Flight number is required")
    private String flightNumber;
}