package com.flightapp.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightapp.dto.SearchRequestDto;
import com.flightapp.entity.Flight;
import com.flightapp.entity.FlightInventory;
import com.flightapp.exception.FlightNotFoundException;
import com.flightapp.service.FlightInventoryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FlightSearchController.class)
class FlightSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightInventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void searchFlights_success() throws Exception {

        SearchRequestDto req = new SearchRequestDto();
        req.setFromPlace("Delhi");
        req.setToPlace("Mumbai");
        req.setJourneyDate(LocalDate.of(2025, 11, 25));
        req.setTripType("ONE_WAY");


        Flight flight = Flight.builder().flightNumber("6E-512").airlineName("IndiGo").fromPlace("Delhi").toPlace("Mumbai").build();
        
        FlightInventory result = FlightInventory.builder().id(1L).flight(flight).departureTime(LocalDateTime.of(2025, 11, 25, 14, 30))
                .arrivalTime(LocalDateTime.of(2025, 11, 25, 16, 20)).price(4500.0).totalSeats(180).availableSeats(180).build();

        Mockito.when(inventoryService.searchFlights(any(SearchRequestDto.class)))
                .thenReturn(List.of(result));

        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].flight.flightNumber").value("6E-512"))
                .andExpect(jsonPath("$[0].flight.airlineName").value("IndiGo"))
                .andExpect(jsonPath("$[0].flight.fromPlace").value("Delhi"))
                .andExpect(jsonPath("$[0].flight.toPlace").value("Mumbai"));
    }

    @Test
    void searchFlights_noResults_throwsFlightNotFoundException() throws Exception {

        SearchRequestDto req = new SearchRequestDto();
        req.setFromPlace("Nowhere");
        req.setToPlace("Elsewhere");
        req.setJourneyDate(LocalDate.of(2025, 11, 25));
        req.setTripType("ONE_WAY");

        Mockito.when(inventoryService.searchFlights(any(SearchRequestDto.class)))
                .thenThrow(new FlightNotFoundException("No flights found for given search criteria"));

        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No flights found for given search criteria"));
    }
}
