package com.flightapp.exception;


public class AvaliableSeatMoreThanTotal extends RuntimeException {
    public AvaliableSeatMoreThanTotal(String message) {
        super(message);
    }
}