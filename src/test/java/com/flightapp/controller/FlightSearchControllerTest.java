package com.flightapp.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightapp.dto.InventoryRequestDto;
import com.flightapp.dto.SearchRequestDto;

import com.flightapp.entity.FlightInventory;
import com.flightapp.exception.AvaliableSeatMoreThanTotal;
import com.flightapp.exception.ExceptionDuetoTiming;
import com.flightapp.exception.FlightAlreadyExist;
import com.flightapp.exception.FlightNotFoundException;
import com.flightapp.service.FlightInventoryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FlightSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightInventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private InventoryRequestDto buildValidDto() {
        InventoryRequestDto dto = new InventoryRequestDto();
        dto.setAirlineName("IndiGo");
        dto.setAirlineLogo("https://indigo/logo.png");
        dto.setFromPlace("Delhi");
        dto.setToPlace("Mumbai");
        dto.setFlightNumber("6E-512");
        dto.setDepartureTime(LocalDateTime.now().plusDays(1));
        dto.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
        dto.setPrice(4500.0);
        dto.setTotalSeats(180);
        dto.setAvailableSeats(180);
        return dto;
    }
    @Test
    void addInventory_success() throws Exception {
        InventoryRequestDto dto = buildValidDto();

        Mockito.when(inventoryService.addInventory(any())).thenReturn(new FlightInventory());

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());  
    }
    @Test
    void addInventory_validationError_missingAirlineName() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setAirlineName("");

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addInventory_validationError_availableSeatsGreaterThanTotal() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setAvailableSeats(500);

        Mockito.doThrow(new AvaliableSeatMoreThanTotal("Available seats cannot be greater than total seats"))
                .when(inventoryService).addInventory(any());

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addInventory_invalidTiming() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setArrivalTime(dto.getDepartureTime().minusHours(5));

        Mockito.doThrow(new ExceptionDuetoTiming("Arrival time cannot be before departure time"))
                .when(inventoryService).addInventory(any());

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void addInventory_duplicateFlight() throws Exception {
        InventoryRequestDto dto = buildValidDto();

        Mockito.doThrow(new FlightAlreadyExist("Flight already exists")).when(inventoryService).addInventory(any());

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchFlight_success() throws Exception {
        SearchRequestDto dto = new SearchRequestDto();
        dto.setFromPlace("Delhi");
        dto.setToPlace("Mumbai");
        dto.setJourneyDate(LocalDate.now().plusDays(1));
        dto.setTripType("ONE_WAY");

        FlightInventory mockInventory = new FlightInventory();
        mockInventory.setDepartureTime(LocalDateTime.now().plusDays(1));


        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
    
    @Test
    void searchFlight_Failure() throws Exception {
        SearchRequestDto dto = new SearchRequestDto();
        dto.setFromPlace("Delhi");
        dto.setToPlace("");
        dto.setJourneyDate(LocalDate.now().plusDays(1));
        dto.setTripType("ONE_WAY");

        FlightInventory mockInventory = new FlightInventory();
        mockInventory.setDepartureTime(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void searchFlight_FailureTime() throws Exception {
        SearchRequestDto dto = new SearchRequestDto();
        dto.setFromPlace("Delhi");
        dto.setToPlace("Bhubaneswar");
        dto.setTripType("ROUND_TRIP");

        FlightInventory mockInventory = new FlightInventory();
        mockInventory.setDepartureTime(LocalDateTime.now().plusDays(1)); 

        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void searchFlight_FailureRoundStart() throws Exception {
        SearchRequestDto dto = new SearchRequestDto();
        dto.setFromPlace("");
        dto.setToPlace("Bhubaneswar");
        dto.setJourneyDate(LocalDate.now().plusDays(1));
        dto.setTripType("ROUND_TRIP");

        FlightInventory mockInventory = new FlightInventory();
        mockInventory.setDepartureTime(LocalDateTime.now().plusDays(1));

        

        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void searchFlight_missingField() throws Exception {
        SearchRequestDto dto = new SearchRequestDto();
        dto.setFromPlace(""); 
        dto.setToPlace("Mumbai");
        dto.setJourneyDate(LocalDate.now().plusDays(1));
        dto.setTripType("ONE_WAY");

        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void searchFlight_missingTrip() throws Exception {
        SearchRequestDto dto = new SearchRequestDto();
        dto.setFromPlace("Delhi"); 
        dto.setToPlace("Mumbai");
        dto.setJourneyDate(LocalDate.now().plusDays(1));
        dto.setTripType("");

        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void addInventory_negativePrice() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setPrice(-100.0);

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addInventory_nullDepartureTime() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setDepartureTime(null);

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void addInventory_nullFlightNumber() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setFlightNumber(null);

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
