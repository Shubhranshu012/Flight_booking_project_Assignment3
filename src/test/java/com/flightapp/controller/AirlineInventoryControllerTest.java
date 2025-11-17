package com.flightapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightapp.dto.InventoryRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AirlineInventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.message").value("Inventory added successfully"));
    }

    @Test
    void addInventory_validationError_missingAirlineName() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setAirlineName("");

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.airlineName").exists());
    }

    @Test
    void addInventory_validationError_missingFromPlace() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setFromPlace("");

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fromPlace").exists());
    }

    @Test
    void addInventory_validationError_missingToPlace() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setToPlace("");

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.toPlace").exists());
    }

    @Test
    void addInventory_validationError_dateInPast() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setDepartureTime(LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.departureTime").exists());
    }

    @Test
    void addInventory_validationError_priceNegative() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setPrice(-4500.0);

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.price").exists());
    }

    @Test
    void addInventory_validationError_totalSeatsNegative() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setTotalSeats(-100);

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.totalSeats").exists());
    }

    @Test
    void addInventory_validationError_availableSeatsNegative() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setAvailableSeats(-10);

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.availableSeats").exists());
    }

    @Test
    void addInventory_validationError_availableSeatsGreaterThanTotal() throws Exception {
        InventoryRequestDto dto = buildValidDto();
        dto.setAvailableSeats(300);  

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").value("Available seats cannot be greater than total seats"));
    }
}
