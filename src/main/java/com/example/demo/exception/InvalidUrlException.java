package com.example.demo.exception;

public class InvalidUrlException extends ShortenerException {
    private static final long serialVersionUID = 1L;
    public InvalidUrlException(String message) { super(message); }
    public InvalidUrlException(String message, Throwable cause) { super(message, cause); }
}
