package com.surya.customerledger.exceptions;

public class InvalidReferenceException extends RuntimeException {
  private final String field;

  public InvalidReferenceException(String field, String message) {
    super(message);
    this.field = field;
  }

  public String getField() {
    return field;
  }
}
