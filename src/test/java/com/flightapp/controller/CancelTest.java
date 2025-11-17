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
public class CancelTest {

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
    private Long flightId2;
    @BeforeEach
    void setup() {
        bookingRepo.deleteAll();
        inventoryRepo.deleteAll();
        flightRepo.deleteAll();

        Flight flight = flightRepo.save(Flight.builder().flightNumber("6E-512").airlineName("IndiGo").fromPlace("Delhi").toPlace("Mumbai").build());
        Flight flight2 = flightRepo.save(Flight.builder().flightNumber("6E-515").airlineName("IndiGo").fromPlace("Delhi").toPlace("Mumbai").build());
        FlightInventory inv = inventoryRepo.save(FlightInventory.builder().flight(flight).price(4500.0).totalSeats(180).availableSeats(180)
                .departureTime(LocalDateTime.now().plusDays(2)).arrivalTime(LocalDateTime.now().plusDays(2).plusHours(2)).active(true).build());
        FlightInventory inv2 = inventoryRepo.save(FlightInventory.builder().flight(flight2).price(4500.0).totalSeats(180).availableSeats(180)
                .departureTime(LocalDateTime.now()).arrivalTime(LocalDateTime.now().plusHours(2)).active(true).build());

        flightId = inv.getId();
        flightId2 = inv2.getId();
    }
	@Test
    void testBookTicket_success_and_cancel() throws Exception {

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

        String bookingResponse = mockMvc.perform(post("/api/v1.0/flight/booking/" + flightId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        String pnr = objectMapper.readTree(bookingResponse).get("pnr").asText();
        mockMvc.perform(delete("/api/v1.0/flight/booking/cancel/" + pnr)).andExpect(status().isOk());

    }
	@Test
    void testBookTicket_success_and_cancelTimeLimit() throws Exception {

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

        String bookingResponse = mockMvc.perform(post("/api/v1.0/flight/booking/" + flightId2)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        String pnr = objectMapper.readTree(bookingResponse).get("pnr").asText();
        mockMvc.perform(delete("/api/v1.0/flight/booking/cancel/" + pnr)).andExpect(status().isBadRequest());
    }
    @Test
    void testBookTicket_cancelThenTicket() throws Exception {

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


        String bookingResponse = mockMvc.perform(post("/api/v1.0/flight/booking/" + flightId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        String pnr = objectMapper.readTree(bookingResponse).get("pnr").asText();

        mockMvc.perform(delete("/api/v1.0/flight/booking/cancel/" + pnr)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1.0/flight/ticket/" + pnr)).andExpect(status().isNotFound());
    }
 
    @Test
    void testBookTicket_cancelWrongPnr() throws Exception {

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
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1.0/flight/ticket/" + "1234")).andExpect(status().isNotFound());
    }
    @Test
    void testBookTicket_ThenTicket() throws Exception {

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


        String bookingResponse = mockMvc.perform(post("/api/v1.0/flight/booking/" + flightId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        String pnr = objectMapper.readTree(bookingResponse).get("pnr").asText();
        mockMvc.perform(get("/api/v1.0/flight/ticket/" + pnr)).andExpect(status().isOk());
    }
    @Test
    void testBookTicket_ThenHistory() throws Exception {

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
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1.0/flight/booking/history/" + "test@gmail.com")).andExpect(status().isOk());
    }
    
}
