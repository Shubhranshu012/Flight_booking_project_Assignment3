package com.flightapp;

import java.util.HashMap;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.flightapp.exception.AvaliableSeatMoreThanTotal;
import com.flightapp.exception.ExceptionDueToSeat;
import com.flightapp.exception.ExceptionDuetoTiming;
import com.flightapp.exception.FlightAlreadyExist;
import com.flightapp.exception.FlightNotFoundException;
import com.flightapp.exception.NotFoundException;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleException(MethodArgumentNotValidException exception) {

        Map<String, String> errorMap = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach(error -> {

            String fieldName;

            if (error instanceof FieldError) {
                fieldName = ((FieldError) error).getField();
            } else {
                fieldName = error.getObjectName();  
            }

            String message = error.getDefaultMessage();
            errorMap.put(fieldName, message);
        });

        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST); 
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(NotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); 
    }
    @ExceptionHandler(ExceptionDueToSeat.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ExceptionDueToSeat ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); 
    }
    
    
    @ExceptionHandler(ExceptionDuetoTiming.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ExceptionDuetoTiming ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); 
    }
    
    @ExceptionHandler(FlightNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(FlightNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); 
    }
    
    @ExceptionHandler(AvaliableSeatMoreThanTotal.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(AvaliableSeatMoreThanTotal ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); 
    }
    
    @ExceptionHandler(FlightAlreadyExist.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(FlightAlreadyExist ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); 
    }
}