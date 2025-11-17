package com.flightapp.entity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "passengers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String gender;
	private Integer age;
	private String seatNumber;
	private String mealOption;
}