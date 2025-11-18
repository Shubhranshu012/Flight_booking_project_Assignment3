package com.flightapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.flightapp.dto.SearchRequestDto;
import com.flightapp.entity.FlightInventory;
import com.flightapp.service.FlightInventoryService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FlightSearchController {

    private final FlightInventoryService inventoryService;

    @PostMapping("/api/v1.0/flight/search")
    public ResponseEntity<Map<String, List<FlightInventory>>> search(@Valid @RequestBody SearchRequestDto dto) {
        return ResponseEntity.ok(inventoryService.searchFlights(dto));
    }
}
