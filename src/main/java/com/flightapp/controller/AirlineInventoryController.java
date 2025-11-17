package com.flightapp.controller;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.flightapp.dto.InventoryRequestDto;
import com.flightapp.service.FlightInventoryService;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AirlineInventoryController {

    private final FlightInventoryService inventoryService;

    @PostMapping("/api/v1.0/flight/airline/inventory/add")
    public ResponseEntity<Map<String, String>> addInventory(@Valid @RequestBody InventoryRequestDto dto) {
        inventoryService.addInventory(dto); 

        Map<String, String> response = Map.of(
                "message", "Inventory added successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}