package com.flightapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightapp.dto.BookingRequestDto;
import com.flightapp.dto.PassengerDto;

import com.flightapp.entity.Flight;
import com.flightapp.entity.FlightInventory;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightInventoryRepository;
import com.flightapp.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlightRepository flightRepo;

    @Autowired
    private FlightInventoryRepository inventoryRepo;

    @Autowired
    private BookingRepository bookingRepo;

    private Long flightId;

    @BeforeEach
    void setup() {
        bookingRepo.deleteAll();
        inventoryRepo.deleteAll();
        flightRepo.deleteAll();

        Flight flight = flightRepo.save(
                Flight.builder().flightNumber("6E-512").airlineName("IndiGo").fromPlace("Delhi").toPlace("Mumbai").build()
        );

        FlightInventory inv = inventoryRepo.save(
                FlightInventory.builder()
                        .flight(flight).price(4500.0).totalSeats(180).availableSeats(180)
                        .departureTime(LocalDateTime.now().plusDays(2)).arrivalTime(LocalDateTime.now().plusDays(2).plusHours(2)).active(true).build()
        );

        flightId = inv.getId();
    }

    @Test
    void testBookTicket_success() throws Exception {

        BookingRequestDto dto = new BookingRequestDto();
        dto.setEmail("test@gmail.com");
        dto.setNumberOfSeats(1);

        PassengerDto p = new PassengerDto();
        p.setName("Rohit");
        p.setGender("M");
        p.setAge(28);
        p.setSeatNumber("12A");
        p.setMealOption("VEG");

        dto.setPassengers(List.of(p));
        dto.setSeatNumbers(List.of("12A"));
        dto.setMealOption("Mix");
        mockMvc.perform(post("/api/v1.0/flight/booking/" + flightId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.passengers[0].name").value("Rohit")).andExpect(jsonPath("$.flight.flightNumber").value("6E-512"));
    }
    @Test
    void testBookTicket_GenderNotPassed() throws Exception {

        BookingRequestDto dto = new BookingRequestDto();
        dto.setEmail("test@gmail.com");dto.setNumberOfSeats(1);

        PassengerDto p = new PassengerDto();
        p.setName("Rohit");
        p.setGender("");p.setAge(28);
        p.setSeatNumber("12A");p.setMealOption("VEG");

        dto.setPassengers(List.of(p));
        dto.setSeatNumbers(List.of("12A"));
        dto.setMealOption("Mix");
        mockMvc.perform(post("/api/v1.0/flight/booking/" + flightId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testBookTicket_SeatNotPassed() throws Exception {

        BookingRequestDto dto = new BookingRequestDto();
        dto.setEmail("test@gmail.com");dto.setNumberOfSeats(1);

        PassengerDto p = new PassengerDto();
        p.setName("Rohit");
        p.setGender("M");p.setAge(28);
        p.setSeatNumber("");p.setMealOption("VEG");

        dto.setPassengers(List.of(p));
        dto.setSeatNumbers(List.of("12A"));
        dto.setMealOption("Mix");
        mockMvc.perform(post("/api/v1.0/flight/booking/" + flightId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testBookTicket_AgeNegative() throws Exception {

        BookingRequestDto dto = new BookingRequestDto();
        dto.setEmail("test@gmail.com");dto.setNumberOfSeats(1);

        PassengerDto p = new PassengerDto();
        p.setName("Rohit");
        p.setGender("");p.setAge(-28);
        p.setSeatNumber("12A");p.setMealOption("VEG");

        dto.setPassengers(List.of(p));
        dto.setSeatNumbers(List.of("12A"));
        dto.setMealOption("Mix");
        mockMvc.perform(post("/api/v1.0/flight/booking/" + flightId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testBookTicket_NameNotPassed() throws Exception {

        BookingRequestDto dto = new BookingRequestDto();
        dto.setEmail("test@gmail.com");dto.setNumberOfSeats(1);

        PassengerDto p = new PassengerDto();
        p.setName("");
        p.setGender("M");p.setAge(28);
        p.setSeatNumber("12A");p.setMealOption("VEG");

        dto.setPassengers(List.of(p));
        dto.setSeatNumbers(List.of("12A"));
        dto.setMealOption("Mix");
        mockMvc.perform(post("/api/v1.0/flight/booking/" + flightId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testBookTicket_MealOptionNotPassed() throws Exception {

        BookingRequestDto dto = new BookingRequestDto();
        dto.setEmail("test@gmail.com");dto.setNumberOfSeats(1);

        PassengerDto p = new PassengerDto();
        p.setName("Rohit");
        p.setGender("");p.setAge(28);
        p.setSeatNumber("12A");p.setMealOption("VEG");

        dto.setPassengers(List.of(p));
        dto.setSeatNumbers(List.of("12A"));
        dto.setMealOption("");
        mockMvc.perform(post("/api/v1.0/flight/booking/" + flightId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testGetTicketByPnr_notFound() throws Exception {
        mockMvc.perform(get("/api/v1.0/flight/ticket/PNR1234"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBookingHistory_empty() throws Exception {
        mockMvc.perform(get("/api/v1.0/flight/booking/history/test@gmail.com"))
                .andExpect(status().isNotFound());
    }
    

    @Test
    void testCancelBooking_invalidPNR() throws Exception {
        mockMvc.perform(delete("/api/v1.0/flight/booking/cancel/PNR123"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testCancelBooking_noPNR() throws Exception {
        mockMvc.perform(delete("/api/v1.0/flight/booking/cancel/"))
                .andExpect(status().isNotFound());
    }
}
