package com.example.demo.exception;

/** Base unchecked exception for application-specific failures. */
public abstract class ShortenerException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    protected ShortenerException(String message) { super(message); }
    protected ShortenerException(String message, Throwable cause) { super(message, cause); }
}
