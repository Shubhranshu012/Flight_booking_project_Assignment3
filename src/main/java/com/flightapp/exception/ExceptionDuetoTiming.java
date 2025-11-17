package com.flightapp.exception;


public class ExceptionDuetoTiming extends RuntimeException {
    public ExceptionDuetoTiming(String message) {
        super(message);
    }
}