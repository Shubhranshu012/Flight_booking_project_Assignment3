package com.flightapp.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	private String pnr;
	private String email;
	private LocalDateTime bookingTime;
	
	
	private LocalDateTime departureTime;
	private LocalDateTime arrivalTime;
	private LocalDateTime journeyDateTime;
	
	
	private Double totalPrice;
	
	
	private boolean cancelled = false;
	private LocalDateTime cancelledAt;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "booking_id")
	private List<Passenger> passengers;
	
	
	@ManyToOne
	@JoinColumn(name = "flight_number")
	private Flight flight;
	
	private Long inventoryId;
}
