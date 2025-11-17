package com.flightapp.exception;


public class FlightAlreadyExist extends RuntimeException {
    public FlightAlreadyExist(String message) {
        super(message);
    }
}