package com.surya.customerledger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalValidationHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException exception) {
    final var errors = exception.getBindingResult().getAllErrors().stream().map((e) -> e != null ? e : "Invalid value");
    return ResponseEntity.status(400).body(Map.of("errors", errors));
  }
}
