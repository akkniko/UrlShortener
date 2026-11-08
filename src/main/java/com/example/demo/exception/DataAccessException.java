package com.example.demo.exception;

public class DataAccessException extends ShortenerException {
    private static final long serialVersionUID = 1L;
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
