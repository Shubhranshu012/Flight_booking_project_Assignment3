package com.flightapp.dto;

import lombok.Data;


import jakarta.validation.constraints.*;
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
	
	@NotBlank(message = "Flight number is required")
	private String flightNumber;
	
	@NotNull(message = "Departure time is required")
	@Future(message = "Departure time Should Be in Future")
	private LocalDateTime departureTime;
	
	
	@NotNull(message = "Arrival time is required")
	@Future(message = "Arrival time Should Be in Future")
	private LocalDateTime arrivalTime;
	
	@NotNull 
	@Positive
	private Double price;
	
	@NotNull 
	@Positive
	private Integer totalSeats;
	
	@NotNull 
	@Positive
	private Integer availableSeats;
}