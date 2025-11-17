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
		
	@ManyToOne
	@JoinColumn(name = "flight_number")
	private Flight flight; 
	
	private LocalDateTime departureTime;
	private LocalDateTime arrivalTime;	
	private Double price;
	private Integer totalSeats;
	private Integer availableSeats;
	private boolean active = true;
}
