package com.flightapp.controller;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightapp.dto.BookingRequestDto;
import com.flightapp.dto.PassengerDto;
import com.flightapp.entity.Booking;
import com.flightapp.entity.Passenger;
import com.flightapp.exception.ExceptionDuetoTiming;
import com.flightapp.exception.NotFoundException;
import com.flightapp.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void bookTicket_success() throws Exception {
        BookingRequestDto req = new BookingRequestDto();
        req.setEmail("testuser@gmail.com");
        req.setNumberOfSeats(2);
        PassengerDto p1 = new PassengerDto(); 
        
        p1.setName("Rohit"); p1.setGender("M"); 
        p1.setAge(28); p1.setSeatNumber("12A"); 
        p1.setMealOption("VEG");
        
        PassengerDto p2 = new PassengerDto(); 
        
        p2.setName("Anita"); p2.setGender("F"); 
        p2.setAge(25); p2.setSeatNumber("12B"); 
        p2.setMealOption("NON_VEG");
        
        req.setPassengers(List.of(p1, p2));
        req.setSeatNumbers(List.of("12A","12B"));
        req.setMealOption("MIX");

        Booking saved = Booking.builder()
                .id(10L)
                .pnr("A1B2C3")
                .email(req.getEmail())
                .bookingTime(LocalDateTime.now())
                .journeyDateTime(LocalDateTime.of(2025,11,25,14,30))
                .totalPrice(9000.0)
                .fromPlace("Delhi")
                .toPlace("Mumbai")
                .passengers(List.of(
                        Passenger.builder().name("Rohit").gender("M").age(28).seatNumber("12A").mealOption("VEG").build(),
                        Passenger.builder().name("Anita").gender("F").age(25).seatNumber("12B").mealOption("NON_VEG").build()
                ))
                .build();

        Mockito.when(bookingService.bookTicket(eq(1L), any(BookingRequestDto.class)))
                .thenReturn(saved);

        mockMvc.perform(post("/api/v1.0/flight/booking/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnr").value("A1B2C3"))
                .andExpect(jsonPath("$.email").value("testuser@gmail.com"))
                .andExpect(jsonPath("$.passengers[0].name").value("Rohit"));
    }

    @Test
    void bookTicket_notEnoughSeats_returnsBadRequest() throws Exception {
        BookingRequestDto req = new BookingRequestDto();
        req.setEmail("t@gmail.com");
        req.setNumberOfSeats(999);
        req.setPassengers(List.of());

        Mockito.when(bookingService.bookTicket(eq(1L), any(BookingRequestDto.class)))
                .thenThrow(new RuntimeException("Not enough seats available"));

        mockMvc.perform(post("/api/v1.0/flight/booking/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTicketByPnr_success() throws Exception {
        Booking saved = Booking.builder()
                .id(10L)
                .pnr("A1B2C3")
                .email("testuser@gmail.com")
                .journeyDateTime(LocalDateTime.of(2025,11,25,14,30))
                .totalPrice(9000.0)
                .fromPlace("Delhi")
                .toPlace("Mumbai")
                .passengers(List.of(Passenger.builder().name("Rohit").build()))
                .build();

        Mockito.when(bookingService.getByPnr("A1B2C3")).thenReturn(saved);

        mockMvc.perform(get("/api/v1.0/flight/ticket/A1B2C3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnr").value("A1B2C3"))
                .andExpect(jsonPath("$.fromPlace").value("Delhi"));
    }

    @Test
    void getTicketByPnr_notFound() throws Exception {
        Mockito.when(bookingService.getByPnr("BADPNR"))
                .thenThrow(new NotFoundException("PNR not found"));

        mockMvc.perform(get("/api/v1.0/flight/ticket/BADPNR"))
        		.andExpect(status().isNotFound())   
                .andExpect(jsonPath("$.error").value("PNR not found"));
    }

    @Test
    void history_success() throws Exception {
        Booking b1 = Booking.builder()
                .id(10L).pnr("P1").fromPlace("Delhi").toPlace("Mumbai").bookingTime(LocalDateTime.now()).totalPrice(4500.0).build();

        Booking b2 = Booking.builder()
                .id(11L).pnr("P2").fromPlace("Delhi").toPlace("Bengaluru").bookingTime(LocalDateTime.now()).totalPrice(6000.0).build();

        Mockito.when(bookingService.history("testuser@gmail.com")).thenReturn(List.of(b1,b2));

        mockMvc.perform(get("/api/v1.0/flight/booking/history/testuser@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pnr").value("P1"))
                .andExpect(jsonPath("$[1].pnr").value("P2"));
    }

    @Test
    void cancelBooking_success() throws Exception {
        
        Mockito.doNothing().when(bookingService).cancelBooking("A1B2C3");

        mockMvc.perform(delete("/api/v1.0/flight/booking/cancel/A1B2C3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ticket cancelled successfully"));
    }

    @Test
    void cancelBooking_pnrNotFound_returnsBadRequest() throws Exception {
    	Mockito.doThrow(new NotFoundException("PNR not found"))
        .when(bookingService).cancelBooking("XYZ123");

    	mockMvc.perform(delete("/api/v1.0/flight/booking/cancel/XYZ123"))
	        .andExpect(status().isNotFound())
	        .andExpect(jsonPath("$.error").value("PNR not found"));
    }
    @Test
    void cancelBooking_within24hrs_returnsBadRequest() throws Exception {
        Mockito.doThrow(new ExceptionDuetoTiming("Cannot cancel within 24 hours of journey"))
                .when(bookingService).cancelBooking("A1B2C3");

        mockMvc.perform(delete("/api/v1.0/flight/booking/cancel/A1B2C3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot cancel within 24 hours of journey"));
    }
}