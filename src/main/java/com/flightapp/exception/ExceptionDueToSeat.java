package com.flightapp.exception;



public class ExceptionDueToSeat extends RuntimeException {
    public ExceptionDueToSeat(String message) {
        super(message);
    }
}