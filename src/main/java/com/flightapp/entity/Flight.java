package com.flightapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Table(name = "flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {
	@Id
	private String flightNumber; 
	
	private String airlineName;
	private String fromPlace;
	private String toPlace;
}