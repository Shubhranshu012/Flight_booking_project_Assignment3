package com.flightapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Data
public class BookingRequestDto {

    @Email(message = "Provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "At least 1 seat must be booked")
    private Integer numberOfSeats;

    @NotNull(message = "Passengers list cannot be null")
    @Min(value = 1, message = "At least 1 passenger is required")
    @Valid   
    private List<PassengerDto> passengers;
    
    @NotBlank(message = "Meal option is required")
    @Pattern(regexp = "(?i)^(VEG|NON_VEG|MIX)$",message = "Meal option must be VEG, NON_VEG, or MIX")
    private String mealOption;

    @NotNull(message = "Seat numbers are required")
    @Min(value = 1, message = "At least 1 seat number required")
    private List<String> seatNumbers;
}
